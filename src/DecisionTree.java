import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by jay on 2/12/16.
 */
public class DecisionTree {

    public String evalType;
    public int topTrees;
    public List<Instance> validationDataset;
    public DataSet dataset;
    public String splitOn;
    public String treeType;

    public DecisionTree(DataSet dataSet, String splitOn, String treeType, String evalType, int topTrees, List<Instance> validationDataset) {
        this.dataset = dataSet;
        this.splitOn = splitOn;
        this.treeType = treeType;
        this.evalType = evalType;
        this.topTrees = topTrees;
        this.validationDataset = validationDataset;
    }

    public TreeNode buildTree() {
        if (this.treeType.equalsIgnoreCase("complete") || this.treeType.equalsIgnoreCase("1")) {
            return getCompleteTree();
        } else if (this.treeType.equalsIgnoreCase("prune") || this.treeType.equalsIgnoreCase("2")) {
            return getPrunedTree();
        } else {
            return getParallelTree();
        }
    }

    public TreeNode getCompleteTree() {
        Queue<TreeNode> queue = new LinkedList<>();

        TreeNode root = getRootNode();

        queue.add(root);

        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();

            if (!node.isLeaf()) {
                makeChildNodes(node, queue);
            }
        }
        System.out.println("Finish");
        return root;
    }

    private TreeNode getRootNode() {
        TreeNode root;
        ArrayList<Instance> instances = this.dataset.getInstances();
        root = getNode(instances, null, null);
        root.recordsOnNode = this.dataset.instances;
        return root;
    }

    public TreeNode getPrunedTree() {

        int split = 0;

        Queue<TreeNode> queue = new LinkedList<>();
        HashMap<String, Integer> classLabelCount = getClassLabelCount(this.dataset.instances);

        int leafCount = 1;
        int misclassifiedCount = countMisclassified(classLabelCount);
        double initialScore = getScore(evalType, leafCount, misclassifiedCount, split, null);
//        System.out.println("initialScore = " + initialScore);

        TreeNode root = getRootNode();

        queue.add(root);
        double afterScore = initialScore;

        while (!queue.isEmpty()) {
            initialScore = afterScore;

            TreeNode node = queue.poll();

            if (!node.isLeaf()) {
                makeChildNodes(node, queue);
                split++;
                if (!node.isLeaf()) {
                    misclassifiedCount = getMisclassifiedCountAfterSplit(node);
                    afterScore = getScore(evalType, split + 1, misclassifiedCount, split, root);
//                    System.out.println("afterScore = " + afterScore);
                }
            }
            if (afterScore > initialScore) {
                makeCurrentNodeAsLeaf(node);
                removeLast(queue);
                removeLast(queue);
            }
        }
        System.out.println("Finish");
        return root;
    }

    private void removeLast(Queue<TreeNode> queue) {
        TreeNode first = queue.peek();
        TreeNode current;
        if (queue.size() == 1) {
            queue.poll();
        } else {
            while (true) {
                current = queue.poll();
                if (first == queue.peek()) {
                    break;
                }
                queue.add(current);
            }
        }
    }

    private double getScore(String evalType, int leafCount, int misclassifiedCount, int nodeCount, TreeNode root) {
        if (evalType.equalsIgnoreCase("Pessimistic Error") || evalType.equalsIgnoreCase("1")) {
            return getPessimisticScore(misclassifiedCount, leafCount, this.dataset.instances.size());
        } else if (evalType.equalsIgnoreCase("Validation Set") || evalType.equalsIgnoreCase("2")) {
            return getErrorOnValidationSet(root);
        } else {
            return getMDLPScore(misclassifiedCount, leafCount, nodeCount);
        }

    }

    private double getErrorOnValidationSet(TreeNode root) {
        List<Instance> validationInstances = this.validationDataset;

        if (root == null) {
            HashMap<String, Integer> classLabelCount = getClassLabelCount(this.dataset.instances);

            String label = getLabel(classLabelCount);

            int count = (int) validationInstances
                    .stream()
                    .filter(instance -> !instance.trueLabel.equalsIgnoreCase(label))
                    .count();

            return count * 100 / validationInstances.size();
        }
        for (Instance validationInstance : validationInstances) {
            traverseForValidation(validationInstance, root, null);
        }

        int count = (int) validationInstances.stream()
                .filter(validationInstance -> !validationInstance.trueLabel.equalsIgnoreCase(validationInstance.classifiedLabel))
                .count();

        return count * 100 / (double) validationInstances.size();
    }

    private void traverseForValidation(Instance validationInstance, TreeNode root, ContinuousTreeNode parent) {
        if (root == null) {
//            TreeNode parent = root.parentNode;
            HashMap<String, Integer> classLabelCount = this.getClassLabelCount((ArrayList<Instance>) parent.recordsOnNode);
            String label = getLabel(classLabelCount);
            validationInstance.classifiedLabel = label;
        } else {
            if (root.isLeaf()) {
                validationInstance.classifiedLabel = root.label;
            } else {
                Feature feature = root.feature;
                int index = feature.index;
                double splitValue = 0;
                if (feature.getType().equalsIgnoreCase("Continuous")) {
                    ContinuousTreeNode continuousTreeNode = (ContinuousTreeNode) root;
                    splitValue = continuousTreeNode.getSplitValue();
                }

                Double value = validationInstance.featureValues.get(index);

                if (feature.getType().equalsIgnoreCase("Continuous")) {
                    ContinuousTreeNode continuousTreeNode = (ContinuousTreeNode) root;
//                    ContinuousTreeNode parent = continuousTreeNode;
                    if (value <= splitValue) {
                        traverseForValidation(validationInstance, continuousTreeNode.leftNode, continuousTreeNode);
                    } else {
                        traverseForValidation(validationInstance, continuousTreeNode.rightNode, continuousTreeNode);
                    }
                }

            }
        }
    }

    private String getLabel(HashMap<String, Integer> classLabelCount) {
        int max = Integer.MIN_VALUE;
        String label = null;
        for (Map.Entry<String, Integer> stringIntegerEntry : classLabelCount.entrySet()) {
            int value = stringIntegerEntry.getValue();

            if (value > max) {
                max = value;
                label = stringIntegerEntry.getKey();
            }

        }
        return label;
    }

    private double getMDLPScore(int misclassifiedCount, int leafCount, int nodeCount) {
        return misclassifiedCount * Math.log(this.dataset.instances.size()) + leafCount * Math.log(this.dataset.classLabels.size()) + nodeCount * Math.log(this.dataset.totalFeatures);
    }

    public int getMisclassifiedCountAfterSplit(TreeNode node) {
        int count = 0;

        if (node.getFeature().getType().equalsIgnoreCase("Continuous")) {
            ContinuousTreeNode continuousTreeNode = (ContinuousTreeNode) node;
            if (null != continuousTreeNode.leftNode && !continuousTreeNode.leftNode.isLeaf()) {
                count += countMisclassified(continuousTreeNode.leftNode.countPerClassLabel);
            }

            if (null != continuousTreeNode.rightNode && !continuousTreeNode.rightNode.isLeaf()) {
                count += countMisclassified(continuousTreeNode.rightNode.countPerClassLabel);
            }
        }
        return count;
    }

    public int countMisclassified(HashMap<String, Integer> countLabel) {
        int count = 0;
        int max = Integer.MIN_VALUE;
        String key = "";

        Set<Map.Entry<String, Integer>> entries = countLabel.entrySet();

        for (Map.Entry<String, Integer> entry : entries) {
            int value = entry.getValue();
            if (value > max) {
                max = value;
                key = entry.getKey();
            }
        }

        List<Integer> collect = new ArrayList<>();
        for (Map.Entry<String, Integer> stringIntegerEntry : countLabel.entrySet()) {
            if (!stringIntegerEntry.getKey().equalsIgnoreCase(key)) {
                collect.add(stringIntegerEntry.getValue());
            }
        }

        for (Integer integer : collect) {
            count = count + integer;
        }
        return count;
    }

    public double getPessimisticScore(int misclassifiedCount, int leafCount, int size) {
        return (misclassifiedCount + leafCount * 0.5) / (double) (size);
    }

    public TreeNode getParallelTree() {

        ParallelTree parallelTree = new ParallelTree(this);
        return parallelTree.getNode();
    }


    private TreeNode getNode(ArrayList<Instance> instances, TreeNode parent, Feature feature) {
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
            node.probabilisticOutput = getProbability(node);
        } else {
            List<Feature> bestFeatures = getTopFeatures(instances, remainingFeatures);

            Feature bestFeature = bestFeatures.get(0);

            node = makeNode(bestFeature);
            node.setCountPerClassLabel(countMap);
            node.feature = bestFeature;
        }
        node.parentNode = parent;

        return node;
    }

    public ArrayList<ArrayList<Instance>> splitData(List<Instance> recordsOnNode, Feature bestFeature) {

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

    public List<Feature> getTopFeatures(ArrayList<Instance> instances, ArrayList<Feature> remainingFeatures) {
//        Feature feature;
        GiniSplit giniSplit;

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
//            feature = remainingFeatures.get(0);
        } else {
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
//            feature = remainingFeatures.get(0);
        }

        return remainingFeatures;
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
                if (splitOn.equalsIgnoreCase("GINI") || splitOn.equals("1")) {
                    giniSplit.giniValue = getGiniValue(giniSplit.splitValue, index);
                } else {
                    giniSplit.infoGain = getInfoGain(giniSplit.splitValue, index);
                }
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
            continuousTreeNode.leftNode = getNode(childDatasets.get(0), node, feature);
            continuousTreeNode.leftNode.recordsOnNode = childDatasets.get(0);
            queue.add(continuousTreeNode.leftNode);

            continuousTreeNode.rightNode = getNode(childDatasets.get(1), node, feature);
            continuousTreeNode.rightNode.recordsOnNode = childDatasets.get(1);
            queue.add(continuousTreeNode.rightNode);
        } else {
            makeCurrentNodeAsLeaf(node);
        }

    }

    public void makeCurrentNodeAsLeaf(TreeNode node) {
        node.isLeaf = true;
        HashMap<String, Integer> countPerClassLabel = getClassLabelCount((ArrayList<Instance>) node.recordsOnNode);

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
        node.probabilisticOutput = getProbability(node);
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

    public HashMap<String, Integer> getClassLabelCount(ArrayList<Instance> instances) {
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
        testDataset.instances.forEach(instance -> {
            traverseTree(instance, treeNode);
//            System.out.println(instance.getTrueLabel() + "   " + instance.getClassifiedLabel());
        });
    }

    private void traverseTree(Instance instance, TreeNode treeNode) {
        if (treeNode.isLeaf()) {
            instance.classifiedLabel = treeNode.label;
            instance.probabilisticOutput = getProbability(treeNode);
        } else {
            Feature feature = treeNode.feature;
            int index = feature.index;
            double splitValue = 0;
            if (feature.getType().equalsIgnoreCase("Continuous")) {
                ContinuousTreeNode continuousTreeNode = (ContinuousTreeNode) treeNode;
                splitValue = continuousTreeNode.getSplitValue();
            }

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

    private double getProbability(TreeNode treeNode) {
        HashMap<String, Integer> countPerClassLabel = treeNode.countPerClassLabel;

        int count = 0;
        if (countPerClassLabel.containsKey(this.dataset.positiveClass)) {
            count = countPerClassLabel.get(this.dataset.positiveClass);
        }
        return count / (double) treeNode.recordsOnNode.size();
    }

}
