package superpeer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Listens for all packets coming to the super peer.
 * Spawns new {@link SuperpeerClientRequestHandler} when a
 * packet is received
 */
public class SuperpeerClientServerListener implements Runnable {

    private final static int SUPER_TO_CLIENTSERVER_PEER_PORT = 5555;
    private final static int PACKET_SIZE = 1024; //In bytes

    @Override
    public void run() {
        System.out.println("Starting listening for ClientServer requests on port " + SUPER_TO_CLIENTSERVER_PEER_PORT);
        DatagramSocket socket;

        //Start listening
        try {
            socket = new DatagramSocket(SUPER_TO_CLIENTSERVER_PEER_PORT);
        } catch (SocketException e) {
            System.err.println("FATAL: Error while opening super peer socket on port " + SUPER_TO_CLIENTSERVER_PEER_PORT);
            throw new RuntimeException(e);
        }

        //Declare things needed for incoming messages
        byte[] buffer;
        DatagramPacket datagramPacket;

        while (true) {

            //Re-setup the datagram packet
            buffer = new byte[PACKET_SIZE];
            datagramPacket = new DatagramPacket(buffer, PACKET_SIZE);

            try {

                //Receive an incoming packet
                socket.receive(datagramPacket);

                //Handle the received packet
                new Thread(new SuperpeerClientRequestHandler(datagramPacket)).start();

            } catch (IOException e) {
                System.err.println("Error: Error while listening for incoming packet");
                System.err.println(e.getMessage());
                System.err.println("");
            }
        }
    }
}
