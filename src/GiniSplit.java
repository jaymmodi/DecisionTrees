/**
 * Created by jay on 2/16/16.
 */
public class GiniSplit {

    public double giniValue;
    public double splitValue;
    public double infoGain;

    public double getInfoGain() {
        return infoGain;
    }

    public void setInfoGain(double infoGain) {
        this.infoGain = infoGain;
    }

    public double getGiniValue() {
        return giniValue;
    }

    public void setGiniValue(double giniValue) {
        this.giniValue = giniValue;
    }

    public double getSplitValue() {
        return splitValue;
    }

    public void setSplitValue(double splitValue) {
        this.splitValue = splitValue;
    }
}
