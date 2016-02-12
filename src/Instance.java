import java.util.List;

/**
 * Created by jay on 2/10/16.
 */
public class Instance {

    int index;
    List<Feature> row;
    String classLabel;

    public Instance(List<Feature> row) {
        this.row = row;
    }

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

    public List<Feature> getRow() {
        return row;
    }

    public void setRow(List<Feature> row) {
        this.row = row;
    }
}
