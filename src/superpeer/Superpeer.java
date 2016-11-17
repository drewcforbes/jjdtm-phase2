package superpeer;

import config.SuperpeerConfig;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * Super peer entry point.
 * Starts listening threads and waits for an 'exit' command.
 * <p>
 * Before you run a Superpeer on your computer, get the IP addresses of any ClientServers that will be categorized under
 * your Superpeer and add them to src/config/network.properties .
 */
public class Superpeer {

    private static final Logger LOGGER = Logger.getLogger(Superpeer.class.getName());

    private static Properties properties = new Properties();

    private static String nodeId;

    public static void main(String[] args) {

        //Setup dependencies of the listeners
        PendingRequestHolder pendingRequestHolder = new PendingRequestHolder();
        SuperpeerConfig config = new SuperpeerConfig();

        //Start the ClientServer listening thread
        Thread clientServerListeningThread = new Thread(
                new SuperpeerClientServerListener(config, pendingRequestHolder)
        );
        clientServerListeningThread.start();

        //Start the Superpeer listening thread
        Thread superpeerListeningThread = new Thread(
                new SuperpeerToSuperpeerListener(config, pendingRequestHolder)
        );
        superpeerListeningThread.start();

        //Block for 'exit' command
        LOGGER.info("Enter 'exit' to stop background processes");
        Scanner scanner = new Scanner(System.in);
        while (!scanner.nextLine().toLowerCase().equals("exit")) {
        }

        //Stop listening threads
        clientServerListeningThread.interrupt();
        superpeerListeningThread.interrupt();
        System.exit(0);

    }
}
