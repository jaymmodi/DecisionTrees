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
            makeChildNodes(node,queue);
        }

        return root;
    }

    private void makeChildNodes(TreeNode node, Queue<TreeNode> queue) {
        Feature feature = node.feature;

        if(feature.getType().equalsIgnoreCase("Continuous")){
            makeContinuousChildNodes(node,queue);
        }else{
            makeCategoricalChildNodes(node,queue);
        }
    }

    private void makeCategoricalChildNodes(TreeNode node, Queue<TreeNode> queue) {

    }

    private void makeContinuousChildNodes(TreeNode node, Queue<TreeNode> queue) {
        ContinuousTreeNode continuousTreeNode = (ContinuousTreeNode) node;
        Feature feature = node.feature;

        continuousTreeNode.leftNode = getTreeNode(this.dataset,feature,node,"left");
        queue.add(continuousTreeNode.leftNode);

        continuousTreeNode.rightNode = getTreeNode(this.dataset,feature,node,"right");
        queue.add(continuousTreeNode.rightNode);
    }

    private TreeNode getTreeNode(DataSet dataset, Feature feature, TreeNode parent, String side) {
        HashMap<String, Integer> countMap = getClassLabelCount(dataset, feature.splitValue,side);

        ArrayList<Feature> remainingFeatures = (ArrayList<Feature>) this.dataset.getRemainingFeatures(feature);
        Feature bestFeature = getFeatureToSplitOn(remainingFeatures);

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

    private HashMap<String, Integer> getClassLabelCount(DataSet dataset, double splitValue, String side) {
        if(side.equalsIgnoreCase("root")){
            return null;
        }else if(side.equalsIgnoreCase("left")){
            return null;
        }else if(side.equalsIgnoreCase("right")){
            return null;
        }
        return null;
    }

    private Feature getFeatureToSplitOn(ArrayList<Feature> features) {
        findGiniValue(features);

        sortOnSplitVariable(features, this.splitOn); //sort in ascending order
        return features.get(0);
    }

    private void findGiniValue(ArrayList<Feature> features) {
        features.get(0).setGiniValue(1.2);
        features.get(1).setGiniValue(-1.2);
        features.get(2).setGiniValue(10.2);
        features.get(3).setGiniValue(100.2);

//        for (Feature feature : features) {
//            //find giniValue
//
//        }
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
