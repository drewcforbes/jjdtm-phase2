package clientserver;

import java.io.FileInputStream;
import java.io.IOException;
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

        // TODO: Create a folder for files to serve
        // TODO: Fill the filestoserve folder with the appropriate files based on nodeId

        if (nodeId.equalsIgnoreCase("A1")) {

        } else if (nodeId.equalsIgnoreCase("A2")) {

        } else if (nodeId.equalsIgnoreCase("B1")) {

        } else if (nodeId.equalsIgnoreCase("B2")) {

        } else {
            LOGGER.severe("Error in application.properties. mymachine must be A1, A2, B1, or B2. mymachine was wrongly " +
                    "set to " + nodeId);
        }

        // TODO: Create a folder for files downloaded

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
}
