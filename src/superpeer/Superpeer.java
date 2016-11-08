package superpeer;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Super peer entry point
 * Starts listening threads and waits for an 'exit' command
 */
public class Superpeer {

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
        System.out.println("Enter 'exit' to stop background processes");
        Scanner scanner = new Scanner(System.in);
        while (!scanner.nextLine().toLowerCase().equals("exit")) {}

        //Stop listening threads
        clientServerListeningThread.interrupt();
        superpeerListeningThread.interrupt();
        System.exit(0);

    }

    private static Map<String, String> getLocalRoutingTable() {

        //TODO Get this from config file
        return new HashMap<>();
    }
}
