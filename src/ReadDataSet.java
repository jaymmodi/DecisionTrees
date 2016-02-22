import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by jay on 2/10/16.
 */
public class ReadDataSet {

    public static void main(String[] args) {

        DataSet dataSet = new DataSet();
        DataSet testDataset = new DataSet();

        readMetaData(dataSet);

        readData(dataSet, dataSet.pathToTrainFile);

        System.out.println("Please provide the criteria to split on ");
        System.out.println(" 1. GINI 2. InfoGain");

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String splitOn = getSplitOnVariable(br);

        System.out.println("Please select a type of tree ");
        System.out.println(" 1. Complete 2. Prune 3. Parallel");

        String treeType = getTreeType(br);

        String evalType = null;
        if(treeType.equalsIgnoreCase("Prune") || treeType.equalsIgnoreCase("2")){
            System.out.println("Select the method to evaluate the overfitting prevention ");
            System.out.println("1. Pessimistic Error 2. Validation Set  3. MDLP");

            try {
                evalType = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        runCrossValidation(dataSet, testDataset, splitOn, treeType,evalType);
    }

    private static String getTreeType(BufferedReader br) {
        String treeType = null;
        try {
            treeType = br.readLine();
            treeType = validateTreeType(treeType);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return treeType;
    }

    private static String validateTreeType(String treeType) {
        if (treeType.equalsIgnoreCase("Complete") || treeType.equals("1")) {
            return "Complete";
        } else if (treeType.equalsIgnoreCase("Prune") || treeType.equals("2")) {
            return "prune";
        } else {
            return "parallel";
        }
    }

    private static String getSplitOnVariable(BufferedReader br) {
        String splitOn = null;
        try {
            splitOn = br.readLine();
            if (splitOn.equals("exit")) {
                System.exit(1);
            }
            splitOn = validateInput(splitOn);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return splitOn;
    }

    private static void runCrossValidation(DataSet dataSet, DataSet testDataset, String splitOn, String treeType,String evalType) {
        int folds = 10;
        CrossValidation crossValidation = new CrossValidation(dataSet, testDataset, folds);

        for (int i = 1; i <=folds; i++) {
            System.out.println("--------------------- fold ------  " + i);
            crossValidation.getDataSetForCurrentFold(i);
            dataSet = crossValidation.getDataSet();
            testDataset = crossValidation.getTestDataset();

            TreeNode treeNode;
            DecisionTree decisionTree = new DecisionTree(dataSet, splitOn,treeType,evalType);
            treeNode = decisionTree.buildTree();

            testDataset.features = dataSet.features;
            testDataset.totalFeatures = dataSet.totalFeatures;

            decisionTree.classify(testDataset, treeNode);

            calculateAccuracy(testDataset);
        }
    }

    private static void calculateAccuracy(DataSet testDataset) {
        int count = (int) testDataset.instances.stream()
                .filter(instance -> instance.trueLabel.equalsIgnoreCase(instance.classifiedLabel))
                .count();

        System.out.println(count * 100 / (double) testDataset.instances.size());

    }

    private static String validateInput(String splitOn) {
        if (splitOn.equalsIgnoreCase("GINI") || splitOn.equals("1")) {
            return "GINI";
        } else if (splitOn.equalsIgnoreCase("InfoGain") || splitOn.equals("2")) {
            return "InfoGain";
        } else {
            return "exit";
        }
    }

    private static DataSet readData(DataSet dataSet, String path) {
        ArrayList<Instance> instances = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));

            String line;
            int index = 1;

            while ((line = br.readLine()) != null) {
                Instance row = new Instance();
                List<Double> featureValues = new ArrayList<>();

                String perLine[] = line.split("\t");

                for (int i = 0; i < dataSet.totalFeatures; i++) {
                    featureValues.add(Double.valueOf(perLine[i]));
                }

                row.setTrueLabel(perLine[dataSet.totalFeatures]);
                row.setFeatureValues(featureValues);
                row.setIndex(index);

                instances.add(row);

                ++index;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        dataSet.setInstances(instances);

        return dataSet;
    }

    private static DataSet readMetaData(DataSet dataSet) {
        try {
            BufferedReader br = new BufferedReader(new FileReader("metadata"));
            ArrayList<Feature> features = new ArrayList<>();

            dataSet.pathToTrainFile = br.readLine();

            dataSet.totalFeatures = Integer.parseInt(br.readLine());

            String lineSplit[] = br.readLine().split(",");
            ArrayList<String> nameList = new ArrayList<>();
            Collections.addAll(nameList, lineSplit);

            lineSplit = br.readLine().split(",");
            ArrayList<String> featureType = new ArrayList<>();
            Collections.addAll(featureType, lineSplit);

            for (int i = 0; i < nameList.size(); i++) {
                Feature feature;

                feature = featureType.get(i).equalsIgnoreCase("continuous") ? new ContinuousFeature() : new CategoricalFeature();
                feature.name = nameList.get(i);
                feature.type = featureType.get(i);
                feature.index = i;

                features.add(feature);
            }
            dataSet.setFeatures(features);

            lineSplit = br.readLine().split(",");
            ArrayList<String> classLabels = new ArrayList<>();
            Collections.addAll(classLabels, lineSplit);
            dataSet.classLabels = classLabels;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return dataSet;
    }
}
