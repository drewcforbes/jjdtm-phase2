package clientserver;

import config.ClientServerConfig;
import stats.CsvStatHelper;
import stats.clientserver.ServerPacketStats;
import stats.clientserver.ServerRequestStats;

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

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        ClientServerConfig config = new ClientServerConfig();
        ServerPacketStats serverPacketStats = new ServerPacketStats();
        ServerRequestStats serverRequestStats = new ServerRequestStats();

        //Start the client thread
        ClientRunnable clientRunnable = new ClientRunnable(config);
        Thread clientThread = new Thread(clientRunnable);

        //Start the server thread to listen for incoming requests from clients
        Thread serverThread = new Thread(new ServerListener(config, serverPacketStats, serverRequestStats));
        serverThread.start();

        //Block for a stop command
        LOGGER.info("Enter 'start' to start the client or 'exit' to stop background processes");
        while (true) {
            String input = scanner.nextLine().toLowerCase();

            if (input.equals("start")) {
                if (clientThread.isAlive()) {
                    System.out.println("Client already running");
                } else {
                    System.out.println("Starting ClientRunnable");
                    clientThread.start();
                }
            } else if (input.equals("exit")) {
                break;
            } else {
                System.out.println("Command not recognized. Please enter 'start' or 'exit'");
            }
        }

        //Stop the background threads (if they're still running)
        if (clientThread.isAlive()) {
            clientThread.interrupt();
        }
        if (serverThread.isAlive()) {
            serverThread.interrupt();
        }

        //Write the stats we've gained
        CsvStatHelper.writeAllStats(clientRunnable.getClientCsvStats());

        System.exit(0);
    }
}
