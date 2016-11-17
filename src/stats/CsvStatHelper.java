package stats;

import java.io.*;
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

        //Print the values to the file
        StringJoiner joiner;
        for (List<Object> values : stat.getValues()) {
            joiner = new StringJoiner(",");

            for (Object val : values) {
                joiner.add(val.toString());
            }

            writer.println(joiner.toString());
        }
    }
}
