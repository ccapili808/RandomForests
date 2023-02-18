import Data.Features;
import Data.Mushroom;
import javafx.scene.Node;

import java.io.InputStreamReader;
import java.util.*;

public class DecisionTree {
    private double entropy;
    //private final Features features;
    private List<String> features;
    private final List<Mushroom> mushrooms;
    private int numFeatures;
    private int numMushrooms;
    private int numberOfClasses = 2;
    private double alpha;
    private List<DecisionNode> treeNodes = new ArrayList<>();
    private Map<Integer, Map<Double,Double>> chiTable;
    /*
    * Constructor for DecisionTree class
    * @param features List of features
    * @param featureValues List of feature values for each feature
    * This constructor stores each feature to a value in a hashmap
     */
    public DecisionTree(List<String> features, List<Mushroom> mushrooms, String type,
                        Map<Integer,Map <Double,Double>> chiTable, double alpha)
    {
        this.features = features;
        this.mushrooms = mushrooms;
        this.numMushrooms = mushrooms.size();
        this.chiTable = chiTable;
        this.alpha = alpha;
        buildTree(type);
        //predict();
    }

    //Implement the decision tree algorithm
    public void buildTree(String type) {
        DecisionNode root = new DecisionNode(mushrooms, features, this, type);
        /*
        this.entropy = entropy(mushrooms);
        System.out.println(entropy);
        Map<String, Double> featureInfoGain = new HashMap<>();
        for (String feature: features.getFeatures()) {
            featureInfoGain.put(feature, informationGain(feature));
        }
        System.out.println(featureInfoGain);
        Map <String, Integer> map = getAllFeatureValues(mushrooms, "veil-type");
        //print the highest information gain
        System.out.println(Collections.max(featureInfoGain.entrySet(), Map.Entry.comparingByValue()).getKey());
        //print the highest information gain value
        System.out.println(Collections.max(featureInfoGain.entrySet(), Map.Entry.comparingByValue()).getValue());
        */
    }
/*
    //predict the class of mushrooms from the csv
    public String predict() {
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
            return traverseNode(treeNodes.get(0),mushroom);
        }
    }
*/
    public String predictMushroom(Mushroom mushroom) {
        return traverseNode(treeNodes.get(0), mushroom);
    }

    //traverse a node's children to find the correct path
    public String traverseNode(DecisionNode node, Mushroom mushroom) {
        if (node.isLeaf()) {
            return node.getMushroomClass();
        }
        else {
            for (DecisionNode decisionNode : node.getChildNodes()) {
                if (mushroom.getFeatureValue(node.getQuestion()).equals(decisionNode.getFeatureValue())) {
                    DecisionNode nextNode = decisionNode;
                    if (nextNode.isLeaf()) {
                        return nextNode.getMushroomClass();
                        //System.out.println(mushroom.getId() + "," + nextNode.getMushroomClass());
                    } else {
                        return traverseNode(nextNode, mushroom);
                    }
                }
            }
        }
        return node.getMushroomClass();
    }

    public Map<String, Integer> getClassCounts(List<Mushroom> mushrooms) {
        Map<String, Integer> classCounts = new HashMap<>();
        for (Mushroom mushroom: mushrooms) {
            String mushroomClass = mushroom.getMushroomClass();
            if (classCounts.containsKey(mushroomClass)) {
                classCounts.put(mushroomClass, classCounts.get(mushroomClass) + 1);
            } else {
                classCounts.put(mushroomClass, 1);
            }
        }
        return classCounts;
    }


    public double entropy(List<Mushroom> mushrooms) {
        Map<String, Integer> classes = getClassCounts(mushrooms);
        double entropy = 0;
        for (String mushroomClass: classes.keySet()) {
            double probability = (double) classes.get(mushroomClass) / mushrooms.size();
            //shannon entropy formula using log base 2
            double  log =  probability * Math.log(probability) / Math.log(2);
            entropy += log;
        }
        return -entropy;
    }

