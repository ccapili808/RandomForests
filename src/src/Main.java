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
        features.loadFeatures("C:\\Users\\gkour\\Documents\\CS429\\RandomForests2\\src\\src\\Resources\\agaricus-lepiota - training.csv");
        buildDataSet(features);
        buildTestingSet(features);
        buildChiTable();
        splitMushroomSet(mushrooms);
        for (int i = 0; i<101 ; i++) {
            DecisionTree decisionTree = new DecisionTree(createFeatureSubset(features.getFeatures()),
                    createMushroomSubset(mushrooms), "gini", chiTable, 0.9);
            randomForest.add(decisionTree);
        }
        int correct = 0;
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
        double accuracy = ((double) (correct)) / ((double) validationSet.size());
        System.out.println("Random Forest Accuracy: " + accuracy);
        for (Mushroom mushroom: testingSet) {
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
            System.out.println(mushroom.getId() + "," + mushroomClass);
        }

    }

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
    public static void splitMushroomSet(List<Mushroom> mushrooms) {
        int size = (int) (0.7 * mushrooms.size());
        Random rand = new Random();
        for (int i = 0; i < size; i++) {
            trainingSet.add(mushrooms.remove(rand.nextInt(mushrooms.size())));
        }
        validationSet = mushrooms;
    }

    public static List<Mushroom> createMushroomSubset (List<Mushroom> mushrooms) {
        List<Mushroom> subset = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < 1000; i++) {
            subset.add(mushrooms.get(rand.nextInt(mushrooms.size())));
        }
        return subset;
    }

    public static List<String> createFeatureSubset (List<String> features) {
        List<String> subset = new ArrayList<>();
        Random rand = new Random();
        for (int i=0; i<4; i++) {
            int index = rand.nextInt(features.size());
            while (subset.contains(features.get(index))) {
                index = rand.nextInt(features.size());
            }
            subset.add(features.get(index));
        }
        return subset;
    }

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
            alphaValues.put(0.10, Double.parseDouble(line[6]));
            alphaValues.put(0.05, Double.parseDouble(line[7]));
            alphaValues.put(0.025, Double.parseDouble(line[8]));
            alphaValues.put(0.01, Double.parseDouble(line[9]));
            alphaValues.put(0.005, Double.parseDouble(line[10]));
            chiTable.put(Integer.parseInt(line[0]), alphaValues);
        }
    }

    public static List<String> getFeatures() {
        return features.getFeatures();
    }
}