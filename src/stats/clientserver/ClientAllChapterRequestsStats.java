package stats.clientserver;

import stats.CsvStat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientAllChapterRequestsStats implements CsvStat {

    private final List<String> keys = Arrays.asList(
            "Total time to get all chapters (ns)",
            "Total number of requests"
    );

    private final List<Long> totalTimesForAllChapters = new ArrayList<>();
    private final List<Long> totalNumberOfChapters = new ArrayList<>();

    public void addTotalTimeForAllChapters(long time, long numberOfChapters) {
        totalTimesForAllChapters.add(time);
        totalNumberOfChapters.add(numberOfChapters);
    }

    @Override
    public String getFileName() {
        return "clientAllChapterRequests.csv";
    }

    @Override
    public List<String> getKeys() {
        return keys;
    }

    @Override
    public List<List<Long>> getValues() {
        return Arrays.asList(
                totalTimesForAllChapters,
                totalNumberOfChapters
        );
    }
}
