import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

        String evalType = "";
        if (treeType.equalsIgnoreCase("Prune") || treeType.equalsIgnoreCase("2")) {
            System.out.println("Select the method to evaluate the overfitting prevention ");
            System.out.println("1. Pessimistic Error 2. Validation Set  3. MDLP");

            try {
                evalType = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        int topTrees = 0;
        if (treeType.equalsIgnoreCase("Parallel") || treeType.equalsIgnoreCase("3")) {
            System.out.println("Please enter a number to select top trees but less than " + dataSet.totalFeatures);

            try {
                topTrees = Integer.parseInt(br.readLine());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        runCrossValidation(dataSet, testDataset, splitOn, treeType, evalType, topTrees);
        System.out.println(" To get measures on whole data set the data has been changed to binary class variables");
        changeDataSetToBinary(dataSet, testDataset);
        trainAndTestOnBinary(dataSet, testDataset, splitOn, treeType, evalType);
        printMeasures(testDataset);
    }

    private static void printMeasures(DataSet testDataset) {
        int truePositive = 0;
        int falsePositive = 0;
        int trueNegative = 0;
        int falseNegative = 0;

        for (Instance instance : testDataset.instances) {
            if (instance.trueLabel.equals("1") && instance.classifiedLabel.equals("1")) {
                truePositive++;
            } else if (instance.trueLabel.equals("0") && instance.classifiedLabel.equals("0")) {
                trueNegative++;
            } else if (instance.trueLabel.equals("1") && instance.classifiedLabel.equals("0")) {
                falseNegative++;
            } else {
                falsePositive++;
            }
        }

        Measure allMeasures = new Measure(truePositive,trueNegative,falsePositive,falseNegative);

        System.out.println("falsePositive = " + allMeasures.falsePositive);
        System.out.println("falseNegative = " + allMeasures.falseNegative);
        System.out.println("trueNegative = " + allMeasures.trueNegative);
        System.out.println("truePositive = " + allMeasures.truePositive);
        System.out.println("getAccuracy() = " + allMeasures.getAccuracy());
        System.out.println("getBalancedAccuracy() = " + allMeasures.getBalancedAccuracy());
        System.out.println("getSensitivity() = " + allMeasures.getSensitivity());
        System.out.println("getSpecificity() = " + allMeasures.getSpecificity());
        System.out.println("getF1Score() = " + allMeasures.getF1Score());
    }

    private static void trainAndTestOnBinary(DataSet dataSet, DataSet testDataset, String splitOn, String treeType, String evalType) {
        DecisionTree decisionTree = new DecisionTree(dataSet, splitOn, treeType, evalType, 0, null);

        TreeNode treeNode = decisionTree.buildTree();

        decisionTree.classify(testDataset, treeNode);
    }

    private static void changeDataSetToBinary(DataSet dataSet, DataSet testDataset) {
        String classLabel = dataSet.classLabels.get(0);

        System.out.println("The problem has changed to classifying " + classLabel + " vs non - " + classLabel);

        for (Instance instance : dataSet.instances) {
            if (instance.trueLabel.equalsIgnoreCase(classLabel)) {
                instance.trueLabel = "1";
            } else {
                instance.trueLabel = "0";
            }
        }

        for (Instance instance : testDataset.instances) {
            if (instance.trueLabel.equalsIgnoreCase(classLabel)) {
                instance.trueLabel = "1";
            } else {
                instance.trueLabel = "0";
            }
        }


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

    private static void runCrossValidation(DataSet dataSet, DataSet testDataset, String splitOn, String treeType, String evalType, int topTrees) {

        int folds = 10;
        List<Instance> validationDataset = null;
        CrossValidation crossValidation = new CrossValidation(dataSet, testDataset, folds);

        for (int i = 1; i <= folds; i++) {
            System.out.println("--------------------- fold ------  " + i);
            crossValidation.getDataSetForCurrentFold(i);
            dataSet = crossValidation.getDataSet();
            if (evalType.equalsIgnoreCase("Validation Set") || evalType.equals("2")) {
                validationDataset = getValidationDataset(dataSet);
                dataSet.instances = (ArrayList<Instance>) getInstancesForTrain(dataSet);
            }
            testDataset = crossValidation.getTestDataset();

            TreeNode treeNode;
            DecisionTree decisionTree = new DecisionTree(dataSet, splitOn, treeType, evalType, topTrees, validationDataset);
            treeNode = decisionTree.buildTree();

            testDataset.features = dataSet.features;
            testDataset.totalFeatures = dataSet.totalFeatures;

            decisionTree.classify(testDataset, treeNode);

            calculateAccuracy(testDataset, crossValidation.foldAccuracy);

            getAverageAccuracy(crossValidation.foldAccuracy);
        }
    }

    private static void getAverageAccuracy(List<Double> foldAccuracy) {
        double avg = foldAccuracy
                .stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .getAsDouble();

        System.out.println("Average Accuracy after 10 fold Cross Validation " + avg);
    }

    private static List<Instance> getInstancesForTrain(DataSet dataSet) {
        int size = (int) (0.75 * dataSet.instances.size());

        return dataSet.instances
                .stream()
                .skip(0)
                .limit(size)
                .collect(Collectors.toList());
    }

    private static List<Instance> getValidationDataset(DataSet dataSet) {
        int size = dataSet.instances.size();

        int validationSize = (int) (0.25 * size);

        return dataSet.instances.stream()
                .skip(size - validationSize)
                .limit(validationSize)
                .collect(Collectors.toList());
    }

    private static void calculateAccuracy(DataSet testDataset, List<Double> foldAccuracy) {
        int count = (int) testDataset.instances.stream()
                .filter(instance -> instance.trueLabel.equalsIgnoreCase(instance.classifiedLabel))
                .count();

        double accuracy = count * 100 / (double) testDataset.instances.size();
        foldAccuracy.add(accuracy);
        System.out.println(accuracy);

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
