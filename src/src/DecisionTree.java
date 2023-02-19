import Data.Features;
import Data.Mushroom;

import java.util.*;

public class DecisionTree {
    private double entropy;
    //private final Features features;
    private List<String> features;
    private final List<Mushroom> mushrooms;
    private int numFeatures;
    private int numMushrooms;
    private int numberOfClasses = 2;
    private int maxDepth = 0;
    private double alpha;
    private List<DecisionNode> treeNodes = new ArrayList<>();
    private Map<Integer, Map<Double,Double>> chiTable;

    /**
     * Constructor for the DecisionTree class
     * @param features The list of features to use
     * @param mushrooms The mushroom dataset list
     * @param type The type of impurity to use (entropy, gini, me)
     * @param chiTable The Chi Table values
     * @param alpha The alpha value to use
     */
    public DecisionTree(List<String> features, List<Mushroom> mushrooms, String type,
                        Map<Integer,Map <Double,Double>> chiTable, double alpha)
    {
        this.features = features;
        this.mushrooms = mushrooms;
        this.numMushrooms = mushrooms.size();
        this.chiTable = chiTable;
        this.alpha = alpha;
        buildTree(type);
    }

    /**
     * This method starts building the decision tree by creating a root node
     * @param type the type of impurity used (entropy, gini, me)
     */
    public void buildTree(String type) {
        DecisionNode root = new DecisionNode(mushrooms, features, this, type);
    }

    /**
     * This method predicts the class of a mushroom
     * @param mushroom the mushroom to predict
     * @return the class of a mushroom
     */
    public String predictMushroom(Mushroom mushroom) {
        return traverseNode(treeNodes.get(0), mushroom);
    }

    /**
     * This method traverses the decision tree nodes until it finds a leaf node
     * and returns the class of the mushroom
     * @param node the current node
     * @param mushroom the mushroom to predict
     * @return the class of the mushroom
     */
    public String traverseNode(DecisionNode node, Mushroom mushroom) {
        //if we reached a leaf, return the class prediction of that node
        if (node.isLeaf()) {
            return node.getMushroomClass();
        }
        //otherwise check the child nodes for the mushroom's feature value
        else {
            for (DecisionNode decisionNode : node.getChildNodes()) {
                if (mushroom.getFeatureValue(node.getQuestion()).equals(decisionNode.getFeatureValue())) {
                    DecisionNode nextNode = decisionNode;
                    //if leaf, return class
                    if (nextNode.isLeaf()) {
                        return nextNode.getMushroomClass();
                    //else traverse the child node
                    } else {
                        return traverseNode(nextNode, mushroom);
                    }
                }
            }
        }
        return node.getMushroomClass();
    }

    /**
     * This method counts the total number of each class of mushrooms in
     * a list of mushrooms
     * @param mushrooms the list of mushrooms
     * @return the class counts
     */
    public Map<String, Integer> getClassCounts(List<Mushroom> mushrooms) {
        Map<String, Integer> classCounts = new HashMap<>();
        for (Mushroom mushroom: mushrooms) {
            String mushroomClass = mushroom.getMushroomClass();
            if (classCounts.containsKey(mushroomClass)) {
                classCounts.put(mushroomClass, classCounts.get(mushroomClass) + 1);
            } else {
                classCounts.put(mushroomClass, 1);
            }
        }
        return classCounts;
    }

    /**
     * This method checks the entropy of a list of mushrooms.
     * Used to stop tree expansion if entropy is 0, which means all
     * mushrooms have the same class in that node.
     * @param mushrooms the list of mushrooms to check
     * @return the entropy value
     */
    public double entropy(List<Mushroom> mushrooms) {
        Map<String, Integer> classes = getClassCounts(mushrooms);
        double entropy = 0;
        for (String mushroomClass: classes.keySet()) {
            double probability = (double) classes.get(mushroomClass) / mushrooms.size();
            //shannon entropy formula using log base 2
            double  log =  probability * Math.log(probability) / Math.log(2);
            entropy += log;
        }
        return -entropy;
    }

    /**
     * This method returns the impurity for a List of mushrooms, using
     * the different impurity types discussed in class
     * @param mushrooms the list of mushrooms to check
     * @param type the impurity type (entropy,gini,me)
     * @return the impurity value
     */
    public double impurity (List<Mushroom> mushrooms, String type) {
        Map<String, Integer> classes = getClassCounts(mushrooms);
        if (type.equals("entropy")) {
            double entropy = 0;
            for (String mushroomClass: classes.keySet()) {
                double probability = (double) classes.get(mushroomClass) / mushrooms.size();
                //shannon entropy formula using log base 2
                double  log =  probability * Math.log(probability) / Math.log(2);
                entropy += log;
            }
            return -entropy;
        }
        else if(type.equals("gini")) {
            double gini = 0;
            for (String mushroomClass: classes.keySet()) {
                double probability = (double) classes.get(mushroomClass) / mushrooms.size();
                //square the probability of each class
                gini += probability*probability;
            }
            //subtract it from 1 to get the gini impurity
            return (1-gini);
        }
        else if(type.equals("me")) {
            double me = 0;
            //find the max of class probabilities
            for (String mushroomClass: classes.keySet()) {
                double probability = (double) classes.get(mushroomClass) / mushrooms.size();
                if (probability>me) {
                    me = probability;
                }
            }
            //subtract from 1 to get misclassification error
            return (1-me);
        }
        return 0;
    }

