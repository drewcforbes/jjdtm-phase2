package setup;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

/**
 * Created by smyrna on 11/21/2016.
 */
public class ChapterMultiplierUtil {
    /*
    Written to quickly create a larger set of files for testing
     */
    private final Logger LOG = Logger.getLogger(this.getClass().toString());

    public static void main(String[] args) {
        ChapterMultiplierUtil chapterMultiplierUtil = new ChapterMultiplierUtil();
        int iterations = 100;
        chapterMultiplierUtil.multiplyChapters(iterations);
    }

    private void multiplyChapters(int iterations) {
        /*
        Clear and recreate the chapters_enlarged directory
         */
        String chaptersEnlargedDirectory = "../chapters_enlarged";
        try {
            Path chaptersEnlargedPath = Paths.get(chaptersEnlargedDirectory);
            File chaptersEnlargedFile = chaptersEnlargedPath.toFile();
            if (chaptersEnlargedFile.exists()) {
                for (File file : chaptersEnlargedFile.listFiles()) {
                    file.delete();
                }
            }
            Files.deleteIfExists(chaptersEnlargedPath);
            Files.createDirectory(chaptersEnlargedPath);
            LOG.info("Created directory " + chaptersEnlargedDirectory);
        } catch (IOException e) {
            LOG.warning("Couldn't delete/create directory: " + e.getMessage());
            return;
        }

        /*
        Read contents of chapters
         */
        String chaptersSourceDirectory = "../chapters";
        Path chaptersSourcePath = Paths.get(chaptersSourceDirectory);
        File chaptersSourceFile = chaptersSourcePath.toFile();
        try {
            if (chaptersSourceFile.exists()) {
                LOG.info("Found chapters source file.");
                for (File file : chaptersSourceFile.listFiles()) {
                    // Read the file
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    StringBuilder inputStringBuilder = new StringBuilder();
                    try {
                        String sCurrentLine;
                        while ((sCurrentLine = br.readLine()) != null) {
                            inputStringBuilder.append(sCurrentLine);
                        }
                        br.close();
                    } catch (IOException e) {
                        LOG.warning("IOException occurred reading file");
                        e.printStackTrace();
                    }

                    // Make a new file
                    FileOutputStream output;
                    try {
                        File outputFile = Files.createFile(Paths.get(chaptersEnlargedDirectory + '/' + file.getName())).toFile();
                        output = new FileOutputStream(outputFile);
                    } catch (IOException e) {
                        LOG.warning("Error creating the output file for " + file.getName() + ": " + e.getMessage());
                        return;
                    }

                    // Copy the file to the new location <iterations> number of times
                    for (int i = 0; i < iterations; i++) {
                        try {
                            output.write(inputStringBuilder.toString().getBytes());
                        } catch (IOException e) {
                            LOG.warning("Error writing to file.");
                            e.printStackTrace();
                        }
                    }

                    // Close the output stream
                    try {
                        output.flush();
                    } catch (IOException e) {
                        LOG.warning("Error closing output stream");
                        e.printStackTrace();
                    }
                }

            }
        } catch (FileNotFoundException fnfe) {
            LOG.warning("FileNotFoundException occurred in ChapterMultiplierUtil");
            fnfe.printStackTrace();
            return;
        }
    }

}
