import java.util.HashMap;
import java.util.List;

/**
 * Created by jay on 2/12/16.
 */
public class TreeNode {

    public TreeNode parentNode;
    public List<TreeNode> childNodes;
    public List<Instance> dataOnNode;
    public String name;
    public HashMap<String,Integer> countPerClassLabel;
    public DataSet dataSet;

    
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

    public List<Instance> getDataOnNode() {
        return dataOnNode;
    }

    public void setDataOnNode(List<Instance> dataOnNode) {
        this.dataOnNode = dataOnNode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, Integer> getCountPerClassLabel() {
        return countPerClassLabel;
    }

    public void setCountPerClassLabel(HashMap<String, Integer> countPerClassLabel) {
        this.countPerClassLabel = countPerClassLabel;
    }
}
