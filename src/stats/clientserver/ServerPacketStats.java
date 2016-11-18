package stats.clientserver;

import stats.CsvStat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServerPacketStats implements CsvStat {

    private final List<String> keys = Arrays.asList(
            "Packet send time (ns)",
            "Packet size (bytes)"
    );

    private List<Long> packetSendTimes = new ArrayList<>();
    private List<Long> packetSize = new ArrayList<>();

    public void addPacketSendTime(long time) {
        packetSendTimes.add(time);
    }

    public void addPacketSize(long size) {
        packetSize.add(size);
    }

    @Override
    public String getFileName() {
        return null;
    }

    @Override
    public List<String> getKeys() {
        return null;
    }

    @Override
    public List<List<Long>> getValues() {
        return Arrays.asList(
                packetSendTimes,
                packetSize
        );
    }
}
