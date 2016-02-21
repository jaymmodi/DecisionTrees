import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by jay on 2/12/16.
 */
public class DecisionTree {


    public DataSet dataset;
    ;
    public String splitOn;


    public DecisionTree(DataSet dataSet, String splitOn) {
        this.dataset = dataSet;
        this.splitOn = splitOn;
    }


    public TreeNode buildTree(TreeNode root) {
        Queue<TreeNode> queue = new LinkedList<>();

        ArrayList<Instance> instances = this.dataset.getInstances();
        root = getNode(instances, null, null, "root");
        root.recordsOnNode = this.dataset.instances;

        queue.add(root);

        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();

            if (!node.isLeaf() && node.recordsOnNode.size() > 0) {
                makeChildNodes(node, queue);
            }
        }
        System.out.println("Finish");
        return root;
    }

    private TreeNode getNode(ArrayList<Instance> instances, TreeNode parent, Feature feature, String side) {
        TreeNode node;
        ArrayList<Feature> remainingFeatures = this.dataset.getRemainingFeatures(feature);

        HashMap<String, Integer> countMap = getClassLabelCount(instances);
        if (countMap.size() == 1) {  // leaf node
            node = new TreeNode(this.dataset);
            node.isLeaf = true;
            node.setCountPerClassLabel(countMap);
            for (Map.Entry<String, Integer> stringIntegerEntry : node.countPerClassLabel.entrySet()) {
                node.label = stringIntegerEntry.getKey();
            }
        } else {
            Feature bestFeature = getSplit(instances, remainingFeatures);

            node = makeNode(bestFeature);
            node.setCountPerClassLabel(countMap);
            node.feature = bestFeature;
        }
        node.parentNode = parent;

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
        Feature feature;
        GiniSplit giniSplit;

        System.out.println("instances = " + instances.size());

        if (this.splitOn.equalsIgnoreCase("GINI") || this.splitOn.equals("1")) {
            for (Feature perFeature : remainingFeatures) {
                ArrayList<Instance> localInstances = new ArrayList<>(instances);
                sortFeature(localInstances, perFeature.index);
                giniSplit = calcGiniInfoGain(localInstances, perFeature, this.splitOn);
                if (giniSplit != null) {
                    perFeature.giniValue = giniSplit.giniValue;
                    perFeature.splitValue = giniSplit.splitValue;
                }
            }
            sortOnSplitVariable(remainingFeatures, this.splitOn); //sort in ascending order
            feature = remainingFeatures.get(0);
        } else {
            System.out.println("Info gain");
            for (Feature perFeature : remainingFeatures) {
                ArrayList<Instance> localInstances = new ArrayList<>(instances);
                sortFeature(localInstances, perFeature.index);
                giniSplit = calcGiniInfoGain(localInstances, perFeature, this.splitOn);
                if (giniSplit != null) {
                    perFeature.infoGain = giniSplit.infoGain;
                    perFeature.splitValue = giniSplit.splitValue;
                }
            }
            sortOnSplitVariable(remainingFeatures, this.splitOn); //sort in ascending order
            feature = remainingFeatures.get(0);
        }

        return feature;
    }


    private GiniSplit calcGiniInfoGain(ArrayList<Instance> instances, Feature feature, String splitOn) {
        int index = feature.index;
        ArrayList<GiniSplit> miniGiniSplit = new ArrayList<>();

        for (int i = 0; i <= instances.size() - 2; i++) {
            Instance one = instances.get(i);
            Instance two = instances.get(i + 1);

            if (!one.trueLabel.equals(two.trueLabel)) {
                GiniSplit giniSplit = new GiniSplit();
                giniSplit.splitValue = (one.featureValues.get(index) + two.featureValues.get(index)) / 2;
                System.out.println(feature.getName() + "  " + one.featureValues.get(index) + "   " + two.featureValues.get(index) + " " + giniSplit.splitValue + " " + one.trueLabel + " " + two.trueLabel);
                if (splitOn.equalsIgnoreCase("GINI") || splitOn.equals("1")) {
                    giniSplit.giniValue = getGiniValue(giniSplit.splitValue, index);
                } else {
                    giniSplit.infoGain = getInfoGain(giniSplit.splitValue, index);
                }
                System.out.println(giniSplit.giniValue);
                miniGiniSplit.add(giniSplit);
            }
        }
        return findMinGini(miniGiniSplit);
    }

    private double getInfoGain(double splitValue, int index) {
        HashMap<String, Integer> countLessThanMap = new HashMap<>();
        HashMap<String, Integer> countMoreThanMap = new HashMap<>();
        ArrayList<Instance> instances = this.dataset.instances;

        makeChildrenMap(splitValue, index, countLessThanMap, countMoreThanMap, instances);

        int lessThanCount = count(countLessThanMap);
        int moreThanCount = count(countMoreThanMap);

        double partialInfo1 = calculateInfo(countLessThanMap, lessThanCount);
        double partialInfo2 = calculateInfo(countMoreThanMap, moreThanCount);

        return ((partialInfo1 * lessThanCount) + (partialInfo2 * moreThanCount)) / (lessThanCount + moreThanCount);
    }

    private void makeChildrenMap(double splitValue, int index, HashMap<String, Integer> countLessThanMap, HashMap<String, Integer> countMoreThanMap, ArrayList<Instance> instances) {
        for (Instance instance : instances) {
            double value = instance.featureValues.get(index);
            String label = instance.trueLabel;

            if (value <= splitValue) {
                insertInMap(label, countLessThanMap);
            } else {
                insertInMap(label, countMoreThanMap);
            }
        }
    }

    private double calculateInfo(HashMap<String, Integer> map, int totalCount) {
        double value = 0;
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            double temp = (entry.getValue() / (double) totalCount);
            value += temp * Math.log(temp);
        }
        return value * -1;
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

        ArrayList<ArrayList<Instance>> childDatasets = splitData(node.recordsOnNode, feature);

        if (childDatasets.get(0).size() != 0 && childDatasets.get(1).size() != 0) {
            continuousTreeNode.leftNode = getNode(childDatasets.get(0), node, feature, "left");
            continuousTreeNode.leftNode.recordsOnNode = childDatasets.get(0);
            queue.add(continuousTreeNode.leftNode);

            continuousTreeNode.rightNode = getNode(childDatasets.get(1), node, feature, "right");
            continuousTreeNode.rightNode.recordsOnNode = childDatasets.get(1);
            queue.add(continuousTreeNode.rightNode);
        } else {
            makeCurrentNodeAsLeaf(node);
        }

    }

    private void makeCurrentNodeAsLeaf(TreeNode node) {
        node.isLeaf = true;
        HashMap<String, Integer> countPerClassLabel = node.countPerClassLabel;

        int max = Integer.MIN_VALUE;
        String label = "";

        for (Map.Entry<String, Integer> stringIntegerEntry : countPerClassLabel.entrySet()) {
            int value = stringIntegerEntry.getValue();

            if (value > max) {
                max = value;
                label = stringIntegerEntry.getKey();
            }
        }

        node.label = label;
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

    private HashMap<String, Integer> getClassLabelCount(ArrayList<Instance> instances) {
        HashMap<String, Integer> countMap = new HashMap<>();

        for (Instance instance : instances) {
            String label = instance.trueLabel;

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
            if (giniSplit.giniValue < min) {
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

        makeChildrenMap(splitValue, index, countLessThanMap, countMoreThanMap, instances);
        if (splitValue == 2.45 && index == 2) {
            System.out.println(splitValue);
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
        System.out.println("Sorting on index " + index);
        Collections.sort(instances, (instance1, instance2) -> instance1.getFeatureValues().get(index).compareTo(instance2.getFeatureValues().get(index)));
    }

    private void sortOnSplitVariable(ArrayList<Feature> features, String splitOn) {
        if (splitOn.equalsIgnoreCase("GINI")) {
            Collections.sort(features, (feature1, feature2) -> Double.valueOf(feature1.giniValue).compareTo(feature2.giniValue));
        } else if (splitOn.equalsIgnoreCase("InfoGain")) {
            Collections.sort(features, (feature1, feature2) -> Double.valueOf(feature1.infoGain).compareTo(feature2.infoGain));
        }
    }

    private void print(ArrayList<Feature> features) {
        for (Feature feature : features) {
            System.out.println(feature.giniValue);
        }
    }


    public void classify(DataSet testDataset, TreeNode treeNode) {
        testDataset.instances.forEach(instance -> traverseTree(instance, treeNode));
    }

    private void traverseTree(Instance instance, TreeNode treeNode) {
        if (treeNode.isLeaf()) {
            instance.classifiedLabel = treeNode.label;
        } else {
            Feature feature = treeNode.feature;
            int index = feature.index;
            double splitValue = feature.splitValue;

            Double value = instance.featureValues.get(index);

            if (feature.getType().equalsIgnoreCase("Continuous")) {
                ContinuousTreeNode continuousTreeNode = (ContinuousTreeNode) treeNode;
                if (value <= splitValue) {
                    traverseTree(instance, continuousTreeNode.leftNode);
                } else {
                    traverseTree(instance, continuousTreeNode.rightNode);
                }
            }

        }
    }


}
