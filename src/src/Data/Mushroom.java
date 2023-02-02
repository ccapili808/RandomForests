package Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mushroom {
    private int id;
    private final List<String> features;
    private Map<String, String> featureMap = new HashMap<>();

    /*
    * Constructor for Data.Mushroom class
    * @param features List of features
    * @param featureValues List of feature values for each feature
    * This constructor stores each feature to a value in a hashmap
     */
    public Mushroom(List<String> features, String featureValues) {
        this.features = features;
        for (String feature: features) {
            if(feature.equals("id")) {
                id = Integer.parseInt(featureValues);
            }
            featureMap.put(feature, featureValues);
        }
    }
    public void addFeature(String feature, String value) {
        featureMap.put(feature, value);
    }

    public String getFeatureValue(String feature) {
        return featureMap.get(feature);
    }


    public void printMushroom() {
        System.out.println("MUSHROOM ID: " + id);
        for (String feature: features) {
            System.out.println(feature + ": " + featureMap.get(feature));
        }
        System.out.println();
    }


}
