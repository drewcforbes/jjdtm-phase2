package clientserver;

import java.util.Scanner;
import java.util.logging.Logger;

/**
 * Entry point for Client/Server.
 * Spawns a thread for listening for requests as a server
 * and a thread for making requests as a client.
 * Waits for 'exit' command
 */
public class ClientServer {

    private static Logger logger = Logger.getLogger(ClientServer.class.getName());

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        //Get the supernode's ip address
        System.out.print("Please enter the super node's IP address:");
        String supernodeIp = scanner.nextLine();
        System.out.println();

        //Start the client thread
        Thread clientThread = new Thread(new ClientRunnable(supernodeIp));
        clientThread.start();

        //Start the server thread to listen for incoming requests from clients
        Thread serverThread = new Thread(new ServerListener());
        serverThread.start();

        //Block for a stop command
        logger.info("Enter 'exit' to stop background processes");
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
