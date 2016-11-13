package setup;

import java.io.*;
import java.util.logging.Logger;


/**
 * Created by smyrna on 11/7/2016.
 */
public class DivineComedySplitter {

    private final Logger LOG = Logger.getLogger(this.getClass().toString());

    public static void main(String[] args) {
        DivineComedySplitter divineComedySplitter = new DivineComedySplitter();
        /*
        Creates a set of 100 chapters. It builds the files in the src directory instead of in /chapters. We'll move the files manually and keep this bug for now.
         */
        divineComedySplitter.createFiles();
        /*
        TODO: Move the files from src to chapters programatically.
         */
    }
    /**
     * Create the individual files for the ClientServer nodes.
     */
    private void createFiles() {
        try (BufferedReader br = new BufferedReader(new FileReader("src/setup/WholeDivineComedyText.txt"))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            String newFileName = "Chapter_1.txt";
            int cantoIndex = 0;
            while (line != null) {
                // if line contains canto header, make a new file and put the current stringbuilder into the new file,
                // unless it is the first appearance of ': Canto'
                if (line.contains(": Canto")) {
                    if (cantoIndex > 0) {
                        writeFile(newFileName, sb);
                        sb = new StringBuilder();
                        newFileName = "Chapter_" + cantoIndex + ".txt";
                    } else {
                        cantoIndex++;
                    }
                    cantoIndex++;
                }
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            // Write one last file for the one that was read from the last chapter heading up until the end of fil
            writeFile(newFileName, sb);
        } catch (FileNotFoundException e) {
            System.out.println("Exception in DivineComedySplitter.createFiles()");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Exception in DivineComedySplitter.createFiles()");
            e.printStackTrace();
        }

    }

    /**
     * Makes the new file and writes the contents of the StringBuilder to it.
     * @param newFileName
     * @param sb
     */
    private void writeFile(String newFileName, StringBuilder sb) {
        try {
            File file = new File(newFileName);
            if (file.createNewFile()) {
                LOG.info("File " + newFileName + " is created!");
            } else {
                LOG.warning("File " + newFileName + " already exists.");
            }
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file.getName()));
            bufferedWriter.write(sb.toString());
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
