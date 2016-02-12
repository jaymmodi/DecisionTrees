/**
 * Created by jay on 2/12/16.
 */
public class Feature {

    String name;
    double value;
    double giniValue;
    double infoGain;

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getGiniValue() {
        return giniValue;
    }

    public void setGiniValue(double giniValue) {
        this.giniValue = giniValue;
    }

    public double getInfoGain() {
        return infoGain;
    }

    public void setInfoGain(double infoGain) {
        this.infoGain = infoGain;
    }
}
