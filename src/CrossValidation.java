import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by jay on 2/21/16.
 */
public class CrossValidation {


    private final DataSet dataSet;
    private final DataSet testDataset;
    private final int folds;
    private List<ArrayList<Instance>> allLists;

    public CrossValidation(DataSet dataSet, DataSet testDataset, int folds) {

        this.dataSet = dataSet;
        this.testDataset = testDataset;
        this.folds = folds;
        splitData();
    }

    public DataSet getTestDataset() {
        return testDataset;
    }

    private void splitData() {
        this.allLists = new ArrayList<>();

        int start = 0;
        int end = this.dataSet.instances.size() / this.folds;

        while (start <= this.dataSet.instances.size() - end) {
            List<Instance> partialInstances = this.dataSet.instances
                    .stream()
                    .skip(start)
                    .limit(end)
                    .collect(Collectors.toList());

            this.allLists.add((ArrayList<Instance>) partialInstances);
            start += end;
        }

    }

    public void getDataSetForCurrentFold(int currentFold) {
        this.testDataset.instances = this.allLists.get(currentFold-1);

        ArrayList<Instance> instances = new ArrayList<>();

        for (int i = 0; i < allLists.size(); i++) {
            if (i != currentFold-1) {
                List<Instance> partialInstances = allLists.get(i);
                instances.addAll(partialInstances.stream().collect(Collectors.toList()));
            }
        }
        this.dataSet.instances = instances;
    }

    public DataSet getDataSet() {
        return dataSet;
    }
}
