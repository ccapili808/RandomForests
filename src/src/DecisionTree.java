import Data.Features;
import Data.Mushroom;
import javafx.scene.Node;

import java.util.*;

public class DecisionTree {
    private double entropy;
    private final Features features;
    private final List<Mushroom> mushrooms;
    private int numFeatures;
    private int numMushrooms;
    private int numberOfClasses = 2;
    /*
    * Constructor for DecisionTree class
    * @param features List of features
    * @param featureValues List of feature values for each feature
    * This constructor stores each feature to a value in a hashmap
     */
    public DecisionTree(Features features, List<Mushroom> mushrooms)
    {
        this.features = features;
        this.mushrooms = mushrooms;
        this.numMushrooms = mushrooms.size();
        buildTree();
    }

    //Implement the decision tree algorithm
    public void buildTree() {
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

    }

    public static Map<String, Integer> getClassCounts(List<Mushroom> mushrooms) {
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

    public double informationGain(String feature) {
        return entropy - featureEntropy(feature);
    }

    public double featureEntropy(String feature) {
        Map<String, Integer> featureValues = getAllFeatureValues(mushrooms, feature);
        double featureEntropy = 0;
        //iterate through each mushroom and calculate the entropy for each feature
        int total = 0;
        for (String featureValue: featureValues.keySet()) {
            double valueEntropy = lookupEncoding(feature, featureValue);
            featureEntropy += valueEntropy;
            total += featureValues.get(featureValue);
        }
        return featureEntropy;
    }

    public double lookupEncoding(String feature, String value) {
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
            System.out.println(mushroomClass + ": " + classCounts.get(mushroomClass));
            total += classCounts.get(mushroomClass);
        }
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



}
