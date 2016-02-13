import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by jay on 2/10/16.
 */
public class ReadDataSet {

    public static void main(String[] args) {

        DataSet dataSet = new DataSet();
        readMetaData(dataSet);
        readData(dataSet);

        System.out.println("Total records = " + dataSet.getInstances().size());



    }

    private static DataSet readData(DataSet dataSet) {
        ArrayList<Instance> instances = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(dataSet.getPathToFile()));

            String line;
            int index = 1;

            while ((line = br.readLine()) != null) {
                Instance row = new Instance();
                List<Double> featureValues = new ArrayList<>();

                String perLine[] = line.split(",");

                for (int i = 0; i < dataSet.totalFeatures; i++) {
                    featureValues.add(Double.valueOf(perLine[i]));
                }

                row.setClassLabel(perLine[dataSet.totalFeatures]);
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
            dataSet.pathToFile = br.readLine();

            dataSet.totalFeatures = Integer.parseInt(br.readLine());

            String lineSplit[] = br.readLine().split(",");
            ArrayList<String> nameList = new ArrayList<>();
            Collections.addAll(nameList, lineSplit);
            dataSet.featureNames = nameList;

            lineSplit = br.readLine().split(",");
            ArrayList<String> featureType = new ArrayList<>();
            Collections.addAll(featureType, lineSplit);
            dataSet.featureTypes = featureType;

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
