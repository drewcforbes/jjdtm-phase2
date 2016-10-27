package superpeer;

import java.io.Console;

/**
 * Super peer entry point
 * Starts listening threads and waits for an 'exit' command
 */
public class Superpeer {

    public static void main(String[] args) {

        //Get the console
        Console console = System.console();
        if (console == null) {
            System.err.println("FATAL: No console available");
            System.exit(1);
        }

        //Start the ClientServer listening thread
        Thread clientServerListeningThread = new Thread(new SuperpeerClientServerListener());
        clientServerListeningThread.start();

        //Start the Superpeer listening thread
        Thread superpeerListeningThread = new Thread(new SuperpeerToSuperpeerListener());
        superpeerListeningThread.start();

        //Block for 'exit' command
        System.out.println("Enter 'exit' to stop background processes");
        while (!console.readLine().toLowerCase().equals("exit")) {}

        //Stop listening threads
        clientServerListeningThread.interrupt();
        superpeerListeningThread.interrupt();
        System.exit(0);

    }
}
