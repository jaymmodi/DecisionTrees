import java.util.HashMap;

/**
 * Created by jay on 2/12/16.
 */
public class TreeNode {

    public TreeNode parentNode;
    public HashMap<String,Integer> countPerClassLabel;
    public DataSet dataSet;
    public Feature feature;
    boolean isLeaf;

    public boolean isLeaf() {
        return isLeaf;
    }

    public void setLeaf(boolean leaf) {
        isLeaf = leaf;
    }

    public Feature getFeature() {
        return feature;
    }

    public void setFeature(Feature feature) {
        this.feature = feature;
    }

    public TreeNode getParentNode() {
        return parentNode;
    }

    public void setParentNode(TreeNode parentNode) {
        this.parentNode = parentNode;
    }

    public HashMap<String, Integer> getCountPerClassLabel() {
        return countPerClassLabel;
    }

    public void setCountPerClassLabel(HashMap<String, Integer> countPerClassLabel) {
        this.countPerClassLabel = countPerClassLabel;
    }
}
