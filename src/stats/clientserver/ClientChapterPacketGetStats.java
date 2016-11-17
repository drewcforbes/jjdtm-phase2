package stats.clientserver;

import stats.CsvStat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientChapterPacketGetStats implements CsvStat {

    private final List<String> keys = Arrays.asList(
            "Individual file packet from Server time (ns)",
            "Individual file packet size (bytes)"
    );

    private List<Long> chapterPacketTransferTime = new ArrayList<>();
    private List<Long> chapterPacketSize = new ArrayList<>();

    public void addChapterPacketTransferTime(long time) {
        chapterPacketTransferTime.add(time);
    }

    public void addChapterPacketSize(long size) {
        chapterPacketSize.add(size);
    }

    @Override
    public String getFileName() {
        return "clientChapterPacketGet.txt";
    }

    @Override
    public List<String> getKeys() {
        return keys;
    }

    @Override
    public List<List<Long>> getValues() {
        return Arrays.asList(
                chapterPacketTransferTime,
                chapterPacketSize
        );
    }
}
