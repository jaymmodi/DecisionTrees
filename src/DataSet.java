import java.util.ArrayList;

/**
 * Created by jay on 2/12/16.
 */
public class DataSet {

    public int totalFeatures;
    public ArrayList<String> featureNames;
    public ArrayList<String> featureTypes;
    public ArrayList<String> classLabels;
    public String pathToFile;
    public ArrayList<Instance> instances;

    public String getPathToFile() {
        return pathToFile;
    }

    public void setPathToFile(String pathToFile) {
        this.pathToFile = pathToFile;
    }

    public int getTotalFeatures() {
        return totalFeatures;
    }

    public void setTotalFeatures(int totalFeatures) {
        this.totalFeatures = totalFeatures;
    }

    public ArrayList<String> getFeatureNames() {
        return featureNames;
    }

    public void setFeatureNames(ArrayList<String> featureNames) {
        this.featureNames = featureNames;
    }

    public ArrayList<String> getFeatureTypes() {
        return featureTypes;
    }

    public void setFeatureTypes(ArrayList<String> featureTypes) {
        this.featureTypes = featureTypes;
    }

    public ArrayList<String> getClassLabels() {
        return classLabels;
    }

    public void setClassLabels(ArrayList<String> classLabels) {
        this.classLabels = classLabels;
    }

    public ArrayList<Instance> getInstances() {
        return instances;
    }

    public void setInstances(ArrayList<Instance> instances) {
        this.instances = instances;
    }
}