    /**
     * This method returns the information gain for a particular decision tree split
     * @param mushrooms The list of mushrooms to check
     * @param feature The feature of mushrooms to check
     * @param type The impurity type to use (entropy, gini, me)
     * @return
     */
    public double informationGain(List<Mushroom> mushrooms, String feature, String type) {
        return  impurity(mushrooms, type) - featureImpurity(feature, type);
    }

    /**
     * This method calculates the entropy of a split using a feature
     * @param feature the feature to calculate
     * @param type the impurity type (entropy, gini, me)
     * @return the value of the feature impurity
     */
    public double featureImpurity(String feature, String type) {
        Map<String, Integer> featureValues = getAllFeatureValues(mushrooms, feature);
        double featureImpurity = 0;
        //iterate through each mushroom and calculate the impurity for each feature
        int total = 0;
        for (String featureValue: featureValues.keySet()) {
            //get the impurity for each value of the feature
            double valueImpurity = lookupEncoding(feature, featureValue, type);
            featureImpurity += valueImpurity;
            total += featureValues.get(featureValue);
        }
        return featureImpurity;
    }

    /**
     * This method calculates the impurity for a particular value of a feature
     * @param feature the feature to calculate
     * @param value the value of the feature to calculate
     * @param type the type of impurity (entropy, gini, me)
     * @return the value of the impurity
     */
    public double lookupEncoding(String feature, String value, String type) {
        Map<String, Integer> classCounts = new HashMap<>();
        int total = 0;
        for (Mushroom mushroom : mushrooms) {
            if (mushroom.getFeatureValue(feature).equals(value)) {
                //Add to class counts
                if (classCounts.containsKey(mushroom.getMushroomClass())) {
                    classCounts.put(mushroom.getMushroomClass(), classCounts.get(mushroom.getMushroomClass()) + 1);
                } else {
                    classCounts.put(mushroom.getMushroomClass(), 1);
                }
            }
        }
        //Print out class counts
        for (String mushroomClass : classCounts.keySet()) {
            //System.out.println(mushroomClass + ": " + classCounts.get(mushroomClass));
            total += classCounts.get(mushroomClass);
        }
        if (type.equals("entropy")) {
            //get the entropy for this feature value
            double probability = total / (double) mushrooms.size();
            double entropy = 0;
            for (String mushroomClass : classCounts.keySet()) {
                double classProbability = classCounts.get(mushroomClass) / (double) total;
                double log = classProbability * Math.log(classProbability) / Math.log(2);
                entropy += log;
            }
            //multiply by the size of the value
            return -entropy * probability;
        }
        else if (type.equals("gini")) {
            //get the ratio of mushrooms with this feature value
            double probability = total / (double) mushrooms.size();
            double gini = 1;
            for (String mushroomClass : classCounts.keySet()) {
                double classProbability = classCounts.get(mushroomClass) / (double) total;
                double square = classProbability * classProbability;
                //get the square of probabilities for gini
                gini += square;
            }
            //subtract squares from 1 and multiply by value ratio for IG
            return ((1 - gini) * probability);
        }
        else if (type.equals("me")) {
            double probability = total / (double) mushrooms.size();
            double me = 1;
            double max = 0;
            //get the max probability
            for (String mushroomClass : classCounts.keySet()) {
                double classProbability = classCounts.get(mushroomClass) / (double) total;
                if (classProbability>max) {
                    max = classProbability;
                }
            }
            //subtract from 1 and multiply by value ratio for IG
            return ((1 - max) * probability);
        }
        return 0;
    }

    /**
     * This method gets the count of each possible value of a feature
     * @param mushrooms the list of mushrooms to use
     * @param feature the feature to get the values of
     * @return a map of the value and the count
     */
    public Map<String, Integer> getAllFeatureValues(List<Mushroom> mushrooms, String feature) {
        Map<String, Integer> featureValues = new HashMap<>();
        //count the unique values for each feature
        for (Mushroom mushroom: mushrooms) {
            String featureValue = mushroom.getFeatureValue(feature);
            if (featureValues.containsKey(featureValue)) {
                featureValues.put(featureValue, featureValues.get(featureValue) + 1);
            } else {
                featureValues.put(featureValue, 1);
            }
        }
        return featureValues;
    }


    /**
     * This method gets all the possible unique values of each feature
     * @param features the list of features to check
     * @return a map of features mapped to their set of string values
     */
    public  Map<String, Set<String>> getUniqueValues(Features features) {
        //for each feature show all of the possible values for that feature
        Map<String, Set<String>> uniqueValues = new HashMap<>();
        for (String feature: features.getFeatures()) {
            //Get all of the values for the feature
            Set<String> values = new HashSet<>();
            for (Mushroom mushroom: mushrooms) {
                values.add(mushroom.getFeatureValue(feature));
            }
            uniqueValues.put(feature, values);
        }
        return uniqueValues;
    }

    /**
     * This method adds a node to the tree
     * @param decisionNode the node to add
     */
    public void addNode(DecisionNode decisionNode) {
        treeNodes.add(decisionNode);
    }

    /**
     * This method checks whether the statistic calculated for the split
     * is greater than the chi square table value.
     * @param degrees the degrees of freedom
     * @param testValue the calculated value
     * @return true if greater, false if not
     */
    public boolean chiPassed (int degrees, double testValue) {
        if (testValue>chiTable.get(degrees).get(alpha)) {
            return true;
        }
        return false;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }
}
