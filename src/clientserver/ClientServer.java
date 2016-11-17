package clientserver;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * Entry point for Client/Server.
 * Spawns a thread for listening for requests as a server
 * and a thread for making requests as a client.
 * Waits for 'exit' command
 */
public class ClientServer {

    private static final Logger LOGGER = Logger.getLogger(ClientServer.class.getName());

    private static Properties properties = new Properties();

    private static String nodeId;

    public static void main(String[] args) {
        /*
        Read properties to use as configs.
         */
        try {
            properties.load(new FileInputStream("src/application.properties"));
        } catch (IOException e) {
            LOGGER.warning("Failed to load properties file.");
            e.printStackTrace();
        }
        nodeId = properties.getProperty("mymachine");

        setUpFileSystem();

        Scanner scanner = new Scanner(System.in);

        //Get the supernode's ip address
        LOGGER.info("Please enter the super node's IP address:");
        String supernodeIp = scanner.nextLine();

        //Start the client thread
        Thread clientThread = new Thread(new ClientRunnable(supernodeIp));
        clientThread.start();

        //Start the server thread to listen for incoming requests from clients
        Thread serverThread = new Thread(new ServerListener());
        serverThread.start();

        //Block for a stop command
        LOGGER.info("Enter 'exit' to stop background processes");
        while (!scanner.nextLine().toLowerCase().equals("exit")) {}

        //Stop the background threads (if they're still running)
        if (clientThread.isAlive()) {
            clientThread.interrupt();
        }
        if (serverThread.isAlive()) {
            serverThread.interrupt();
        }

        System.exit(0);
    }
    private static void setUpFileSystem(){
        //Fill the filestoserve folder with the appropriate files based on nodeId
        int numberOfChaptersPerNode = Integer.parseInt(properties.getProperty("chaptersperclientserver"));

        int startingIndex = 1;

        if (nodeId.equalsIgnoreCase("A1")) {
            startingIndex = 1;
        } else if (nodeId.equalsIgnoreCase("A2")) {
            startingIndex = 1 + 1 * numberOfChaptersPerNode;
        } else if (nodeId.equalsIgnoreCase("B1")) {
            startingIndex = 1 + 2 * numberOfChaptersPerNode;
        } else if (nodeId.equalsIgnoreCase("B2")) {
            startingIndex = 1 + 3 * numberOfChaptersPerNode;
        } else {
            LOGGER.severe("Error in application.properties. mymachine must be A1, A2, B1, or B2. mymachine was wrongly " +
                    "set to " + nodeId);
            System.exit(0);
        }

        Path currentDirectoryPath = Paths.get("");
        String currentDirectory = currentDirectoryPath.toAbsolutePath().toString();

        // Create a folder for files to serve
        File filesToServeFolder = new File(currentDirectory + File.separator + "filestoserve");
        deleteExistingFolder(filesToServeFolder);
        filesToServeFolder.mkdir();

        // Create a folder for files downloaded
        File filesDownloadedFolder = new File(currentDirectory + File.separator + "filesdownloaded");
        deleteExistingFolder(filesDownloadedFolder);
        filesDownloadedFolder.mkdir();

        // Put the appropriate files in the filestoserve folder
        int endingIndex = startingIndex + numberOfChaptersPerNode;
        // Actually endingIndex plus 1, e.g. 101 to stop with chapter 100
        for(int i = startingIndex; i < endingIndex; i++) {
            copyChapterToFilesToServe(i);
        }

    }

    /**
     * Deletes an existing folder
     */
    private static void deleteExistingFolder(File folderToDelete) {
        String[]entries = folderToDelete.list();
        if (entries != null) {
            for (String s : entries) {
                File currentFile = new File(folderToDelete.getPath(), s);
                currentFile.delete();
            }
        }
    }

    private static void copyChapterToFilesToServe(int chapterNumber) {
        Path currentDirectoryPath = Paths.get("");
        String currentDirectory = currentDirectoryPath.toAbsolutePath().toString();

        File sourceChapter = new File(currentDirectory + File.separator + "chapters" + File.separator + "Chapter_" +
                chapterNumber + ".txt");
        File copiedFile = new File(currentDirectory + File.separator + "filestoserve" + File.separator + "Chapter_" +
                chapterNumber + ".txt");

        InputStream inputStream;
        OutputStream outputStream;
        try {
            inputStream = new FileInputStream(sourceChapter);
            outputStream = new FileOutputStream(copiedFile);

            //copy the file content in bytes
            byte[] buffer = new byte[1024];
            int readLength;
            while ((readLength = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, readLength);
            }

            inputStream.close();
            outputStream.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
