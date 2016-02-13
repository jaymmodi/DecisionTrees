import java.util.HashMap;
import java.util.List;

/**
 * Created by jay on 2/13/16.
 */
public class CategoricalFeature extends Feature {

    public List<Double> uniqueValues;
    public HashMap<Double,Integer> countPerUniqueValue;

    public CategoricalFeature(DataSet dataSet) {
        this.dataSet = dataSet;
    }

    public List<Double> getUniqueValues() {
        return uniqueValues;
    }

    public void setUniqueValues(List<Double> uniqueValues) {
        this.uniqueValues = uniqueValues;
    }

    public HashMap<Double, Integer> getCountPerUniqueValue() {
        return countPerUniqueValue;
    }

    public void setCountPerUniqueValue(HashMap<Double, Integer> countPerUniqueValue) {
        this.countPerUniqueValue = countPerUniqueValue;
    }
}
