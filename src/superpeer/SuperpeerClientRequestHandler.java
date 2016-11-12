package superpeer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Map;

/**
 * Handler for all packets coming to the super peer
 */
public class SuperpeerClientRequestHandler implements Runnable {

    private final DatagramPacket incomingPacket;
    //<chapter#, ipAddress>
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
        //Get the client address and message
        InetAddress clientAddr = incomingPacket.getAddress();
        String chapter = new String(incomingPacket.getData());
        //TODO change this address
        String superPeer = "192.0.0.1";

        //check local routing table for chapters
        if (localRoutingTable.containsKey(chapter)) {
            try {
                //create the message and send it to the client
                byte[] bufferAry = localRoutingTable.get(chapter).getBytes();
                DatagramSocket sock = new DatagramSocket();
                DatagramPacket pack = new DatagramPacket(bufferAry, bufferAry.length, incomingPacket.getAddress(), 5555);
                sock.send(pack);
                sock.close();
            }
            catch (IOException e) {
                System.err.println(e);
            }
        }
        else {
            // Add the pending request to the Map
            pendingRequestHolder.addPendingRequest(chapter, clientAddr.getHostAddress());
            try {
                //send chapter other superpeer
                byte[] bufferAry2 = chapter.getBytes();
                DatagramSocket sock2 = new DatagramSocket();
                InetAddress superPeerInet = InetAddress.getByName(superPeer);
                DatagramPacket pack2 = new DatagramPacket(bufferAry2, bufferAry2.length, superPeerInet, 5556);
                sock2.send(pack2);
                sock2.close();
            }
            catch (IOException e) {
                System.err.println(e);
            }
        }
    }
}
