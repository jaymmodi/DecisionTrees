import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by jay on 2/12/16.
 */
public class DecisionTree {


    public DataSet dataset;
    public String splitOn;
    public TreeNode root;


    public DecisionTree(DataSet dataSet, String splitOn) {
        this.dataset = dataSet;
        this.splitOn = splitOn;
    }

    public void buildTree() {
        System.out.println("Making Tree.. finding node to split on");

        ArrayList<Feature> features = this.dataset.getFeatures();

        Feature feature = getFeatureToSplitOn(features);

        if (this.root == null) {
            this.root = new TreeNode(dataset);
        }
        this.root.setFeature(feature);


        //divide data set among child nodes

    }

    private Feature getFeatureToSplitOn(ArrayList<Feature> features) {
        findGiniValue(features);

        sortOnSplitVariable(features, this.splitOn); //sort in ascending order
        return features.get(0);
    }

    private void findGiniValue(ArrayList<Feature> features) {
        features.get(0).setGiniValue(1.2);
        features.get(1).setGiniValue(-1.2);
        features.get(2).setGiniValue(10.2);
        features.get(3).setGiniValue(100.2);

//        for (Feature feature : features) {
//            //find giniValue
//
//        }
    }

    private void sortOnSplitVariable(ArrayList<Feature> features, String splitOn) {
        if (splitOn.equalsIgnoreCase("GINI")) {
            Collections.sort(features, (feature1, feature2) -> {
                if (feature1.giniValue > feature2.giniValue) {
                    return 1;
                } else if (feature1.giniValue < feature2.giniValue) {
                    return -1;
                } else {
                    return 0;
                }
            });
        } else if (splitOn.equalsIgnoreCase("InfoGain")) {
            Collections.sort(features, (feature1, feature2) -> {
                if (feature1.infoGain > feature2.infoGain) {
                    return 1;
                } else if (feature1.infoGain < feature2.infoGain) {
                    return -1;
                } else {
                    return 0;
                }
            });
        }
    }

    private void print(ArrayList<Feature> features) {
        for (Feature feature : features) {
            System.out.println(feature.giniValue);
        }
    }
}
