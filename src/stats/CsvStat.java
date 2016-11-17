package stats;

import java.util.List;

public interface CsvStat {

    String getFileName();

    List<String> getKeys();

    List<List<Long>> getValues();
}
