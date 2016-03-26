/**
 * Created by jay on 2/25/16.
 */
public class Measure {
    public int truePositive;
    public int trueNegative;
    public int falsePositive;
    public int falseNegative;

    public Measure(int truePositive, int trueNegative, int falsePositive, int falseNegative) {

        this.truePositive = truePositive;
        this.trueNegative = trueNegative;
        this.falsePositive = falsePositive;
        this.falseNegative = falseNegative;
    }

    public int getTruePositive() {
        return truePositive;
    }

    public void setTruePositive(int truePositive) {
        this.truePositive = truePositive;
    }

    public int getTrueNegative() {
        return trueNegative;
    }

    public void setTrueNegative(int trueNegative) {
        this.trueNegative = trueNegative;
    }

    public int getFalsePositive() {
        return falsePositive;
    }

    public void setFalsePositive(int falsePositive) {
        this.falsePositive = falsePositive;
    }

    public int getFalseNegative() {
        return falseNegative;
    }

    public void setFalseNegative(int falseNegative) {
        this.falseNegative = falseNegative;
    }

    public double getAccuracy() {
        return (this.trueNegative + this.truePositive) * 100 / (this.falsePositive + this.falseNegative + this.truePositive + this.trueNegative);
    }

    public double getSensitivity() {
        return (this.truePositive) / (double) (this.truePositive + this.falseNegative);
    }

    public double getSpecificity() {
        return (this.trueNegative) / (double) (this.trueNegative + this.falsePositive);
    }

    public double getF1Score() {
        return (2 * this.truePositive) / (double) (2 * truePositive + this.falsePositive + this.falseNegative);
    }

    public double getBalancedAccuracy() {
        return ((getSensitivity() + getSpecificity()) / 2.0);
    }
}

