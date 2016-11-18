package stats.clientserver;

import stats.CsvStat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServerRequestStats implements CsvStat {

    private final List<String> keys = Arrays.asList(
            "Total request time (ns)",
            "File read time (ns)",
            "Total file send time (ns)"
    );

    private List<Long> totalRequestTimes = new ArrayList<>();
    private List<Long> fileReadTimes = new ArrayList<>();
    private List<Long> totalFileSendTime = new ArrayList<>();

    public void addTotalRequestTimes(long time) {
         totalRequestTimes.add(time);
    }

    public void setFileReadTimes(long time) {
        fileReadTimes.add(time);
    }

    public void setTotalFileSendTime(long time) {
        totalFileSendTime.add(time);
    }

    @Override
    public String getFileName() {
        return "serverRequestStats.csv";
    }

    @Override
    public List<String> getKeys() {
        return keys;
    }

    @Override
    public List<List<Long>> getValues() {
        return Arrays.asList(
                totalRequestTimes,
                fileReadTimes,
                totalFileSendTime
        );
    }
}
