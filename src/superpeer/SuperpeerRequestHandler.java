package superpeer;

import java.net.DatagramPacket;

/**
 * Handler for all packets coming to the super peer
 */
public class SuperpeerRequestHandler implements Runnable {

    private final DatagramPacket incomingPacket;

    public SuperpeerRequestHandler(DatagramPacket incomingPacket) {
        this.incomingPacket = incomingPacket;
    }

    @Override
    public void run() {
        //TODO Handle incoming packet here
    }
}
