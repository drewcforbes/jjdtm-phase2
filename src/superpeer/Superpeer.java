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

        //Start the listening thread
        Thread listeningThread = new Thread(new SuperpeerListener());
        listeningThread.start();

        //Block for 'exit' command
        System.out.println();
        System.out.println("Enter 'exit' to stop background processes");
        while (!console.readLine().toLowerCase().equals("exit")) {}

        //Stop listening thread
        listeningThread.interrupt();
        System.exit(0);

    }
}
