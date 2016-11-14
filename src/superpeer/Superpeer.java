package superpeer;

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

        //Setup dependencies of the listeners
        Map<String, String> localRoutingTable = getLocalRoutingTable();
        PendingRequestHolder pendingRequestHolder = new PendingRequestHolder();

        //Start the ClientServer listening thread
        Thread clientServerListeningThread = new Thread(
                new SuperpeerClientServerListener(localRoutingTable, pendingRequestHolder)
        );
        clientServerListeningThread.start();

        //Start the Superpeer listening thread
        Thread superpeerListeningThread = new Thread(
                new SuperpeerToSuperpeerListener(localRoutingTable, pendingRequestHolder)
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

    /**
     * Gets a map where the keys are the list of ClientServers on this Superpeer and the values are their IP addresses.
     *
     * @return
     */
    private static Map<String, String> getLocalRoutingTable() {
        String clientServer1Name;
        String clientServer2Name;
        String clientServer1IPAddress;
        String clientServer2IPAddress;

        if (nodeId.equalsIgnoreCase("SPA")) {
            clientServer1Name = "A1";
            clientServer2Name = "A2";
        } else if (nodeId.equalsIgnoreCase("SPB")) {
            clientServer1Name = "B1";
            clientServer2Name = "B2";
        } else {
            LOGGER.severe("Error in application.properties. mymachine must be SPA or SPB. mymachine was wrongly set" +
                    "to " + nodeId);
            return new HashMap<>();
        }
        clientServer1IPAddress = properties.getProperty("clientserver1ip");
        clientServer2IPAddress = properties.getProperty("clientserver2ip");

        Map<String, String> localRoutingTable = new HashMap<String, String>();
        localRoutingTable.put(clientServer1Name, clientServer1IPAddress);
        localRoutingTable.put(clientServer2Name, clientServer2IPAddress);

        LOGGER.info("Loaded configs for Superpeer: \n" + "NodeId:\t" + nodeId +
                "\nClientServer1:\t" + clientServer1Name + "\t" + clientServer1IPAddress +
                "\nClientServer2:\t" + clientServer2Name + "\t" + clientServer2IPAddress);

        return localRoutingTable;
    }
}
