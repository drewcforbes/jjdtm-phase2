package initialization;

import java.io.*;


/**
 * Created by smyrna on 11/7/2016.
 */
public class DivineComedySplitter {

    /**
     * Unfinished method to create the individual files for the clientserver nodes.
     */
    public void createFiles() {
        try (BufferedReader br = new BufferedReader(new FileReader("WholeDivineComedyText.txt"))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            String newFileName = "Inferno_Canto_1";
            while (line != null) {
                try {
                    File file = new File(newFileName + ".txt");
                    if (file.createNewFile()){
                        System.out.println("File is created!");
                    } else{
                        System.out.println("File already exists.");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            String everything = sb.toString();
        } catch (FileNotFoundException e) {
            System.out.println("Exception in DivineComedySplitter.createFiles()");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Exception in DivineComedySplitter.createFiles()");
            e.printStackTrace();
        }
    }
}
