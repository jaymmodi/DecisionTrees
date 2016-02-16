import java.util.HashMap;
import java.util.List;

/**
 * Created by jay on 2/12/16.
 */
public class TreeNode {

    public TreeNode parentNode;
    public HashMap<String,Integer> countPerClassLabel;
    public DataSet dataSet;
    public Feature feature;
    public boolean isLeaf;
    public List<Feature> remainingFeatures;

    public TreeNode(DataSet dataset) {
        this.dataSet = dataset;
    }

    public List<Feature> getRemainingFeatures() {
        return remainingFeatures;
    }

    public void setRemainingFeatures(List<Feature> remainingFeatures) {
        this.remainingFeatures = remainingFeatures;
    }

    public boolean isLeaf() {
        return (this.countPerClassLabel.size() == 1);
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
