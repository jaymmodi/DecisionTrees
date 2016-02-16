/**
 * Created by jay on 2/14/16.
 */
public class ContinuousTreeNode extends TreeNode {

    double splitValue;
    public TreeNode leftNode;    // <= splitValue
    public TreeNode rightNode;   // > splitValue

    public ContinuousTreeNode(DataSet dataSet) {
        super(dataSet);
        this.leftNode = null;
        this.rightNode = null;
    }

    public double getSplitValue() {
        return splitValue;
    }

    public void setSplitValue(double splitValue) {
        this.splitValue = splitValue;
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
