package superpeer;

import java.net.DatagramPacket;
import java.util.Map;

public class SuperpeerToSuperpeerRequestHandler implements Runnable {

    private final DatagramPacket incomingPacket;
    private final Map<String, String> localRoutingTable;

    public SuperpeerToSuperpeerRequestHandler(
            DatagramPacket incomingPacket,
            Map<String, String> localRoutingTable
    ) {
        this.incomingPacket = incomingPacket;
        this.localRoutingTable = localRoutingTable;
    }

    @Override
    public void run() {
        //TODO Handle incoming superpeer to superpeer requests
    }
}