    public double impurity (List<Mushroom> mushrooms, String type) {
        Map<String, Integer> classes = getClassCounts(mushrooms);
        if (type.equals("entropy")) {
            double entropy = 0;
            for (String mushroomClass: classes.keySet()) {
                double probability = (double) classes.get(mushroomClass) / mushrooms.size();
                //shannon entropy formula using log base 2
                double  log =  probability * Math.log(probability) / Math.log(2);
                entropy += log;
            }
            return -entropy;
        }
        else if(type.equals("gini")) {
            double gini = 0;
            for (String mushroomClass: classes.keySet()) {
                double probability = (double) classes.get(mushroomClass) / mushrooms.size();
                gini += probability*probability;
            }
            return (1-gini);
        }
        else if(type.equals("me")) {
            double me = 0;
            for (String mushroomClass: classes.keySet()) {
                double probability = (double) classes.get(mushroomClass) / mushrooms.size();
                if (probability>me) {
                    me = probability;
                }
            }
            return (1-me);
        }
        return 0;
    }


    public double informationGain(List<Mushroom> mushrooms, String feature, String type) {
        return  impurity(mushrooms, type) - featureEntropy(feature, type);
    }

    public double featureEntropy(String feature, String type) {
        Map<String, Integer> featureValues = getAllFeatureValues(mushrooms, feature);
        double featureImpurity = 0;
        //iterate through each mushroom and calculate the entropy for each feature
        int total = 0;
        for (String featureValue: featureValues.keySet()) {
            double valueImpurity = lookupEncoding(feature, featureValue, type);
            featureImpurity += valueImpurity;
            total += featureValues.get(featureValue);
        }
        return featureImpurity;
    }

    public double lookupEncoding(String feature, String value, String type) {
        Map<String, Integer> classCounts = new HashMap<>();
        int total = 0;
        for (Mushroom mushroom : mushrooms) {
            if (mushroom.getFeatureValue(feature).equals(value)) {
                //Add to class counts
                if (classCounts.containsKey(mushroom.getMushroomClass())) {
                    classCounts.put(mushroom.getMushroomClass(), classCounts.get(mushroom.getMushroomClass()) + 1);
                } else {
                    classCounts.put(mushroom.getMushroomClass(), 1);
                }
            }
        }
        //Print out class counts
        for (String mushroomClass : classCounts.keySet()) {
            //System.out.println(mushroomClass + ": " + classCounts.get(mushroomClass));
            total += classCounts.get(mushroomClass);
        }
        if (type.equals("entropy")) {
            //get the entropy for this feature
            double probability = total / (double) mushrooms.size();
            double entropy = 0;
            for (String mushroomClass : classCounts.keySet()) {
                double classProbability = classCounts.get(mushroomClass) / (double) total;
                double log = classProbability * Math.log(classProbability) / Math.log(2);
                entropy += log;
            }
            return -entropy * probability;
        }
        else if (type.equals("gini")) {
            double probability = total / (double) mushrooms.size();
            double gini = 1;
            for (String mushroomClass : classCounts.keySet()) {
                double classProbability = classCounts.get(mushroomClass) / (double) total;
                double square = classProbability * classProbability;
                gini += square;
            }
            return ((1 - gini) * probability);
        }
        else if (type.equals("me")) {
            double probability = total / (double) mushrooms.size();
            double me = 1;
            double max = 0;
            for (String mushroomClass : classCounts.keySet()) {
                double classProbability = classCounts.get(mushroomClass) / (double) total;
                if (classProbability>max) {
                    max = classProbability;
                }
            }
            return ((1 - max) * probability);
        }
        return 0;
    }

    public Map<String, Integer> getAllFeatureValues(List<Mushroom> mushrooms, String feature) {
        Map<String, Integer> featureValues = new HashMap<>();
        //count the unique values for each feature
        for (Mushroom mushroom: mushrooms) {
            String featureValue = mushroom.getFeatureValue(feature);
            if (featureValues.containsKey(featureValue)) {
                featureValues.put(featureValue, featureValues.get(featureValue) + 1);
            } else {
                featureValues.put(featureValue, 1);
            }
        }
        return featureValues;
    }


    public  Map<String, Set<String>> getUniqueValues(Features features) {
        //for each feature show all of the possible values for that feature
        Map<String, Set<String>> uniqueValues = new HashMap<>();
        for (String feature: features.getFeatures()) {
            //Get all of the values for the feature
            Set<String> values = new HashSet<>();
            for (Mushroom mushroom: mushrooms) {
                values.add(mushroom.getFeatureValue(feature));
            }
            uniqueValues.put(feature, values);
        }
        return uniqueValues;
    }

    public void addNode(DecisionNode decisionNode) {
        treeNodes.add(decisionNode);
    }

    public boolean chiPassed (int degrees, double testValue) {
        if (testValue>chiTable.get(degrees).get(alpha)) {
            return true;
        }
        return false;
    }


}
