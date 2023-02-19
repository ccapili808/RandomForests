package Data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Features {
    public List<String> getFeatures() {
        return features;
    }
    private final List<String> features = new ArrayList<>();
    private int featureCount = 0;

    /**
     * Empty Constructor
     */
    public Features(){}

    /**
     * Loads features from CSV file
     * NOTE: 'id and class' are not included in the list of features
     * @return - List of features
     */
    public List<String> loadFeatures() {
        //parsing a CSV file into Scanner class constructor
        Scanner sc = null;
        sc = new Scanner(new InputStreamReader(getClass().getResourceAsStream("/agaricus-lepiota - training.csv")));
        //Read first line and comma seperate
        String[] firstLine = sc.nextLine().split(",");
        //Add all features to list
        for (String s : firstLine) {
            if (s.equals("id")) continue;
            if (s.equals("class")) continue;
            featureCount++;
            features.add(s);
        }
        return features;
    }

    /**
     * Returns the number of features
     * @return number of features
     * NOTE: 'id and class' are not included in the list of features
     */
    public int getFeatureCount() {
        return featureCount;
    }


}
