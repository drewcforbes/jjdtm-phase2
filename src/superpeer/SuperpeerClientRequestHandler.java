package superpeer;

import config.SuperpeerConfig;
import stats.superpeer.SuperpeerRoutingTableLookupStats;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
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
    private final SuperpeerRoutingTableLookupStats superpeerRoutingTableLookupStats;

    public SuperpeerClientRequestHandler(
            DatagramPacket incomingPacket,
            SuperpeerConfig config,
            PendingRequestHolder pendingRequestHolder,
            SuperpeerRoutingTableLookupStats superpeerRoutingTableLookupStats
    ) {

        this.incomingPacket = incomingPacket;
        this.config = config;
        this.pendingRequestHolder = pendingRequestHolder;
        this.superpeerRoutingTableLookupStats = superpeerRoutingTableLookupStats;
    }

    @Override
    public void run() {
        //Get the client address and message
        String packetData = new String(incomingPacket.getData());
        System.out.println("Got client packet: " + packetData);
        String[] contents = packetData.split(" ");
        InetAddress clientAddr;
        try {
            clientAddr = InetAddress.getByName(contents[0]);
        } catch (UnknownHostException e) {
            System.err.println("FATAL: SuperpeerClientRequestHandler: Couldn't get client return address: " + e.getMessage());
            return;
        }
        Integer chapter = Integer.parseInt(contents[1].trim());

        System.out.println(config.getMyIp());

        Map<Integer, String> routingTable = config.getClientChapterLookupTable();

        long routingTableLookupStart = System.nanoTime();
        boolean hasIp = routingTable.containsKey(chapter);
        String clientIp = hasIp ? routingTable.get(chapter) : null;

        //check local routing table for chapters
        if (hasIp) {
            try {
                //create the message and send it to the client
                byte[] bufferAry = clientIp.getBytes();
                DatagramSocket sock = new DatagramSocket();
                DatagramPacket pack = new DatagramPacket(bufferAry, bufferAry.length, InetAddress.getByName(contents[0]), SUPERPEER_CLIENT_SERVER_PORT);
                sock.send(pack);
                sock.close();
                long routingTableLookupFinish = System.nanoTime();
                superpeerRoutingTableLookupStats.addRoutingTableLookupTime(routingTableLookupFinish - routingTableLookupStart);
            }
            catch (IOException e) {
                System.err.println("ERROR: SuperpeerClientRequestHandler: " + e.getMessage());
            }
        } else {
            // Add the pending request to the Map
            pendingRequestHolder.addPendingRequest(chapter.toString(), clientAddr.getHostAddress());
            try {
                //send chapter other superpeer
                byte[] bufferAry2 = (chapter + " " + config.getMyIp()).getBytes();
                DatagramSocket sock = new DatagramSocket();
                DatagramPacket pack;

                for (String superpeer : config.getOtherSuperpeerIps()) {

                    try {
                        InetAddress superPeerInet = InetAddress.getByName(superpeer);
                        pack = new DatagramPacket(bufferAry2, bufferAry2.length, superPeerInet, SUPERPEER_PORT);
                        System.out.println("Sent " + new String(pack.getData()) + " to other superpeer");
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
