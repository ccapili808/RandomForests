import Data.Mushroom;
import javafx.scene.Node;

import java.util.*;

public class DecisionNode {
    private DecisionTree decisionTree;
    private List<DecisionNode> childNodes = new ArrayList<>();
    private DecisionNode parentNode;
    private String featureValue;
    private String question;
    private List<String> features = new ArrayList<>();
    private List<Mushroom> dataSet = new ArrayList<>();
    private boolean isRoot = false;
    private boolean isLeaf = false;
    private String mushroomClass;
    private int depth = 0;
    private String impurityType;

    /**
     * This is the class constructor for a node of a decision tree
     * @param featureValue The value of the feature corresponding to this node
     * @param parentNode The parent DecisionNode
     * @param dataSet The remaining mushroom dataSet that had this featureValue
     * @param features A list of the remaining possible features
     * @param decisionTree The DecisionTree of this node
     * @param impurityType The impurity type being used (entropy, gini, me)
     */
    public DecisionNode(String featureValue, DecisionNode parentNode, List<Mushroom> dataSet,
                        List<String> features, DecisionTree decisionTree, String impurityType) {
        this.featureValue = featureValue;
        this.parentNode = parentNode;
        //add the mushrooms to this node's dataset
        //can't use = or the same object will be modified
        for(Mushroom m: dataSet) {
            this.dataSet.add(m);
        }
        for (String s: features) {
            this.features.add(s);
        }
        //set node variables
        this.decisionTree = decisionTree;
        this.depth = parentNode.getDepth() + 1;
        //change tree max depth if necessary
        if (this.depth > decisionTree.getMaxDepth()) {
            decisionTree.setMaxDepth(this.depth);
        }
        this.impurityType = impurityType;
        //add node to parent's list
        parentNode.addChildNode(this);
        //add node to tree
        decisionTree.addNode(this);
        //check dataset entropy
        //if = 0 no need to split any more
        //set as leaf node and set class
        if(decisionTree.entropy(dataSet)==0) {
            isLeaf = true;
            mushroomClass = dataSet.get(0).getMushroomClass();
        }
        //if more features left, try to split tree
        else if(features.size()>0){
            Map<String,Integer> classCounts = decisionTree.getClassCounts(dataSet);
            //set node class in case split terminates
            if (classCounts.get("p")>=classCounts.get("e")) {
                this.mushroomClass = "p";
            }
            else {
                this.mushroomClass = "e";
            }
            Split();
        }
        //if no more features left we can't split any more
        //set as leaf and set class as majority
        else {
            this.isLeaf = true;
            Map<String,Integer> classCounts = decisionTree.getClassCounts(dataSet);
            if (classCounts.get("p")>=classCounts.get("e")) {
                this.mushroomClass = "p";
            }
            else {
                this.mushroomClass = "e";
            }
        }
    }


    /**
     * This is the class constructor for the root node of a decision tree
     * @param dataSet The list of mushrooms
     * @param features The list of features
     * @param decisionTree The decision tree of this node
     * @param type The type of impurity being used (entropy, gini, me)
     */
    public DecisionNode(List<Mushroom> dataSet,
                        List<String> features, DecisionTree decisionTree, String type) {
        //root node, same logic as above but simpler because we always have features left
        for(Mushroom m: dataSet) {
            this.dataSet.add(m);
        }
        for (String s: features) {
            this.features.add(s);
        }
        this.decisionTree = decisionTree;
        this.isRoot = true;
        this.impurityType = type;
        decisionTree.addNode(this);
        Map<String,Integer> classCounts = decisionTree.getClassCounts(dataSet);
        if (classCounts.get("p")>=classCounts.get("e")) {
            this.mushroomClass = "p";
        }
        else {
            this.mushroomClass = "e";
        }
        Split();
    }

    /**
     * Getter for the depth of a node
     * @return the depth
     */
    public int getDepth() {
        return  depth;
    }

    /**
     * Getter for the question asked at a node
     * @return the question
     */
    public String getQuestion() {
        return question;
    }

    /**
     * Getter for the child nodes of a node
     * @return the list of child nodes
     */
    public List<DecisionNode> getChildNodes() {
        return childNodes;
    }

    /**
     * Getter for the feature value associated with a node
     * @return the feature value
     */
    public String getFeatureValue() {
        return featureValue;
    }

    /**
     * Getter for the isLeaf boolean
     * @return the isLeaf value
     */
    public boolean isLeaf() {
        return isLeaf;
    }

    /**
     * Getter for the mushroom class decision at this node
     * @return the mushroom class
     */
    public String getMushroomClass() {
        return mushroomClass;
    }

