package stats;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.StringJoiner;

public class CsvStatHelper {

    public static void writeStat(CsvStat stat) {

        String statsDirectory = "stats";
        Path statsDirectoryPath = Paths.get(statsDirectory);

        String statFileString = statsDirectory + '/' + stat.getFileName();
        File statFile = new File(statFileString);
        PrintWriter writer = null;

        try {
            if (!Files.exists(statsDirectoryPath)) {
                Files.createDirectory(statsDirectoryPath);
            }

            if (!statFile.exists()) {
                if (!statFile.createNewFile()) {
                    throw new RuntimeException("Couldn't create new stats file");
                }

                //Write the keys to the new file
                List<String> keys = stat.getKeys();
                StringJoiner joiner = new StringJoiner(",");
                for (String key : keys) {
                    joiner.add(key);
                }
                writer = new PrintWriter(statFile);
                writer.println(joiner.toString());

            }
        } catch (IOException e) {
            System.err.println("FATAL: CsvStatHelper: Couldn't create stats directory or file: " + e.getMessage());
            return;
        }

        if (writer == null) {
            try {
                writer = new PrintWriter(statFile);
            } catch (FileNotFoundException e) {
                System.err.println("FATAL: CsvStatHelper: Couldn't create print writer: " + e.getMessage());
                return;
            }
        }

        //Get the length of the longest set of values
        int longest = 0;
        for (List<Long> values : stat.getValues()) {
            longest = Math.max(longest, values.size());
        }

        //Print the values to the file
        StringJoiner joiner;
        for (int i = 0; i < longest; i++) {

            //Stripe the data so that there's one value from
            // each set per line (or a blank placeholder)
            joiner = new StringJoiner(",");
            for (List<Long> values : stat.getValues()) {
                joiner.add(i >= values.size() ? " " : values.get(i).toString());
            }

            //Write the line to the file
            writer.println(joiner.toString());
        }
    }

    public static void writeAllStats(List<CsvStat> clientCsvStats) {
        for (CsvStat stat : clientCsvStats) {
            writeStat(stat);
        }
    }
}
