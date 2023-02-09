package Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mushroom {
    private int id;
    private String mushroomClass;
    private final List<String> features;
    private Map<String, String> featureMap = new HashMap<>();

    /*
    * Constructor for Data.Mushroom class
    * @param features List of features
    * @param featureValues List of feature values for each feature
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

    public int getId() {
        return id;
    }

    public String getMushroomClass() {
        return mushroomClass;
    }


    public void addFeature(String feature, String value) {
        featureMap.put(feature, value);
    }

    public String getFeatureValue(String feature) {
        return featureMap.get(feature);
    }


    public void printMushroom() {
        System.out.println("MUSHROOM ID: " + id + " Class: " + mushroomClass);
        for (String feature: features) {
            System.out.println(feature + ": " + featureMap.get(feature));
        }
        System.out.println();
    }


}
