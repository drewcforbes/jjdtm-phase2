package stats.superpeer;

import stats.CsvStat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SuperpeerLookupRequestStats implements CsvStat {

    private final List<String> keys = Arrays.asList(
            "Superpeer to Superpeer IP address lookup (ns)"
    );

    private final List<Long> superpeerLookupRequestTimes = new ArrayList<>();

    public void addSuperpeerLookupRequestTime(long time) {
        superpeerLookupRequestTimes.add(time);
    }

    @Override
    public String getFileName() {
        return "superpeerLookupRequestStats.csv";
    }

    @Override
    public List<String> getKeys() {
        return keys;
    }

    @Override
    public List<List<Long>> getValues() {
        return Arrays.asList(
                superpeerLookupRequestTimes
        );
    }
}
