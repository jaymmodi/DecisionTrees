import java.util.List;

/**
 * Created by jay on 2/10/16.
 */
public class Instance {

    int index;
    List<Double> featureValues;
    String classLabel;

    public String getClassLabel() {
        return classLabel;
    }

    public void setClassLabel(String classLabel) {
        this.classLabel = classLabel;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<Double> getFeatureValues() {
        return featureValues;
    }

    public void setFeatureValues(List<Double> featureValues) {
        this.featureValues = featureValues;
    }
}
