package superpeer;

import config.SuperpeerConfig;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Map;

/**
 * Handler for all packets coming to the super peer
 */
public class SuperpeerClientRequestHandler implements Runnable {

    private final static Integer SUPERPEER_PORT = 5556;
    private final static Integer SUPERPEER_CLIENT_SERVER_PORT = 5555;

    private final DatagramPacket incomingPacket;
    private final SuperpeerConfig config;
    private final PendingRequestHolder pendingRequestHolder;

    public SuperpeerClientRequestHandler(
            DatagramPacket incomingPacket,
            SuperpeerConfig config,
            PendingRequestHolder pendingRequestHolder) {

        this.incomingPacket = incomingPacket;
        this.config = config;
        this.pendingRequestHolder = pendingRequestHolder;
    }

    @Override
    public void run() {
        //Get the client address and message
        InetAddress clientAddr = incomingPacket.getAddress();
        String packetData = new String(incomingPacket.getData());
        Integer chapter = Integer.getInteger(packetData);

        Map<Integer, String> routingTable = config.getClientChapterLookupTable();

        long routingTableLookupStart = System.nanoTime();
        boolean hasIp = routingTable.containsKey(chapter);
        String clientIp = hasIp ? routingTable.get(chapter) : null;
        long routingTableLookupFinish = System.nanoTime();
        //TODO Routing table lookup stats

        //check local routing table for chapters
        if (hasIp) {
            try {
                //create the message and send it to the client
                byte[] bufferAry = clientIp.getBytes();
                DatagramSocket sock = new DatagramSocket();
                DatagramPacket pack = new DatagramPacket(bufferAry, bufferAry.length, incomingPacket.getAddress(), SUPERPEER_CLIENT_SERVER_PORT);
                sock.send(pack);
                sock.close();
            }
            catch (IOException e) {
                System.err.println("ERROR: SuperpeerClientRequestHandler: " + e.getMessage());
            }
        } else {
            // Add the pending request to the Map
            pendingRequestHolder.addPendingRequest(packetData, clientAddr.getHostAddress());
            try {
                //send chapter other superpeer
                byte[] bufferAry2 = packetData.getBytes();
                DatagramSocket sock = new DatagramSocket();
                DatagramPacket pack;

                for (String superpeer : config.getOtherSuperpeerIps()) {

                    try {
                        InetAddress superPeerInet = InetAddress.getByName(superpeer);
                        pack = new DatagramPacket(bufferAry2, bufferAry2.length, superPeerInet, SUPERPEER_PORT);
                        sock.send(pack);
                    } catch (IOException e) {
                        System.err.println("ERROR: SuperpeerClientRequestHandler: Problem sending request to other superpeers: " + e.getMessage());
                    }
                }

                sock.close();
            }
            catch (IOException e) {
                System.err.println("ERROR: SuperpeerClientRequestHandler: Problem closing socket: " + e.getMessage());
            }
        }
    }
}
