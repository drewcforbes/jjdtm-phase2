package superpeer;

import java.net.DatagramPacket;
import java.util.Map;

/**
 * Handler for all packets coming to the super peer
 */
public class SuperpeerClientRequestHandler implements Runnable {

    private final DatagramPacket incomingPacket;
    private final Map<String, String> localRoutingTable;

    public SuperpeerClientRequestHandler(
            DatagramPacket incomingPacket,
            Map<String, String> localRoutingTable
    ) {
        this.incomingPacket = incomingPacket;
        this.localRoutingTable = localRoutingTable;
    }

    @Override
    public void run() {
        //TODO Handle incoming packets from a {@link ClientServer} here
    }
}
