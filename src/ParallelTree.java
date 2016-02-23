import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

/**
 * Created by jay on 2/22/16.
 */
public class ParallelTree {

    public DecisionTree decisionTree;

    public ParallelTree(DecisionTree decisionTree) {
        this.decisionTree = decisionTree;
    }

    public TreeNode getNode() {

        List<Feature> topMFeatures = getTopMFeatures(this.decisionTree.dataset.instances, null);
        List<Tree> mTrees = makeFirstMTrees(topMFeatures, this.decisionTree.dataset.instances, null);
        List<Tree> bestMTrees = selectBest(mTrees, this.decisionTree.topTrees);

        boolean allQueueEmpty = checkLoopCondition(bestMTrees);
        int leftOrRight = 1;

        while (!allQueueEmpty) {
            for (Tree bestTree : bestMTrees) {
                Queue<TreeNode> queue = bestTree.getQueue();

                TreeNode node = queue.poll();

                if (!node.isLeaf) {
                    List<Tree> childMTrees = splitChild(bestTree, node, queue, leftOrRight);
                    mTrees.addAll(childMTrees);
                } else {
                    mTrees.add(bestTree);
                }
            }
            bestMTrees = selectBest(mTrees, this.decisionTree.topTrees);
            allQueueEmpty = checkLoopCondition(bestMTrees);
        }

        List<Tree> trees = selectBest(bestMTrees, 1);
        return trees.get(0).treeNode;
    }

    private List<Tree> splitChild(Tree bestTree, TreeNode node, Queue<TreeNode> queue, int leftOrRight) {
        //           update queue
        List<Tree> mTrees = new ArrayList<>();
        ContinuousTreeNode continuousTreeNode = (ContinuousTreeNode) node;
        Feature feature = continuousTreeNode.feature;
        ArrayList<ArrayList<Instance>> splitData = this.decisionTree.splitData(continuousTreeNode.recordsOnNode, feature);

        List<Feature> topMFeatures;
        if (leftOrRight % 2 != 0) {
            topMFeatures = getTopMFeatures(splitData.get(0), feature);
        } else {
            topMFeatures = getTopMFeatures(splitData.get(1), feature);
        }

        for (Feature topMFeature : topMFeatures) {
            TreeNode childNode;

            if (leftOrRight % 2 != 0) {
                childNode = getTreeNode(splitData.get(0), topMFeature, continuousTreeNode);
                continuousTreeNode.leftNode = childNode;
            } else {
                childNode = getTreeNode(splitData.get(1), topMFeature, continuousTreeNode);
                continuousTreeNode.rightNode = childNode;
            }

            Tree tree = makeTree(bestTree, childNode);
            mTrees.add(tree);
        }
        return mTrees;
    }

    private Tree makeTree(Tree bestTree, TreeNode childNode) {
        Tree tree = new Tree();

        tree.treeNode = bestTree.treeNode;
        if (tree.queue == null) {
            tree.queue = new LinkedList<>();
        }
        tree.queue.add(childNode);
        tree.splitCount = bestTree.splitCount + 1;
        tree.leafNodes = bestTree.leafNodes + 1;

        return tree;
    }

    private boolean checkLoopCondition(List<Tree> bestMTrees) {
        boolean allQueueEmpty = true;
        for (Tree bestMTree : bestMTrees) {
            allQueueEmpty = allQueueEmpty && bestMTree.getQueue().isEmpty();
        }

        return allQueueEmpty;
    }


    private List<Tree> selectBest(List<Tree> trees, int topNumber) {
        if (trees.size() == this.decisionTree.topTrees) {
            return trees;
        }
        ArrayList<Tree> bestMTrees = new ArrayList<>();

        for (Tree tree : trees) {
            TreeNode treeNode = tree.treeNode;
            int misclassifiedCount = this.decisionTree.getMisclassifiedCountAfterSplit(treeNode);
            tree.score = this.decisionTree.getPessimisticScore(misclassifiedCount, tree.leafNodes, this.decisionTree.dataset.instances.size());
            bestMTrees.add(tree);
        }
        bestMTrees.sort((tree1, tree2) -> Double.valueOf(tree1.score).compareTo(tree2.score));
        return bestMTrees.stream()
                .skip(0)
                .limit(topNumber)
                .collect(Collectors.toList());
    }

    private List<Feature> getTopMFeatures(ArrayList<Instance> instances, Feature feature) {
        List<Feature> remainingFeatures = this.decisionTree.dataset.getRemainingFeatures(feature);

        List<Feature> topFeatures = this.decisionTree.getTopFeatures(instances, (ArrayList<Feature>) remainingFeatures);

        return topFeatures.subList(0, this.decisionTree.topTrees);
    }

    private List<Tree> makeFirstMTrees(List<Feature> topMFeatures, ArrayList<Instance> instances, TreeNode parent) {
        List<Tree> mTrees = new ArrayList<>();

        for (Feature topMFeature : topMFeatures) {
            Tree tree = new Tree();
            tree.treeNode = getTreeNode(instances, topMFeature, parent);
            tree.splitCount++;
            tree.leafNodes = tree.splitCount + 1;
            if (tree.queue == null) {
                tree.queue = new LinkedList<>();
            }
            tree.queue.add(tree.treeNode);
            mTrees.add(tree);
        }
        return mTrees;
    }

    private TreeNode getTreeNode(ArrayList<Instance> instances, Feature topFeature, TreeNode parent) {
        TreeNode node = new ContinuousTreeNode(this.decisionTree.dataset);
        ContinuousTreeNode continuousTreeNode = (ContinuousTreeNode) node;
        continuousTreeNode.feature = topFeature;
        continuousTreeNode.parentNode = parent;
        continuousTreeNode.countPerClassLabel = this.decisionTree.getClassLabelCount(instances);
        continuousTreeNode.recordsOnNode = instances;
        continuousTreeNode.splitValue = topFeature.splitValue;

        if (node.countPerClassLabel.size() == 1) {
            node.isLeaf = true;
        }

        return node;
    }
}
