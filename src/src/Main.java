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
        for (Mushroom mushroom : mushrooms) {
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
            Mushroom mushroom = new Mushroom(features.getFeatures(), line[0]);
            for (int i = 1; i < line.length; i++) {
                mushroom.addFeature(features.getFeatures().get(i), line[i]);
            }
            mushrooms.add(mushroom);
        }
    }



}