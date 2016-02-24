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

    public ContinuousTreeNode(ContinuousTreeNode c,DataSet dataSet,TreeNode parent){
        super(dataSet);
        this.splitValue = c.splitValue;
        this.leftNode = null;
        this.rightNode = null;
        this.recordsOnNode = c.recordsOnNode;
        this.parentNode = parent;
        this.label = c.label;
        this.feature = c.feature;
        this.remainingFeatures = c.remainingFeatures;
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
