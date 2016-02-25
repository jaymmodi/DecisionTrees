import java.util.*;
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

        while (!allQueueEmpty) {
            mTrees.clear();
            for (Tree bestTree : bestMTrees) {

                Queue<TreeNode> queue = bestTree.getQueue();

                if (!queue.isEmpty()) {
                    TreeNode node = queue.poll();

                    if (!node.isLeaf) {
                        List<Tree> childMTrees = makeChildNodes(bestTree, node, queue);
                        if (childMTrees.size() == 0) {
                            mTrees.add(bestTree);
                        }
                        mTrees.addAll(childMTrees);
                    } else {
                        mTrees.add(bestTree);
                    }
                }
            }
            bestMTrees = selectBest(mTrees, this.decisionTree.topTrees);
            allQueueEmpty = checkLoopCondition(bestMTrees);
        }

        List<Tree> trees = selectBest(bestMTrees, 1);
        return trees.get(0).treeNode;
    }

    private List<Tree> makeChildNodes(Tree bestTree, TreeNode node, Queue<TreeNode> queue) {
        List<Tree> mTrees = new ArrayList<>();
        ContinuousTreeNode continuousTreeNode = (ContinuousTreeNode) node;
        Feature feature = continuousTreeNode.feature;
        ArrayList<ArrayList<Instance>> splitData = this.decisionTree.splitData(continuousTreeNode.recordsOnNode, feature);

        if (isDataSetZeroSize(splitData)) {
            //
            Tree tempTree = makeTree(bestTree, continuousTreeNode, null, "any");
            return Collections.singletonList(tempTree);
        }
        List<Feature> topMFeatures;
        if (continuousTreeNode.leftNode == null) {
            HashMap<String, Integer> classLabelCount = this.decisionTree.getClassLabelCount(splitData.get(0));
            if (classLabelCount.size() == 1) {
                continuousTreeNode.leftNode = getTreeNode(splitData.get(0), feature, continuousTreeNode);
                Tree tempTreeRight = makeTree(bestTree, continuousTreeNode, continuousTreeNode.leftNode, "left");
                return Collections.singletonList(tempTreeRight);
            }
            topMFeatures = getTopMFeatures(splitData.get(0), feature);
            makeMChildNodesForNode(bestTree, mTrees, continuousTreeNode, splitData, topMFeatures, "left");
        } else {
            HashMap<String, Integer> classLabelCount = this.decisionTree.getClassLabelCount(splitData.get(1));
            if (classLabelCount.size() == 1) {
                continuousTreeNode.rightNode = getTreeNode(splitData.get(1), feature, bestTree.treeNode);
                Tree tempTreeRight = makeTree(bestTree, continuousTreeNode, continuousTreeNode.rightNode, "right");
                return Collections.singletonList(tempTreeRight);
            }
            topMFeatures = getTopMFeatures(splitData.get(1), feature);
            makeMChildNodesForNode(bestTree, mTrees, continuousTreeNode, splitData, topMFeatures, "right");
        }

        return mTrees;
    }

    private void makeMChildNodesForNode(Tree bestTree, List<Tree> mTrees, ContinuousTreeNode continuousTreeNode, ArrayList<ArrayList<Instance>> splitData, List<Feature> topMFeatures, String side) {
        for (Feature topMFeature : topMFeatures) {
            TreeNode childNode;
            Tree tree;

            if (side.equalsIgnoreCase("left")) {
                childNode = getTreeNode(splitData.get(0), topMFeature, continuousTreeNode);
                continuousTreeNode.leftNode = childNode;
                tree = makeTree(bestTree, continuousTreeNode, childNode, side);
            } else {
                childNode = getTreeNode(splitData.get(1), topMFeature, continuousTreeNode);
                continuousTreeNode.rightNode = childNode;
                tree = makeTree(bestTree, continuousTreeNode, childNode, side);
            }

            mTrees.add(tree);
        }
    }

    private boolean isDataSetZeroSize(ArrayList<ArrayList<Instance>> splitData) {
        for (ArrayList<Instance> instances : splitData) {
            if (instances.size() == 0) {
                return true;
            }
        }
        return false;
    }

    private Tree makeTree(Tree bestTree, ContinuousTreeNode findThisNode, TreeNode childNode, String leftOrRight) {

        Tree tree = copyTree(bestTree);

        if (tree.queue == null) {
            tree.queue = new LinkedList<>();
            for (TreeNode treeNode : bestTree.queue) {
                TreeNode temp = findNode(tree, (ContinuousTreeNode) treeNode);
                tree.queue.add(temp);
            }
        }

        TreeNode parent = findNode(tree, findThisNode);
        if (childNode == null) {
            this.decisionTree.makeCurrentNodeAsLeaf(parent);
            parent.isLeaf = true;
            tree.splitCount = bestTree.splitCount - 1;
            tree.leafNodes = bestTree.leafNodes - 1;
        } else {

            if (leftOrRight.equalsIgnoreCase("left")) {
                if (parent != null) {
                    ((ContinuousTreeNode) parent).leftNode = childNode;
                }
            } else {
                if (parent != null) {
                    ((ContinuousTreeNode) parent).rightNode = childNode;
                }
            }

            if (parent != null && ((ContinuousTreeNode) parent).rightNode == null) {
                tree.queue.add(parent);
            }

            tree.queue.add(childNode);

            if (!childNode.isLeaf) {
                tree.splitCount = bestTree.splitCount + 1;
                tree.leafNodes = bestTree.leafNodes + 1;
            } else {
                tree.splitCount = bestTree.splitCount;
                tree.leafNodes = bestTree.leafNodes;
            }
        }
        return tree;
    }

    private TreeNode findNode(Tree tree, ContinuousTreeNode findThisNode) {
        Queue<TreeNode> queue = new LinkedList<>();

        queue.add(tree.treeNode);

        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();

            if (null != node) {
                ContinuousTreeNode continuousTreeNode = (ContinuousTreeNode) node;

                if (continuousTreeNode.splitValue == findThisNode.splitValue && continuousTreeNode.recordsOnNode.size() == findThisNode.recordsOnNode.size()) {
                    return node;
                } else {
                    queue.add(continuousTreeNode.leftNode);
                    queue.add(continuousTreeNode.rightNode);
                }
            }
        }

        return null;
    }

    private boolean checkLoopCondition(List<Tree> bestMTrees) {
        boolean allQueueEmpty = true;
        for (Tree bestMTree : bestMTrees) {
            allQueueEmpty = allQueueEmpty && bestMTree.getQueue().isEmpty();
        }

        return allQueueEmpty;
    }


    private List<Tree> selectBest(List<Tree> trees, int topNumber) {
        if (trees.size() == topNumber) {
            return new ArrayList<>(trees);
        }
        ArrayList<Tree> bestMTrees = new ArrayList<>();

        for (Tree tree : trees) {
//            TreeNode treeNode = tree.treeNode;
            int misclassifiedCount = traverseTree(tree);
            tree.score = this.decisionTree.getPessimisticScore(misclassifiedCount, tree.leafNodes, this.decisionTree.dataset.instances.size());
            bestMTrees.add(tree);
        }
        bestMTrees.sort((tree1, tree2) -> Double.valueOf(tree1.score).compareTo(tree2.score));
        return bestMTrees.stream()
                .skip(0)
                .limit(topNumber)
                .collect(Collectors.toList());
    }

    private int traverseTree(Tree tree) {
        Queue<TreeNode> queue = new LinkedList<>();

        queue.add(tree.treeNode);
        int count = 0;

        while (!queue.isEmpty()) {
            TreeNode treeNode = queue.poll();
            ContinuousTreeNode continuousTreeNode = (ContinuousTreeNode) treeNode;
            if (!continuousTreeNode.isLeaf) {
                if (continuousTreeNode.leftNode == null) {
                    //count
                    ArrayList<ArrayList<Instance>> lists = this.decisionTree.splitData(continuousTreeNode.recordsOnNode, continuousTreeNode.feature);
                    HashMap<String, Integer> classLabelCount = this.decisionTree.getClassLabelCount(lists.get(0));
                    count += this.decisionTree.countMisclassified(classLabelCount);

                } else {
                    queue.add(continuousTreeNode.leftNode);
                }

                if (continuousTreeNode.rightNode == null) {
                    ArrayList<ArrayList<Instance>> lists = this.decisionTree.splitData(continuousTreeNode.recordsOnNode, continuousTreeNode.feature);
                    HashMap<String, Integer> classLabelCount = this.decisionTree.getClassLabelCount(lists.get(1));
                    count += this.decisionTree.countMisclassified(classLabelCount);
                } else {
                    queue.add(continuousTreeNode.rightNode);
                }
            }
        }
        return count;
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
            for (Map.Entry<String, Integer> stringIntegerEntry : node.countPerClassLabel.entrySet()) {
                node.label = stringIntegerEntry.getKey();
            }
        }

        return node;
    }

    public Tree copyTree(Tree bestTree) {
        if (bestTree == null) {
            return null;
        }
        TreeNode root = copyAndMakeTree(bestTree.treeNode, null);
        Tree tree = new Tree();
        tree.treeNode = root;
        return tree;
    }

    private TreeNode copyAndMakeTree(TreeNode root, TreeNode parent) {
        if (root == null) {
            return null;
        }
        ContinuousTreeNode continuousRoot = (ContinuousTreeNode) root;
        ContinuousTreeNode continuousNewRoot = new ContinuousTreeNode(continuousRoot, this.decisionTree.dataset);
        continuousNewRoot.parentNode = parent;

        continuousNewRoot.leftNode = copyAndMakeTree(continuousRoot.leftNode, continuousNewRoot);
        continuousNewRoot.rightNode = copyAndMakeTree(continuousRoot.rightNode, continuousNewRoot);

        return continuousNewRoot;
    }
}
