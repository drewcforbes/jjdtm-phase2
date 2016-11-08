package superpeer;

import java.net.DatagramPacket;
import java.util.Map;

/**
 * Handler for all packets coming to the super peer
 */
public class SuperpeerClientRequestHandler implements Runnable {

    private final DatagramPacket incomingPacket;
    private final Map<String, String> localRoutingTable;
    private final PendingRequestHolder pendingRequestHolder;

    public SuperpeerClientRequestHandler(
            DatagramPacket incomingPacket,
            Map<String, String> localRoutingTable,
            PendingRequestHolder pendingRequestHolder) {

        this.incomingPacket = incomingPacket;
        this.localRoutingTable = localRoutingTable;
        this.pendingRequestHolder = pendingRequestHolder;
    }

    @Override
    public void run() {
        //TODO Handle incoming packets from a {@link ClientServer} here
    }
}
