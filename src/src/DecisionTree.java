import Data.Features;
import Data.Mushroom;
import javafx.scene.Node;

import java.util.List;

public class DecisionTree {
    private final Features features;
    private final List<Mushroom> mushrooms;
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
        buildTree();
    }

    //Implement the decision tree algorithm
    public void buildTree() {


    }



}
