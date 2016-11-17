package stats;

import java.util.Arrays;
import java.util.List;

public class ClientServerStats implements CsvStat {

    private final List<String> keys = Arrays.asList(new String[] {

    });

    @Override
    public String getFileName() {
        return "clientserverStats.txt";
    }

    @Override
    public List<String> getKeys() {
        return null;
    }

    @Override
    public List<List<Object>> getValues() {
        return null;
    }
}
