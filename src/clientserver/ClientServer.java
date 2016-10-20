package clientserver;

import java.io.Console;

/**
 * Entry point for Client/Server.
 * Spawns a thread for listening for requests as a server
 * and a thread for making requests as a client.
 * Waits for 'exit' command
 */
public class ClientServer {

    public static void main(String[] args) {

        //Get the console
        Console console = System.console();
        if (console == null) {
            System.err.println("FATAL: No console available");
            System.exit(1);
        }

        //Get the supernode's ip address
        System.out.print("Please enter the super node's IP address:");
        String supernodeIp = console.readLine();
        System.out.println();

        //Start the client thread
        Thread clientThread = new Thread(new ClientRunnable(supernodeIp));
        clientThread.start();

        //Start the server thread to listen for incoming requests from clients
        Thread serverThread = new Thread(new ServerListener());
        serverThread.start();

        //Block for a stop command
        System.out.println("Enter 'exit' to stop background processes");
        while (!console.readLine().toLowerCase().equals("exit")) {}

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
