import Data.Features;
import Data.Mushroom;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Encoder {

    public Encoder() {}

    /*

     */
    public Map<String, Map<String, Integer>> numericEncoder(
            Features features, List<Mushroom> mushrooms) {
        Map<String, Map<String, Integer>> encoding = new HashMap<>();
        for(Mushroom mushroom: mushrooms) {
            for(String feature: features.getFeatures()) {
                String value = mushroom.getFeatureValue(feature);
                //give each value a unique integer
                if(!encoding.containsKey(feature)) {
                    encoding.put(feature, new HashMap<>());
                }
                if(!encoding.get(feature).containsKey(value)) {
                    encoding.get(feature).put(value, encoding.get(feature).size());
                }
            }
        }
        //print encoding keyset
        for(String feature: encoding.keySet()) {
            System.out.println(feature);
            for(String value: encoding.get(feature).keySet()) {
                System.out.println(value + " " + encoding.get(feature).get(value));
            }
        }
        return encoding;
    }

    public static Map<String, Map<String, Integer>> oneHotEncoder(
            Features features, List<Mushroom> mushrooms) {
        Map<String, Map<String, Integer>> encoding = new HashMap<>();
        //code here

        return encoding;
    }

    public static Map<String, Map<String, Integer>> ordinalEncoder(
            Features features, List<Mushroom> mushrooms) {
        Map<String, Map<String, Integer>> encoding = new HashMap<>();
        //code here

        return encoding;
    }
}
