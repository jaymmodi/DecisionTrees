/**
 * Created by jay on 2/14/16.
 */
public class ContinuousTreeNode extends TreeNode {

    double value;
    public TreeNode leftNode;
    public TreeNode rightNode;

    public ContinuousTreeNode(DataSet dataSet) {
        this.dataSet = dataSet;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public TreeNode getLeftNode() {
        return leftNode;
    }

    public void setLeftNode(TreeNode leftNode) {
        this.leftNode = leftNode;
    }

    public TreeNode getRightNode() {
        return rightNode;
    }

    public void setRightNode(TreeNode rightNode) {
        this.rightNode = rightNode;
    }
}
