package superpeer;

import config.SuperpeerConfig;
import stats.CsvStat;
import stats.superpeer.SuperpeerLookupRequestStats;
import stats.superpeer.SuperpeerRoutingTableLookupStats;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.List;

public class SuperpeerToSuperpeerListener implements Runnable {

    private final static int SUPERPEER_TO_SUPERPEER_PORT = 5556;
    private final static int PACKET_SIZE = 1024; //In bytes

    private final SuperpeerConfig config;
    private final PendingRequestHolder pendingRequestHolder;

    private final SuperpeerLookupRequestStats lookupRequestStats;
    private final SuperpeerRoutingTableLookupStats routingTableLookupStats;

    public SuperpeerToSuperpeerListener(
            SuperpeerConfig config,
            PendingRequestHolder pendingRequestHolder,
            SuperpeerRoutingTableLookupStats routingTableLookupStats,
            SuperpeerLookupRequestStats lookupRequestStats
    ) {
        this.config = config;
        this.pendingRequestHolder = pendingRequestHolder;
        this.routingTableLookupStats = routingTableLookupStats;
        this.lookupRequestStats = lookupRequestStats;
    }

    @Override
    public void run() {
        System.out.println("Starting listening for superpeer requests on port " + SUPERPEER_TO_SUPERPEER_PORT);
        DatagramSocket socket;

        //Start listening
        try {
            socket = new DatagramSocket(SUPERPEER_TO_SUPERPEER_PORT);
        } catch (SocketException e) {
            System.err.println("FATAL: Error while opening super peer socket on port " + SUPERPEER_TO_SUPERPEER_PORT);
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
                new Thread(new SuperpeerToSuperpeerRequestHandler(
                        datagramPacket,
                        config,
                        routingTableLookupStats,
                        lookupRequestStats,
                        pendingRequestHolder
                )).start();

            } catch (IOException e) {
                System.err.println("Error: Error while listening for incoming packet");
                System.err.println(e.getMessage());
                System.err.println("");
            }
        }
    }

    public List<CsvStat> getAllCsvStats() {
        return Arrays.asList(
                lookupRequestStats,
                routingTableLookupStats
        );
    }

}