    /**
     * This method adds a child node to the node's list of child nodes
     * @param node the node to add
     */
    public void addChildNode(DecisionNode node) {
        this.childNodes.add(node);
    }

    //find best split and create child nodes

    /**
     * This method finds the best possible remaining feature to use for splitting the data
     * using information gain, checks to make sure it passes the chi square test.
     * If it does, it creates a child node for each value of the feature.
     * If it doesn't, it sets this node as a leaf node, and sets the class of the node
     * to the majority class of the mushroom set.
     */
    public void Split() {
        //calculate the information gain for each feature
        Map<String, Double> featureInfoGain = new HashMap<>();
        for (String feature: features) {
            featureInfoGain.put(feature, decisionTree.informationGain(dataSet, feature, impurityType));
        }
        Map<String, Integer> featureValues = new HashMap<>();
        //choose the best feature
        String bestFeature = Collections.max(featureInfoGain.entrySet(), Map.Entry.comparingByValue()).getKey();
        //get all possible feature labels
        featureValues = decisionTree.getAllFeatureValues(dataSet, bestFeature);
        //set this node's question for splitting as the best IG feature
        this.question = bestFeature;
        double chiTest = 0;
        Map<String, Integer> classCounts = decisionTree.getClassCounts(dataSet);
        Map<String, Double> classProbabilities = new HashMap<>();
        //get each class probability
        for (String s: classCounts.keySet()) {
            double classProb = ((double) classCounts.get(s))/((double) dataSet.size());
            classProbabilities.put(s, classProb);
        }
        //loop through all labels of the best feature
        for (String featureValue: featureValues.keySet()) {
            //loop through the dataset, and match the mushrooms with each label of the feature
            List<Mushroom> featureValueDataSet = new ArrayList<>();
            for(Mushroom mushroom: dataSet) {
                if (mushroom.getFeatureValue(bestFeature).equals(featureValue)) {
                    featureValueDataSet.add(mushroom);
                }
            }
            //find the real counts of mushrooms of each class for the specific attribute value
            Map<String, Integer> valueClassCounts = decisionTree.getClassCounts(featureValueDataSet);
            for (String string:classCounts.keySet()) {
                if (!valueClassCounts.containsKey(string)) {
                    valueClassCounts.put(string, 0);
                }
            }
            //find the expected counts of mushrooms of each class for the specific attribute value
            Map<String, Double> expectedCounts = new HashMap<>();
            for (String s: classCounts.keySet()) {
                double expected = (double) (classProbabilities.get(s) * featureValueDataSet.size());
                expectedCounts.put(s, expected);
                //add to the chi test value
                chiTest += ((double) (((valueClassCounts.get(s) - expectedCounts.get(s)) *
                        (valueClassCounts.get(s) - expectedCounts.get(s)))))/( (double) expectedCounts.get(s));
            }
        }
        //use chi-square test to check if tree expansion should stop or not
        int degrees = (1)*((featureValues).keySet().size()-1);
        if (degrees>0) {
            if (decisionTree.chiPassed(degrees, chiTest)) {
                // for each possible attribute value, add the mushrooms to a new list and create a child node with it
                for (String value : featureValues.keySet()) {
                    List<Mushroom> valueDataSet = new ArrayList<>();
                    for (Mushroom mushroom : dataSet) {
                        if (mushroom.getFeatureValue(bestFeature).equals(value)) {
                            valueDataSet.add(mushroom);
                        }
                    }
                    List<String> featuresLeft = new ArrayList<>(features);
                    //remove the feature used from the list of features to be used for future splits
                    featuresLeft.remove(bestFeature);
                    if (valueDataSet.size() > 0) {
                        DecisionNode childNode = new DecisionNode(value, this, valueDataSet, featuresLeft, this.decisionTree, this.impurityType);
                    }
                }
            }
            //if chi square test fails, set as leaf node and set the class
            else {
                this.isLeaf = true;
                this.mushroomClass = "null";
                for (String s : classCounts.keySet()) {
                    if (mushroomClass.equals("null")) {
                        mushroomClass = s;
                    } else if (classCounts.get(s) > classCounts.get(mushroomClass)) {
                        mushroomClass = s;
                    }
                }
            }
        }
        //if only one feature value, set as leaf and set the class
        else {
            this.isLeaf = true;
            this.mushroomClass = "null";
            for (String s : classCounts.keySet()) {
                if (mushroomClass.equals("null")) {
                    mushroomClass = s;
                } else if (classCounts.get(s) > classCounts.get(mushroomClass)) {
                    mushroomClass = s;
                }
            }
        }

    }
}
