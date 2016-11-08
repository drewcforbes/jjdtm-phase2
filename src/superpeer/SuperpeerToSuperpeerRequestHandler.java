package superpeer;

import java.net.DatagramPacket;
import java.util.Map;

public class SuperpeerToSuperpeerRequestHandler implements Runnable {

    private final DatagramPacket incomingPacket;
    private final Map<String, String> localRoutingTable;
    private final PendingRequestHolder pendingRequestHolder;

    public SuperpeerToSuperpeerRequestHandler(
            DatagramPacket incomingPacket,
            Map<String, String> localRoutingTable,
            PendingRequestHolder pendingRequestHolder) {

        this.incomingPacket = incomingPacket;
        this.localRoutingTable = localRoutingTable;
        this.pendingRequestHolder = pendingRequestHolder;
    }

    @Override
    public void run() {
        //TODO Handle incoming superpeer to superpeer requests
    }
}
