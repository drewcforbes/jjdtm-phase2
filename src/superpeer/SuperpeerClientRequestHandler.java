package superpeer;

import java.net.DatagramPacket;

/**
 * Handler for all packets coming to the super peer
 */
public class SuperpeerClientRequestHandler implements Runnable {

    private final DatagramPacket incomingPacket;

    public SuperpeerClientRequestHandler(DatagramPacket incomingPacket) {
        this.incomingPacket = incomingPacket;
    }

    @Override
    public void run() {
        //TODO Handle incoming packets from a {@link ClientServer} here
    }
}
