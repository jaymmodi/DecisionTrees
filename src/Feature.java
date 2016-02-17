/**
 * Created by jay on 2/12/16.
 */
public class Feature {

    String name;
    String type;
    double giniValue;
    double infoGain;
    int index;
    double splitValue;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
