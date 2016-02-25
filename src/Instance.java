import java.util.List;

/**
 * Created by jay on 2/10/16.
 */
public class Instance {

    int index;
    List<Double> featureValues;
    String trueLabel;
    String classifiedLabel;
    double probabilisticOutput;

    public double getProbabilisticOutput() {
        return probabilisticOutput;
    }

    public void setProbabilisticOutput(double probabilisticOutput) {
        this.probabilisticOutput = probabilisticOutput;
    }

    public String getClassifiedLabel() {
        return classifiedLabel;
    }

    public void setClassifiedLabel(String classifiedLabel) {
        this.classifiedLabel = classifiedLabel;
    }

    public String getTrueLabel() {
        return trueLabel;
    }

    public void setTrueLabel(String trueLabel) {
        this.trueLabel = trueLabel;
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
