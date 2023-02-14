import Data.Features;
import Data.Mushroom;
import java.io.File;
import java.io.*;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {
    private static final List<Mushroom> mushrooms = new ArrayList<>();
    public static void main(String[] args) {
        Features features = new Features();
        features.loadFeatures("C:\\Users\\gkour\\Documents\\CS429\\RandomForests2\\src\\src\\Resources\\agaricus-lepiota - training.csv");
        buildDataSet(features);
        DecisionTree decisionTree = new DecisionTree(features, mushrooms);
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

    /*
        Lookup a feature's possible values feature entropy
     */
}