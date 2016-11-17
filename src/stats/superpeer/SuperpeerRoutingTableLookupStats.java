package stats.superpeer;

import stats.CsvStat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SuperpeerRoutingTableLookupStats implements CsvStat {

    private final List<String> keys = Arrays.asList(
            "Superpeer routing table lookup (ns)"
    );

    private List<Long> routingTableLookupTimes = new ArrayList<>();

    public void addRoutingTableLookupTime(long time) {
        routingTableLookupTimes.add(time);
    }

    @Override
    public String getFileName() {
        return "superpeerRoutingTableLookup.csv";
    }

    @Override
    public List<String> getKeys() {
        return keys;
    }

    @Override
    public List<List<Long>> getValues() {
        return null;
    }
}
