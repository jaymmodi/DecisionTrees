import java.util.*;

/**
 * Created by jay on 2/12/16.
 */
public class DecisionTree {


    public DataSet dataset;
    public String splitOn;


    public DecisionTree(DataSet dataSet, String splitOn) {
        this.dataset = dataSet;
        this.splitOn = splitOn;
    }

    public TreeNode buildTree(TreeNode root) {
        Queue<TreeNode> queue = new LinkedList<>();

        root = getTreeNode(this.dataset, null, null, "root");
        queue.add(root);

        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            makeChildNodes(node, queue);
        }

        return root;
    }

    private void makeChildNodes(TreeNode node, Queue<TreeNode> queue) {
        Feature feature = node.feature;

        if (feature.getType().equalsIgnoreCase("Continuous")) {
            makeContinuousChildNodes(node, queue);
        } else {
            makeCategoricalChildNodes(node, queue);
        }
    }

    private void makeCategoricalChildNodes(TreeNode node, Queue<TreeNode> queue) {

    }

    private void makeContinuousChildNodes(TreeNode node, Queue<TreeNode> queue) {
        ContinuousTreeNode continuousTreeNode = (ContinuousTreeNode) node;
        Feature feature = node.feature;

        continuousTreeNode.leftNode = getTreeNode(this.dataset, feature, node, "left");
        queue.add(continuousTreeNode.leftNode);

        continuousTreeNode.rightNode = getTreeNode(this.dataset, feature, node, "right");
        queue.add(continuousTreeNode.rightNode);
    }

    private TreeNode getTreeNode(DataSet dataset, Feature feature, TreeNode parent, String side) {
        ArrayList<Feature> remainingFeatures = (ArrayList<Feature>) this.dataset.getRemainingFeatures(feature);
        Feature bestFeature = getFeatureToSplitOn(remainingFeatures);

        HashMap<String, Integer> countMap = getClassLabelCount(dataset, bestFeature, side);

        TreeNode node = makeNode(bestFeature);
        node.feature = bestFeature;
        node.setCountPerClassLabel(countMap);
        node.parentNode = parent;

        return node;
    }

    private TreeNode makeNode(Feature bestFeature) {
        if (bestFeature.getType().equalsIgnoreCase("Continuous")) {
            return new ContinuousTreeNode(this.dataset);
        } else {
            return new CategoricalTreeNode(this.dataset);
        }
    }

    private HashMap<String, Integer> getClassLabelCount(DataSet dataset, Feature feature, String side) {
        HashMap<String, Integer> countMap = new HashMap<>();

        if (side.equalsIgnoreCase("root")) {
            countMap = dataForRoot(dataset);
        } else if (side.equalsIgnoreCase("left")) {
            countMap = dataForChild(dataset, feature, side);
        } else if (side.equalsIgnoreCase("right")) {
            countMap = dataForChild(dataset, feature, side);
        }

        return countMap;
    }

    private HashMap<String, Integer> dataForChild(DataSet dataset, Feature feature, String side) {
        HashMap<String, Integer> countMap = new HashMap<>();

        if (side.equalsIgnoreCase("left")) {           // <=
            for (Instance instance : dataset.instances) {
                List<Double> values = instance.featureValues;
                if (values.get(feature.index) <= feature.splitValue) {
                    insertInMap(instance.classLabel, countMap);
                }
            }

        } else {
            for (Instance instance : dataset.instances) {
                List<Double> values = instance.featureValues;
                if (values.get(feature.index) > feature.splitValue) {
                    insertInMap(instance.classLabel, countMap);
                }
            }
        }

        return countMap;
    }

    private void insertInMap(String classLabel, HashMap<String, Integer> countMap) {
        if (countMap.containsKey(classLabel)) {
            int count = countMap.get(classLabel);
            ++count;
            countMap.put(classLabel, count);
        } else {
            countMap.put(classLabel, 1);
        }
    }

    private HashMap<String, Integer> dataForRoot(DataSet dataset) {
        HashMap<String, Integer> countMap = new HashMap<>();

        for (Instance instance : dataset.instances) {
            String label = instance.classLabel;

            insertInMap(label, countMap);
        }
        return countMap;
    }

    private Feature getFeatureToSplitOn(ArrayList<Feature> features) {
        if (this.splitOn.equalsIgnoreCase("GINI") || this.splitOn.equals("1")) {
            features = findGiniValue(features);
        } else {
            features = findInfoGain(features);
        }

        sortOnSplitVariable(features, this.splitOn); //sort in ascending order
        return features.get(0);
    }

    private ArrayList<Feature> findInfoGain(ArrayList<Feature> features) {

        return features;
    }

    private ArrayList<Feature> findGiniValue(ArrayList<Feature> features) {
        for (Feature feature : features) {
            int index = feature.index;

            sortFeature(index);
            feature.giniValue = Double.MAX_VALUE;

            ArrayList<Instance> instances = this.dataset.instances;
            for (int i = 0; i <= instances.size() - 2; i++) {
                Instance instance1 = instances.get(i);
                Instance instance2 = instances.get(i + 1);

                if (!instance1.classLabel.equals(instance2.classLabel)) {

                    feature.splitValue = instance1.featureValues.get(index) + instance2.featureValues.get(index) / 2;
                    feature.giniValue = Math.min(getGiniValue(feature.splitValue, index), feature.giniValue);
                }
            }
        }

        // calculate split value as well
        return features;
    }

    private double getGiniValue(double splitValue, int index) {

        HashMap<String, Integer> countLessThanMap = new HashMap<>();
        HashMap<String, Integer> countMoreThanMap = new HashMap<>();
        ArrayList<Instance> instances = this.dataset.instances;

        for (Instance instance : instances) {
            double value = instance.featureValues.get(index);
            String label = instance.classLabel;

            if (value <= splitValue) {
                insertInMap(label, countLessThanMap);
            } else {
                insertInMap(label, countMoreThanMap);
            }
        }

        int lessThanCount = count(countLessThanMap);
        int moreThanCount = count(countMoreThanMap);

        double partialG1 = calculateGini(countLessThanMap, lessThanCount);
        double partialG2 = calculateGini(countMoreThanMap, moreThanCount);

        return (partialG1 * lessThanCount) + (partialG2 * moreThanCount) / (lessThanCount + moreThanCount);

    }

    private double calculateGini(HashMap<String, Integer> map, int totalCount) {
        double value = 0;
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            value += entry.getValue() / (double) totalCount;
        }
        return 1 - value;
    }

    private int count(HashMap<String, Integer> map) {
        int count = 0;
        for (Map.Entry<String, Integer> stringIntegerEntry : map.entrySet()) {
            count += stringIntegerEntry.getValue();
        }
        return count;
    }

    private void sortFeature(int index) {
        Collections.sort(this.dataset.instances, (instance1, instance2) -> {
            if (instance1.featureValues.get(index) > instance2.featureValues.get(index)) {
                return 1;
            } else if (instance1.featureValues.get(index) < instance2.featureValues.get(index)) {
                return -1;
            } else {
                return 0;
            }
        });
    }

    private void sortOnSplitVariable(ArrayList<Feature> features, String splitOn) {
        if (splitOn.equalsIgnoreCase("GINI")) {
            Collections.sort(features, (feature1, feature2) -> {
                if (feature1.giniValue > feature2.giniValue) {
                    return 1;
                } else if (feature1.giniValue < feature2.giniValue) {
                    return -1;
                } else {
                    return 0;
                }
            });
        } else if (splitOn.equalsIgnoreCase("InfoGain")) {
            Collections.sort(features, (feature1, feature2) -> {
                if (feature1.infoGain > feature2.infoGain) {
                    return 1;
                } else if (feature1.infoGain < feature2.infoGain) {
                    return -1;
                } else {
                    return 0;
                }
            });
        }
    }

    private void print(ArrayList<Feature> features) {
        for (Feature feature : features) {
            System.out.println(feature.giniValue);
        }
    }


}
