import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Created by jay on 2/12/16.
 */
public class DataSet {

    public int totalFeatures;
    public ArrayList<Feature> features;
    public ArrayList<String> classLabels;
    public String pathToTrainFile;
    public ArrayList<Instance> instances;

    public DataSet() {
    }

    public ArrayList<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(ArrayList<Feature> features) {
        this.features = features;
    }

    public String getPathToTrainFile() {
        return pathToTrainFile;
    }

    public void setPathToTrainFile(String pathToTrainFile) {
        this.pathToTrainFile = pathToTrainFile;
    }

    public int getTotalFeatures() {
        return totalFeatures;
    }

    public void setTotalFeatures(int totalFeatures) {
        this.totalFeatures = totalFeatures;
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

    public ArrayList<Feature> getRemainingFeatures(Feature feature) {
        if (feature == null) {
            return this.features.stream()
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        return this.features.stream()
                .filter(p -> (!p.getName().equals(feature.getName())))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
