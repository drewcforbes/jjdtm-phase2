package clientserver;

import config.ClientServerConfig;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    public ClientRunnable(ClientServerConfig config) {
        this.config = config;
    }

    @Override
    public void run() {

        List<Integer> neededChapters = config.getChaptersNeeded();

        //Print the list of files to download to the console
        StringBuilder stringBuilder = new StringBuilder();
        for(Integer entry: neededChapters) {
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
        String downloadedDirectory = "downloaded";
        try {
            Path downloadedDirectoryPath = Paths.get(downloadedDirectory);

            Files.deleteIfExists(downloadedDirectoryPath);
            Files.createDirectory(downloadedDirectoryPath);
        } catch (IOException e) {
            System.err.println("FATAL: ClientRunnable: Couldn't delete/create downloaded directory: " + e.getMessage());
            return;
        }

        //Get the chapters now
        for (int i = 0; i < neededChapters.size(); i++) {
			int chapter = neededChapters.get(i);
            FileOutputStream output;

            try {
                File outputFile = Files.createFile(Paths.get(downloadedDirectory + '/' + chapter)).toFile();
                output = new FileOutputStream(outputFile);
            } catch (IOException e) {
                System.err.println("ERROR: ClientRunnable: Error creating the output file for chapter " + chapter + ": " + e.getMessage());
                continue;
            }

            //Send request to superpeer for the other ClientServer's ip corresponding to the chapter we need
            byte[] buffer = new byte[1024];
            DatagramPacket packet;
			try {
                packet = new DatagramPacket(buffer, buffer.length, config.getSuperpeerAddress(), CLIENT_AND_SUPERNODE_PORT);
                sock.send(packet);
	        } catch (IOException e) {
	            System.err.println("ERROR: ClientRunnable: Couldn't send request to superpeer: " + e.getMessage());
                continue;
	        }

	        //Get response from superpeer
            try {
                sock.receive(packet);
            } catch (IOException e) {
                System.err.println("ERROR: ClientRunnable: Couldn't receive the response from superpeer: " + e.getMessage());
                continue;
            }

            //Get the address of the server from the packet
            String packetContents = new String(packet.getData());
            InetAddress serverAddress;
            try {
                serverAddress = InetAddress.getByName(packetContents);
            } catch (UnknownHostException e) {
                System.err.println("ERROR: ClientRunnable: Couldn't get ip address from" +
                        " the data that was gotten from the superpeer: " + packetContents);
                continue;
            }

            //Make a connection to the server
            Socket serverSocket;
            try {
                serverSocket = new Socket(serverAddress, CLIENT_SERVER_PORT);
            } catch (IOException e) {
                System.err.println("ERROR: ClientRunnable: Couldn't connect to other server: " + e.getMessage());
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
                continue;
            }

            //Make the request to the other server for the chapter
            out.append(String.valueOf(chapter));
            out.flush();

            //Read the response from the server and write it to a file
            String input;
            try {
                while ((input = in.readLine()) != null) {
                    output.write(input.getBytes());
                }
                output.flush();
            } catch (IOException e) {
                System.err.println("ERROR: ClientRunnable: Couldn't read from server or write to file: " + e.getMessage());
                continue;
            }

            System.out.println("INFO: ClientServer successfully got chapter " + chapter);
        }

    }

}
