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
    //constructor for non root node
    public DecisionNode(String featureValue, DecisionNode parentNode, List<Mushroom> dataSet,
                        List<String> features, DecisionTree decisionTree) {
        this.featureValue = featureValue;
        this.parentNode = parentNode;
        for(Mushroom m: dataSet) {
            this.dataSet.add(m);
        }
        for (String s: features) {
            this.features.add(s);
        }
        this.decisionTree = decisionTree;
        this.depth = parentNode.getDepth() + 1;
        parentNode.addChildNode(this);
        decisionTree.addNode(this);
        if(decisionTree.entropy(dataSet)==0) {
            isLeaf = true;
            mushroomClass = dataSet.get(0).getMushroomClass();
        }
        else {
            Split();
        }
        //Split();
    }
    //constructor for root node
    public DecisionNode(List<Mushroom> dataSet,
                        List<String> features, DecisionTree decisionTree) {
        for(Mushroom m: dataSet) {
            this.dataSet.add(m);
        }
        for (String s: features) {
            this.features.add(s);
        }
        this.decisionTree = decisionTree;
        this.isRoot = true;
        decisionTree.addNode(this);
        Split();
    }
    public int getDepth() {
        return  depth;
    }

    public String getQuestion() {
        return question;
    }

    public List<DecisionNode> getChildNodes() {
        return childNodes;
    }

    public String getFeatureValue() {
        return featureValue;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public String getMushroomClass() {
        return mushroomClass;
    }

    public void addChildNode(DecisionNode node) {
        this.childNodes.add(node);
    }

    //find best split and create child nodes
    public void Split() {
        double entropy = decisionTree.entropy(dataSet);
        Map<String, Double> featureInfoGain = new HashMap<>();
        for (String feature: features) {
            featureInfoGain.put(feature, decisionTree.informationGain(feature));
        }
        Map<String, Integer> featureValues = new HashMap<>();
        String bestFeature = Collections.max(featureInfoGain.entrySet(), Map.Entry.comparingByValue()).getKey();
        featureValues = decisionTree.getAllFeatureValues(dataSet, bestFeature);
        this.question = bestFeature;
        for (String featureValue: featureValues.keySet()) {
            List<Mushroom> featureValueDataSet = new ArrayList<>();
            for(Mushroom mushroom: dataSet) {
                if (mushroom.getFeatureValue(bestFeature).equals(featureValue)) {
                    featureValueDataSet.add(mushroom);
                }
            }
            List<String> featuresLeft = new ArrayList<>(features);
            featuresLeft.remove(bestFeature);
            DecisionNode childNode = new DecisionNode(featureValue, this, featureValueDataSet, featuresLeft, this.decisionTree);
        }
    }
}
