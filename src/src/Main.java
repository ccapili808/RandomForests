import Data.Features;
import Data.Mushroom;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final List<Mushroom> mushrooms = new ArrayList<>();
    public static void main(String[] args) {
        Features features = new Features();
        features.loadFeatures("C:\\Users\\Chai\\Documents\\RandomForests\\src\\src\\Resources\\agaricus-lepiota - training.csv");
        buildDataSet(features);
        DecisionTree decisionTree = new DecisionTree(features, mushrooms);
        //Print features
        for (String feature: features.getFeatures()) {
            System.out.println(feature);
        }
        System.out.println("Feature size: " + features.getFeatures().size());
        //Print mushrooms
        for (Mushroom mushroom: mushrooms) {
            mushroom.printMushroom();
        }
    }


    public static void buildDataSet(Features features) {
        //Read each line after the first line
        Scanner sc = null;
        try {
            sc = new Scanner(new File("C:\\Users\\Chai\\Documents\\RandomForests\\src\\src\\Resources\\agaricus-lepiota - training.csv"));
        } catch (FileNotFoundException e) {
            System.out.println("The given filepath could not be found");
        }
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
}