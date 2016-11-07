package initialization;

import java.io.*;


/**
 * Created by smyrna on 11/7/2016.
 */
public class DivineComedySplitter {

    public static void main (String[] args) {
        createFiles();
    }

    /**
     * Unfinished ethod to create the individual files for the ClientServer nodes.
     */
    public static void createFiles() {
    //TODO: Get output written to three different folders for the different books
        try (BufferedReader br = new BufferedReader(new FileReader("src/initialization/WholeDivineComedyText.txt"))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            String newFileName = "Inferno_Canto_1";
            int cantoIndex = 0;
            File file;
            BufferedWriter bufferedWriter;
            while (line != null) {
                // if line contains canto header, make a new file and put the current stringbuilder into the new file,
                // unless it is the first appearance of ': Canto'
                if (line.contains(": Canto") && cantoIndex != 0) {
                    try {
                        file = new File(newFileName + ".txt");
                        if (file.createNewFile()) {
                            System.out.println("File is created!");
                        } else {
                            System.out.println("File already exists.");
                        }
                        bufferedWriter = new BufferedWriter(new FileWriter(file.getName()));
                        bufferedWriter.write(sb.toString());
                        sb = new StringBuilder();
                        newFileName = line;
                        newFileName.replace(' ', '_');
                        newFileName.replace(":", "");

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                sb.append(line);
                sb.append(System.lineSeparator());

                cantoIndex++;
                line = br.readLine();
            }
        } catch (FileNotFoundException e) {
            System.out.println("Exception in DivineComedySplitter.createFiles()");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Exception in DivineComedySplitter.createFiles()");
            e.printStackTrace();
        }
    }
}
