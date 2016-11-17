package stats.clientserver;

import stats.CsvStat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientSuperpeerQueryStats implements CsvStat {

    private final List<String> keys = Arrays.asList(
            "Super Peer IP Query Time (ns)"
    );

    private List<Long> superpeerIpQueryTime = new ArrayList<>();

    public void addSuperpeerIpQueryTime(long time) {
        superpeerIpQueryTime.add(time);
    }

    @Override
    public String getFileName() {
        return "clientSuperpeerQueryStats.txt";
    }

    @Override
    public List<String> getKeys() {
        return keys;
    }

    @Override
    public List<List<Long>> getValues() {
        return Arrays.asList(
                superpeerIpQueryTime
        );
    }
}
