import Data.Features;
import Data.Mushroom;
import java.io.File;
import java.io.*;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {
    private static final List<Mushroom> mushrooms = new ArrayList<>();
    private static Map<Integer, Map<Double, Double>> chiTable = new HashMap<>();
    private static List<DecisionTree> randomForest = new ArrayList<>();
    private static List<Mushroom> trainingSet = new ArrayList<>();
    private static List<Mushroom> validationSet = new ArrayList<>();
    private static List<Mushroom> testingSet = new ArrayList<>();
    private static Features features;
    public static void main(String[] args) {
        features = new Features();
        features.loadFeatures();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter impurity type (\"entropy\", \"gini\", \"me\")");
        String impurityType = scanner.nextLine();
        while (!impurityType.equals("entropy") && !impurityType.equals("gini") && !impurityType.equals("me")) {
            System.out.println("Wrong input. Enter impurity type (\"entropy\", \"gini\", \"me\")");
            impurityType = scanner.nextLine();
        }
        double alpha = 0;
        System.out.println("Enter alpha value (\"0.995\", \"0.99\", \"0.975\", \"0.95\", \"0.90\", \"0.75\"," +
                " \"0.5\", \"0.25\", \"0.10\", \"0.05\", \"0.025\", \"0.01\", \"0.005\", \"0.002\", \"0.001\")");
        alpha = Double.parseDouble(scanner.nextLine());
        while (alpha != 0.995 && alpha != 0.99 && alpha != 0.975 && alpha != 0.95 && alpha != 0.90 && alpha != 0.75
                && alpha != 0.5 && alpha != 0.25 && alpha != 0.10 && alpha != 0.05 && alpha != 0.025
                && alpha != 0.01 && alpha != 0.005 && alpha != 0.002 && alpha != 0.001) {
            System.out.println("Wrong input. Enter alpha value (\"0.995\", \"0.99\", \"0.975\", \"0.95\", \"0.90\", \"0.75\", \"0.5\"," +
                    " \"0.25\", \"0.10\", \"0.05\", \"0.025\", \"0.01\", \"0.005\", \"0.002\", \"0.001\")");
            alpha = Double.parseDouble(scanner.nextLine());
        }

        buildDataSet(features);
        buildTestingSet(features);
        buildChiTable();
        splitMushroomSet(mushrooms);
        buildRandomForest(impurityType, alpha);
        predict();

    }

    /**
     * This method predicts the class of the mushrooms in the testing set
     */
    private static void predict() {
        //for each mushroom get the majority from the random forest
        for (Mushroom mushroom: testingSet) {
            int pVotes = 0;
            int eVotes = 0;
            //get a vote from each tree
            for(DecisionTree decisionTree: randomForest) {
                switch (decisionTree.predictMushroom(mushroom)) {
                    case "p":
                        pVotes += 1;
                        break;
                    default:
                        eVotes += 1;
                }
            }
            String mushroomClass;
            if (pVotes>eVotes) {
                mushroomClass = "p";
            }
            else {
                mushroomClass = "e";
            }
            //System.out.println(mushroom.getId() + "," + mushroomClass);
        }
    }


    /**
     * This method builds a random forest with the training set using the given variables alpha and impurity type.
     * @param impurityType the impurity type (entropy, gini, me)
     * @param alpha the alpha value
     */
    private static void buildRandomForest(String impurityType, double alpha) {
        //loop and create i trees
        int maxDepth = 0;
        double avgDepth = 0;
        double avgAccuracy = 0;
        for (int i = 0; i<51 ; i++) {
            DecisionTree decisionTree = new DecisionTree(createFeatureSubset(features.getFeatures()),
                    createMushroomSubset(trainingSet), impurityType, chiTable, alpha);
            randomForest.add(decisionTree);
            if (decisionTree.getMaxDepth()>maxDepth) {
                maxDepth = decisionTree.getMaxDepth();
            }
            avgDepth += decisionTree.getMaxDepth();
            //System.out.println("Max depth: " + decisionTree.getMaxDepth());
        }
        System.out.println("Forest max depth: " + maxDepth);
        avgDepth = avgDepth/51;
        System.out.println("Average tree depth: " + avgDepth);
        int correct = 0;
        Map<DecisionTree,Integer> treeAccuracyMap = new HashMap<>();
        //test with validation set
        for (Mushroom mushroom: validationSet) {
            int pVotes = 0;
            int eVotes = 0;
            for(DecisionTree decisionTree: randomForest) {
                switch (decisionTree.predictMushroom(mushroom)) {
                    case "p":
                        pVotes += 1;
                        break;
                    default:
                        eVotes += 1;
                }
            }
            String mushroomClass;
            if (pVotes>eVotes) {
                mushroomClass = "p";
            }
            else {
                mushroomClass = "e";
            }
            if (mushroom.getMushroomClass().equals(mushroomClass)) {
                correct++;
            }
        }
        //get accuracy per tree
        for (DecisionTree decisionTree: randomForest) {
            int treeCorrect = 0;
            for (Mushroom mushroom:validationSet) {
                if (mushroom.getMushroomClass().equals(decisionTree.predictMushroom(mushroom))) {
                    treeCorrect++;
                }
            }
            avgAccuracy += ( (double) (treeCorrect)) /( (double) validationSet.size());
        }

        //print forest accuracy
        double accuracy = ((double) (correct)) / ((double) validationSet.size());
        avgAccuracy = avgAccuracy / 51;
        System.out.println("Average Tree Accuracy: " + avgAccuracy);
        System.out.println("Random Forest Accuracy: " + accuracy);
    }

    /**
     * This method build the mushroom data set from the training csv file.
     * @param features The Features object to add mushroom features to
     */
    public static void buildDataSet(Features features) {
        //Read each line after the first line
        Scanner sc = null;
        sc = new Scanner(new InputStreamReader(Main.class.getResourceAsStream("agaricus-lepiota - training.csv")));
        //Skip first line
        sc.nextLine();
        while (sc.hasNextLine()) {
            String[] line = sc.nextLine().split(",");
            String mushroomClass = line[1];
            int id = Integer.parseInt(line[0]);
            Mushroom mushroom = new Mushroom(features.getFeatures(), line[0], id, mushroomClass);
            for (int i = 2; i < line.length; i++) {
                mushroom.addFeature(features.getFeatures().get(i - 2), line[i]);
            }
            mushrooms.add(mushroom);
        }
    }


    /**
     * This method loads in the testing set from the csv file and make it into a mushroom list
     * @param features The Features object to add mushroom features to
     */
    public static void buildTestingSet(Features features) {
        //Read each line after the first line
        Scanner sc = null;
        sc = new Scanner(new InputStreamReader(Main.class.getResourceAsStream("agaricus-lepiota - testing.csv")));
        //Skip first line
        sc.nextLine();
        while (sc.hasNextLine()) {
            String[] line = sc.nextLine().split(",");
            int id = Integer.parseInt(line[0]);
            Mushroom mushroom = new Mushroom(Main.getFeatures(), line[0], id);
            for (int i = 1; i < line.length; i++) {
                mushroom.addFeature(Main.getFeatures().get(i - 1), line[i]);
            }
            testingSet.add(mushroom);
        }
    }


    /**
     * This method splits the training set into training and validation sets
     * @param mushrooms The mushroom list to split
     */
    public static void splitMushroomSet(List<Mushroom> mushrooms) {
        //split mushroom dataset into training and validation sets
        int size = (int) (0.8 * mushrooms.size());
        Random rand = new Random();
        for (int i = 0; i < size; i++) {
            trainingSet.add(mushrooms.remove(rand.nextInt(mushrooms.size())));
        }
        validationSet = mushrooms;
    }


    /**
     * This method generates a new random mushroom list for the random forest decision trees.
     * The new list contains the same amount of mushrooms as the original set, but randomly selected.
     * @param mushrooms The mushroom list to use
     * @return The random mushroom list
     */
    public static List<Mushroom> createMushroomSubset (List<Mushroom> mushrooms) {
        List<Mushroom> subset = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < mushrooms.size(); i++) {
            subset.add(mushrooms.get(rand.nextInt(mushrooms.size())));
        }
        return subset;
    }

    /**
     * This method generates a new random feature list for the random forest decision trees
     * @param features The list of all mushroom features
     * @return The new random list of features
     */
    public static List<String> createFeatureSubset (List<String> features) {
        List<String> subset = new ArrayList<>();
        Random rand = new Random();
        //get i number of unique features
        for (int i=0; i<7; i++) {
            int index = rand.nextInt(features.size());
            while (subset.contains(features.get(index))) {
                index = rand.nextInt(features.size());
            }
            subset.add(features.get(index));
        }
        return subset;
    }

    /**
     * This method builds the chi square value map from the txt containing the table values.
     */
    public static void buildChiTable() {
        Scanner sc = new Scanner(new InputStreamReader(Main.class.getResourceAsStream("chitable.txt")));
        while (sc.hasNextLine()) {
            String[] line = sc.nextLine().split(" ");
            Map<Double, Double> alphaValues = new HashMap<>();
            alphaValues.put(0.995, Double.parseDouble(line[1]));
            alphaValues.put(0.99, Double.parseDouble(line[2]));
            alphaValues.put(0.975, Double.parseDouble(line[3]));
            alphaValues.put(0.95, Double.parseDouble(line[4]));
            alphaValues.put(0.90, Double.parseDouble(line[5]));
            alphaValues.put(0.75, Double.parseDouble(line[6]));
            alphaValues.put(0.5, Double.parseDouble(line[7]));
            alphaValues.put(0.25, Double.parseDouble(line[8]));
            alphaValues.put(0.10, Double.parseDouble(line[9]));
            alphaValues.put(0.05, Double.parseDouble(line[10]));
            alphaValues.put(0.025, Double.parseDouble(line[11]));
            alphaValues.put(0.01, Double.parseDouble(line[12]));
            alphaValues.put(0.005, Double.parseDouble(line[13]));
            alphaValues.put(0.002, Double.parseDouble(line[14]));
            alphaValues.put(0.001, Double.parseDouble(line[15]));
            chiTable.put(Integer.parseInt(line[0]), alphaValues);
        }
    }

    /**
     * This method gets the list of all mushroom features.
     * @return
     */
    public static List<String> getFeatures() {
        return features.getFeatures();
    }
}