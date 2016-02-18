

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by jay on 2/12/16.
 */
public class DataSet {

    public int totalFeatures;
    public ArrayList<Feature> features;
    public ArrayList<String> classLabels;
    public String pathToFile;
    public ArrayList<Instance> instances;
    public Set<Feature> usedFeatures;

    public DataSet() {
        usedFeatures = new HashSet<>();
    }

    public ArrayList<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(ArrayList<Feature> features) {
        this.features = features;
    }

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

    public List<Feature> getRemainingFeatures(Feature feature){
        if(feature == null){
            return this.features;
        }
        usedFeatures.add(feature);

        return this.features.stream()
                .filter(p -> (!usedFeatures.contains(p)))
                .collect(Collectors.<Feature>toList());
    }
}
