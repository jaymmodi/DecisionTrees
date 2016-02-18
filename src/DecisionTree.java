import java.util.*;
import java.util.stream.Collectors;

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

        root = getNode(this.dataset.instances, null, null, "root");
        root.recordsOnNode= this.dataset.instances;

        queue.add(root);

        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            if (!node.isLeaf()) {
                makeChildNodes(node, queue);
            }
        }

        return root;
    }

    private TreeNode getNode(ArrayList<Instance> instances, TreeNode parent, Feature feature, String side) {
        ArrayList<Feature> remainingFeatures = (ArrayList<Feature>) this.dataset.getRemainingFeatures(feature);

        Feature bestFeature = getSplit(instances, remainingFeatures);

        HashMap<String, Integer> countMap = getClassLabelCount(instances, side);

        TreeNode node = makeNode(bestFeature);
        node.feature = bestFeature;
        node.setCountPerClassLabel(countMap);
        node.parentNode = parent;
        node.label = bestFeature.getName();
        node.isLeaf = (node.countPerClassLabel.size() == 1);

        return node;
    }

    private ArrayList<ArrayList<Instance>> splitData(List<Instance> recordsOnNode, Feature bestFeature) {

        ArrayList<ArrayList<Instance>> childDatasets = new ArrayList<>();
        double spliValue = bestFeature.splitValue;
        int index = bestFeature.index;

        if (bestFeature.getType().equalsIgnoreCase("Continuous")) {
            ArrayList<Instance> instances = (ArrayList<Instance>) recordsOnNode;

            ArrayList<Instance> leftInstances = instances.stream()
                    .filter(instance -> instance.featureValues.get(index) <= spliValue)
                    .collect(Collectors.toCollection(ArrayList::new));

            ArrayList<Instance> rightInstances = instances.stream()
                    .filter(instance -> instance.featureValues.get(index) > spliValue)
                    .collect(Collectors.toCollection(ArrayList::new));
            childDatasets.add(leftInstances);
            childDatasets.add(rightInstances);
        } else {

        }
        return childDatasets;
    }

    private Feature getSplit(ArrayList<Instance> instances, ArrayList<Feature> remainingFeatures) {
        Feature feature = null;

        if (this.splitOn.equalsIgnoreCase("GINI") || this.splitOn.equals("1")) {
            GiniSplit giniSplit;
            for (Feature perFeature : remainingFeatures) {
                sortFeature(instances, perFeature.index);
                giniSplit = calculateGiniWithSplit(instances, perFeature);
                if (giniSplit != null) {
                    perFeature.giniValue = giniSplit.giniValue;
                    perFeature.splitValue = giniSplit.splitValue;
                }
            }
            sortOnSplitVariable(remainingFeatures, this.splitOn); //sort in ascending order
            feature = remainingFeatures.get(0);
        } else {
            System.out.println("Info gain");
        }

        return feature;
    }

    private GiniSplit calculateGiniWithSplit(ArrayList<Instance> instances, Feature feature) {
        int index = feature.index;
        ArrayList<GiniSplit> miniGiniSplit = new ArrayList<>();

        for (int i = 0; i <= instances.size() - 2; i++) {
            Instance one = instances.get(i);
            Instance two = instances.get(i + 1);

            if (!one.classLabel.equals(two.classLabel)) {
                GiniSplit giniSplit = new GiniSplit();
                giniSplit.splitValue = (one.featureValues.get(index) + two.featureValues.get(index)) / 2;
                giniSplit.giniValue = getGiniValue(giniSplit.splitValue, index);
                miniGiniSplit.add(giniSplit);
            }
        }
        return findMinGini(miniGiniSplit);
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

        ArrayList<ArrayList<Instance>> childDatasets = splitData(node.recordsOnNode,feature);

        continuousTreeNode.leftNode = getNode(childDatasets.get(0), node, feature, "left");
        continuousTreeNode.leftNode.recordsOnNode = childDatasets.get(0);
        queue.add(continuousTreeNode.leftNode);

        continuousTreeNode.rightNode = getNode(childDatasets.get(1), node, feature, "right");
        continuousTreeNode.rightNode.recordsOnNode = childDatasets.get(1);
        queue.add(continuousTreeNode.rightNode);
    }

    private TreeNode makeNode(Feature bestFeature) {
        if (bestFeature.getType().equalsIgnoreCase("Continuous")) {
            ContinuousTreeNode continuousTreeNode = new ContinuousTreeNode(this.dataset);
            continuousTreeNode.splitValue = bestFeature.splitValue;
            return continuousTreeNode;
        } else {
            return new CategoricalTreeNode(this.dataset);
        }
    }

    private HashMap<String, Integer> getClassLabelCount(ArrayList<Instance> instances, String side) {
        HashMap<String, Integer> countMap = new HashMap<>();

        for (Instance instance : instances) {
            String label = instance.classLabel;

            insertInMap(label, countMap);
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

    private GiniSplit findMinGini(ArrayList<GiniSplit> minGiniPerFeature) {
        GiniSplit minGiniFeature = null;
        double min = Double.MAX_VALUE;

        for (GiniSplit giniSplit : minGiniPerFeature) {
            if (giniSplit.giniValue <= min) {
                minGiniFeature = giniSplit;
                min = giniSplit.giniValue;
            }
        }
        return minGiniFeature;
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

        return ((partialG1 * lessThanCount) + (partialG2 * moreThanCount)) / (lessThanCount + moreThanCount);

    }

    private double calculateGini(HashMap<String, Integer> map, int totalCount) {
        double value = 0;
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            value += Math.pow((entry.getValue() / (double) totalCount), 2);
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

    private void sortFeature(ArrayList<Instance> instances, int index) {
        Collections.sort(instances, (instance1, instance2) -> {
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
