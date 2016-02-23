import java.util.Queue;

/**
 * Created by jay on 2/22/16.
 */
public class Tree {

    public TreeNode treeNode;
    public Queue<TreeNode> queue;
    public double score;
    public int splitCount;
    public int leafNodes;

    public Tree() {
        this.splitCount = 1;
    }

    public int getSplitCount() {
        return splitCount;
    }

    public void setSplitCount(int splitCount) {
        this.splitCount = splitCount;
    }

    public int getLeafNodes() {
        return leafNodes;
    }

    public void setLeafNodes(int leafNodes) {
        this.leafNodes = leafNodes;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public TreeNode getTreeNode() {
        return treeNode;
    }

    public void setTreeNode(TreeNode treeNode) {
        this.treeNode = treeNode;
    }

    public Queue<TreeNode> getQueue() {
        return queue;
    }

    public void setQueue(Queue<TreeNode> queue) {
        this.queue = queue;
    }
}
