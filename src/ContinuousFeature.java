/**
 * Created by jay on 2/13/16.
 */
public class ContinuousFeature extends Feature {

    public double splitValue;

    public ContinuousFeature(DataSet dataSet) {
        this.dataSet = dataSet;
    }

    public double getSplitValue() {
        return splitValue;
    }

    public void setSplitValue(double splitValue) {
        this.splitValue = splitValue;
    }
}
