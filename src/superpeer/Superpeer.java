package superpeer;

import java.util.HashMap;
import java.util.Map;
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

    public static void main(String[] args) {

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


        //TODO Get this from config file
        return new HashMap<>();
    }
}
