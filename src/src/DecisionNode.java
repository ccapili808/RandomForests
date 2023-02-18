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

    //constructor for non root node
    public DecisionNode(String featureValue, DecisionNode parentNode, List<Mushroom> dataSet,
                        List<String> features, DecisionTree decisionTree, String impurityType) {
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
        this.impurityType = impurityType;
        parentNode.addChildNode(this);
        decisionTree.addNode(this);
        if(decisionTree.entropy(dataSet)==0) {
            isLeaf = true;
            mushroomClass = dataSet.get(0).getMushroomClass();
        }
        else if(features.size()>0){
            Map<String,Integer> classCounts = decisionTree.getClassCounts(dataSet);
            if (classCounts.get("p")>=classCounts.get("e")) {
                this.mushroomClass = "p";
            }
            else {
                this.mushroomClass = "e";
            }
            Split();
        }
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
        //Split();
    }
    //constructor for root node
    public DecisionNode(List<Mushroom> dataSet,
                        List<String> features, DecisionTree decisionTree, String type) {
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
            featureInfoGain.put(feature, decisionTree.informationGain(dataSet, feature, impurityType));
        }
        Map<String, Integer> featureValues = new HashMap<>();
        String bestFeature = Collections.max(featureInfoGain.entrySet(), Map.Entry.comparingByValue()).getKey();
        featureValues = decisionTree.getAllFeatureValues(dataSet, bestFeature);
        this.question = bestFeature;
        double chiTest = 0;
        Map<String, Integer> classCounts = decisionTree.getClassCounts(dataSet);
        Map<String, Double> classProbabilities = new HashMap<>();
        for (String s: classCounts.keySet()) {
            double classProb = ((double) classCounts.get(s))/((double) dataSet.size());
            classProbabilities.put(s, classProb);
        }
        for (String featureValue: featureValues.keySet()) {
            List<Mushroom> featureValueDataSet = new ArrayList<>();
            for(Mushroom mushroom: dataSet) {
                if (mushroom.getFeatureValue(bestFeature).equals(featureValue)) {
                    featureValueDataSet.add(mushroom);
                }
            }
            Map<String, Integer> valueClassCounts = decisionTree.getClassCounts(featureValueDataSet);
            for (String string:classCounts.keySet()) {
                if (!valueClassCounts.containsKey(string)) {
                    valueClassCounts.put(string, 0);
                }
            }
            Map<String, Double> expectedCounts = new HashMap<>();
            for (String s: classCounts.keySet()) {
                double expected = (double) (classProbabilities.get(s) * featureValueDataSet.size());
                expectedCounts.put(s, expected);
                chiTest += (double) (((valueClassCounts.get(s) - expectedCounts.get(s)) *
                        (valueClassCounts.get(s) - expectedCounts.get(s)))/expectedCounts.get(s));
            }
            int degrees = (classCounts.size()-1)*((featureValues).size()-1);
            if (degrees>0) {
                if (decisionTree.chiPassed(degrees, chiTest)) {
                    for (String value : featureValues.keySet()) {
                        List<Mushroom> valueDataSet = new ArrayList<>();
                        for (Mushroom mushroom : dataSet) {
                            if (mushroom.getFeatureValue(bestFeature).equals(value)) {
                                valueDataSet.add(mushroom);
                            }
                        }
                        List<String> featuresLeft = new ArrayList<>(features);
                        featuresLeft.remove(bestFeature);
                        if (valueDataSet.size() > 0) {
                            DecisionNode childNode = new DecisionNode(value, this, valueDataSet, featuresLeft, this.decisionTree, this.impurityType);
                        }
                    }
                }
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
}
