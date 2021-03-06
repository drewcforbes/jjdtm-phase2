package stats.clientserver;

import stats.CsvStat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientChapterGetStats implements CsvStat {

    private final List<String> keys = Arrays.asList(
            "Client getting file from Server time (ns)",
            "Total Chapter size (bytes)"
    );

    private List<Long> totalChapterGetTime = new ArrayList<>();
    private List<Long> totalChapterSize = new ArrayList<>();

    public void addTotalChapterGetTime(long time) {
        totalChapterGetTime.add(time);
    }

    public void addTotalChapterSize(long size) {
        totalChapterSize.add(size);
    }

    @Override
    public String getFileName() {
        return "clientChapterGetStats.csv";
    }

    @Override
    public List<String> getKeys() {
        return keys;
    }

    @Override
    public List<List<Long>> getValues() {
        return Arrays.asList(
                totalChapterGetTime,
                totalChapterSize
        );
    }
}
