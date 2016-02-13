import java.util.HashMap;
import java.util.List;

/**
 * Created by jay on 2/12/16.
 */
public class TreeNode {

    public TreeNode parentNode;
    public List<TreeNode> childNodes;
    public HashMap<String,Integer> countPerClassLabel;
    public DataSet dataSet;
    public Feature feature;

    public TreeNode(DataSet dataSet) {
        this.dataSet = dataSet;
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

    public List<TreeNode> getChildNodes() {
        return childNodes;
    }

    public void setChildNodes(List<TreeNode> childNodes) {
        this.childNodes = childNodes;
    }

    public HashMap<String, Integer> getCountPerClassLabel() {
        return countPerClassLabel;
    }

    public void setCountPerClassLabel(HashMap<String, Integer> countPerClassLabel) {
        this.countPerClassLabel = countPerClassLabel;
    }
}
