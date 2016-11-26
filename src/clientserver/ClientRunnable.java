package clientserver;

import config.ClientServerConfig;
import stats.CsvStat;
import stats.clientserver.ClientAllChapterRequestsStats;
import stats.clientserver.ClientChapterGetStats;
import stats.clientserver.ClientChapterPacketStats;
import stats.clientserver.ClientSuperpeerQueryStats;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Runnable to make requests to the super peer and
 * other clients
 */
public class ClientRunnable implements Runnable {

    private final static int CLIENT_AND_SUPERNODE_PORT = 5555;
    private final static int CLIENT_SERVER_PORT = 5554;

    private static final Logger LOGGER = Logger.getLogger(ClientRunnable.class.getName());

    private final ClientServerConfig config;
    private final List<CsvStat> clientCsvStats;

    private final ClientChapterPacketStats clientChapterPacketStats;
    private final ClientChapterGetStats clientChapterGetStats;
    private final ClientSuperpeerQueryStats clientSuperpeerQueryStats;
    private final ClientAllChapterRequestsStats clientAllChapterRequestsStats;

    public ClientRunnable(ClientServerConfig config) {
        this.config = config;

        this.clientChapterPacketStats = new ClientChapterPacketStats();
        this.clientChapterGetStats = new ClientChapterGetStats();
        this.clientSuperpeerQueryStats = new ClientSuperpeerQueryStats();
        this.clientAllChapterRequestsStats = new ClientAllChapterRequestsStats();

        clientCsvStats = Arrays.asList(
                clientChapterGetStats,
                clientChapterPacketStats,
                clientSuperpeerQueryStats,
                clientAllChapterRequestsStats
        );
    }

    @Override
    public void run() {

        long totalTimeStart = System.nanoTime();
        boolean anyFailure = false;

        List<Integer> neededChapters = config.getChaptersNeeded();

        //Print the list of files to download to the console
        StringBuilder stringBuilder = new StringBuilder();
        for (Integer entry : neededChapters) {
            stringBuilder.append(entry).append(", ");
        }
        LOGGER.info("ClientServer: Will download chapters " + stringBuilder.toString());

        DatagramSocket sock;
        try {
            sock = new DatagramSocket(CLIENT_AND_SUPERNODE_PORT);
        } catch (SocketException e) {
            System.err.println("FATAL: ClientRunnable: Couldn't open datagram socket: " + e.getMessage());
            return;
        }

        //Clear and recreate the downloaded directory
        String downloadedDirectory = "../downloaded";
        try {
            Path downloadedDirectoryPath = Paths.get(downloadedDirectory);

            File downloadedDirectoryFile = downloadedDirectoryPath.toFile();
            if (downloadedDirectoryFile.exists()) {
                for (File file : downloadedDirectoryFile.listFiles()) {
                    file.delete();
                }
            }
            Files.deleteIfExists(downloadedDirectoryPath);
            Files.createDirectory(downloadedDirectoryPath);
        } catch (IOException e) {
            System.err.println("FATAL: ClientRunnable: Couldn't delete/create downloaded directory: " + e.getMessage());
            return;
        }

        //Get the chapters now
        for (Integer chapter : neededChapters) {
            FileOutputStream output;

            //Setup output file
            try {
                File outputFile = Files.createFile(Paths.get(downloadedDirectory + '/' + chapter)).toFile();
                output = new FileOutputStream(outputFile);
            } catch (IOException e) {
                System.err.println("ERROR: ClientRunnable: Error creating the output file for chapter " + chapter + ": " + e.getMessage());
                anyFailure = true;
                continue;
            }

            long ipQueryTimeStart = System.nanoTime();
            //Send request to superpeer for the other ClientServer's ip corresponding to the chapter we need
            byte[] buffer = (config.getMyIp() + " " + chapter).getBytes();
            DatagramPacket packet;
            try {
                packet = new DatagramPacket(buffer, buffer.length, config.getSuperpeerAddress(), CLIENT_AND_SUPERNODE_PORT);
                sock.send(packet);
            } catch (IOException e) {
                System.err.println("ERROR: ClientRunnable: Couldn't send request to superpeer: " + e.getMessage());
                anyFailure = true;
                continue;
            }

            //Get response from superpeer
            try {
                packet = new DatagramPacket(new byte[1024], 1024);
                sock.receive(packet);
            } catch (IOException e) {
                System.err.println("ERROR: ClientRunnable: Couldn't receive the response from superpeer: " + e.getMessage());
                anyFailure = true;
                continue;
            }
            long ipQueryTimeFinish = System.nanoTime();
            clientSuperpeerQueryStats.addSuperpeerIpQueryTime(ipQueryTimeFinish - ipQueryTimeStart);

            //Get the address of the server from the packet
            String packetContents = new String(packet.getData()).trim();
            InetAddress serverAddress;
            try {
                serverAddress = InetAddress.getByName(packetContents);
            } catch (UnknownHostException e) {
                System.err.println("ERROR: ClientRunnable: Couldn't get ip address from" +
                        " the data that was gotten from the superpeer: " + packetContents);
                anyFailure = true;
                continue;
            }

            long totalChapterTimeStart = System.nanoTime();
            //Make a connection to the server
            Socket serverSocket;
            try {
                serverSocket = new Socket(serverAddress, CLIENT_SERVER_PORT);
            } catch (IOException e) {
                System.err.println("ERROR: ClientRunnable: Couldn't connect to other server: " + e.getMessage());
                anyFailure = true;
                continue;
            }

            //Make an output print writer and a buffered reader for communicating with the other server
            PrintWriter out;
            BufferedReader in;
            try {
                out = new PrintWriter(serverSocket.getOutputStream());
                in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
            } catch (IOException e) {
                System.err.println("ERROR: ClientRunnable: Couldn't create print " +
                        "writer or buffered reader for server: " + e.getMessage());
                anyFailure = true;
                continue;
            }

            //Make the request to the other server for the chapter
            out.println(String.valueOf(chapter));
            out.flush();

            long totalSize = 0;

            //Read the response from the server and write it to a file
            String input;
            try {
                while (true) {

                    long chapterPacketStart = System.nanoTime();
                    input = in.readLine();
                    long chapterPacketFinish = System.nanoTime();

                    if (input == null) {
                        break;
                    }

                    clientChapterPacketStats.addChapterPacketTransferTime(chapterPacketFinish - chapterPacketStart);
                    byte[] bytes = input.getBytes();
                    output.write(bytes);

                    totalSize += bytes.length;
                    clientChapterPacketStats.addChapterPacketSize(bytes.length);
                }
                output.flush();
            } catch (IOException e) {
                System.err.println("ERROR: ClientRunnable: Couldn't read from server or write to file: " + e.getMessage());
                anyFailure = true;
                continue;
            }
            long totalChapterTimeFinish = System.nanoTime();
            clientChapterGetStats.addTotalChapterGetTime(totalChapterTimeFinish - totalChapterTimeStart);
            clientChapterGetStats.addTotalChapterSize(totalSize);

            System.out.println("INFO: ClientServer successfully downloaded file " + chapter);

        }
        long totalTime = System.nanoTime() - totalTimeStart;
        if (anyFailure) {
            System.out.println("INFO: ClientRunnable: Not saving time spent since there was an error. Total time was " + totalTime + "ns");
        } else {
            clientAllChapterRequestsStats.addTotalTimeForAllChapters(totalTime, neededChapters.size());
        }

    }

    public List<CsvStat> getClientCsvStats() {
        return clientCsvStats;
    }
}
