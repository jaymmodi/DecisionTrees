/**
 * Created by jay on 2/13/16.
 */
public class ContinuosFeature extends Feature {

    public double splitValue;

    public ContinuosFeature(DataSet dataSet) {
        this.dataSet = dataSet;
    }

    public double getSplitValue() {
        return splitValue;
    }

    public void setSplitValue(double splitValue) {
        this.splitValue = splitValue;
    }
}
