package Data;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Features {
    public List<String> getFeatures() {
        return features;
    }

    private final List<String> features = new ArrayList<>();
    private int featureCount = 0;
    public Features(){}
    public List<String> loadFeatures(String filePath) {
        //parsing a CSV file into Scanner class constructor
        Scanner sc = null;
        try {
            sc = new Scanner(new File(filePath));
        } catch (FileNotFoundException e) {
            System.out.println("The given filepath could not be found");
        }
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

}
