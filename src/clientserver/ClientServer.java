package clientserver;

import config.ClientServerConfig;

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

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        ClientServerConfig config = new ClientServerConfig();

        //Start the client thread
        Thread clientThread = new Thread(new ClientRunnable(config));
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
