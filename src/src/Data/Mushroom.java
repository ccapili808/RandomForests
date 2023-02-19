package Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mushroom {
    private int id;                                               //Mushroom id
    private String mushroomClass;                               //Edible or poisonous
    private final List<String> features;                       //List of features
    private Map<String, String> featureMap = new HashMap<>(); //Stores the value of each feature for a single mushroom

    /**
    * @param features List of features
    * @param featureValues List of feature values for each feature
    * @param id mushroom id
    * @param mushroomClass mushroom class (edible or poisonous)
    * This constructor stores each feature to a value in a hashmap
     */
    public Mushroom(List<String> features, String featureValues, int id, String mushroomClass) {
        this.features = features;
        for (String feature: features) {
            featureMap.put(feature, featureValues);
        }
        this.id = id;
        this.mushroomClass = mushroomClass;
    }

    /**
     * Constructor for testing mushrooms with no class provided
     * @param features list of features
     * @param featureValues List of feature values for each feature
     * @param id mushroom id
     */
    public Mushroom(List<String> features, String featureValues, int id) {
        this.features = features;
        for (String feature: features) {
            featureMap.put(feature, featureValues);
        }
        this.id = id;
    }


    /**
     * Returns the mushroom id
     * @return mushroom id
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the mushroom class (edible or poisonous)
     * @return mushroom class
     */
    public String getMushroomClass() {
        return mushroomClass;
    }

    /**
     * Adds a feature to the map
     */
    public void addFeature(String feature, String value) {
        featureMap.put(feature, value);
    }

    /**
     * Returns the value of a feature
     * @param feature feature to get value of
     * @return value of feature
     * Example usage: getFeatureValue("cap-shape") --> "x"
     */
    public String getFeatureValue(String feature) {
        return featureMap.get(feature);
    }


    /**
     * Prints the mushroom id, class, and all features
     * Example output:
     * MUSHROOM ID: 1 Class: p
     * cap-shape: x
     * cap-surface: s
     * ..... etc .....
     */
    public void printMushroom() {
        System.out.println("MUSHROOM ID: " + id + " Class: " + mushroomClass);
        for (String feature: features) {
            System.out.println(feature + ": " + featureMap.get(feature));
        }
        System.out.println();
    }


    /**
     * Returns the feature map
     * @return feature map
     * The feature map is a hashmap that stores the value of each feature for a single mushroom
     */
    public Map<String, String> getFeatureMap() {
        return featureMap;
    }

}
