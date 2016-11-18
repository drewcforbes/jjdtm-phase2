package clientserver;

import config.ClientServerConfig;
import stats.clientserver.ServerPacketStats;
import stats.clientserver.ServerRequestStats;

import java.io.*;
import java.net.Socket;

/**
 * Handles an incoming server request from a client.
 */
public class ServerRequestHandler implements Runnable {

    private final Socket clientRequestSocket;
    private final ClientServerConfig config;
    private final ServerPacketStats serverPacketStats;
    private final ServerRequestStats serverRequestStats;

    public ServerRequestHandler(
            Socket clientRequestSocket,
            ClientServerConfig config,
            ServerPacketStats serverPacketStats,
            ServerRequestStats serverRequestStats
    ) {
        this.clientRequestSocket = clientRequestSocket;
        this.config = config;
        this.serverPacketStats = serverPacketStats;
        this.serverRequestStats = serverRequestStats;
    }

    @Override
    public void run() {
        long totalTimeStart = System.nanoTime();

        OutputStream out;
        BufferedReader in;// reader (for reading from the machine connected to)
    	try {
            //(A) Receive a TCP connection
    	   out = clientRequestSocket.getOutputStream();
   		   in = new BufferedReader(new InputStreamReader(clientRequestSocket.getInputStream()));
   		} catch (IOException e) {
            System.err.println("FATAL: ServerRequestHandler: Client failed to connect: " + e.getMessage());
            return;
        }

    	// Initial sends/receives
    	try {

            //(B) read the chapter number from that connection,
            String chapterRequested = in.readLine();
            System.out.println("Getting Chapter: " + chapterRequested);
            System.out.println("Connected to the client seeking chapter" + chapterRequested); // confirmation of connection

            //read the chapter from the file system,
            File file = new File(String.format(config.getFileSubstitutionFormat(), Integer.parseInt(chapterRequested)));
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);

            //Read File Contents into contents array
            byte[] contents;
            long fileLength = file.length();
            long current = 0;

            long totalRequestTime = 0;
            long totalReadTime = 0;

            long start = System.nanoTime();
            while (current != fileLength) {
                int size = 10000;
                if(fileLength - current >= size) {
                    current += size;
                } else {
                    size = (int) (fileLength - current);
                    current = fileLength;
                }
                contents = new byte[size];

                long readStart = System.nanoTime();
                bis.read(contents, 0, size);
                long readTime = System.nanoTime() - readStart;
                totalReadTime += readTime;
                serverPacketStats.addPacketReadTime(readTime);

                long writeStart = System.nanoTime();
                out.write(contents);
                long writeTime = System.nanoTime() - writeStart;
                totalRequestTime += writeTime;
                serverPacketStats.addPacketSendTime(writeTime);

                System.out.print("Sending file ... " + (current*100) / fileLength + "% complete!");
                out.flush();
            }

            serverRequestStats.addTotalRequestTimes(totalRequestTime);
            serverRequestStats.addFileReadTimes(totalReadTime);

            //File transfer done. Close the socket connection!
            bis.close();
            clientRequestSocket.close();   //JP Watch this for possible bug
            System.out.println("File sent succesfully!");

        } catch (IOException e) {
            System.err.println("Could not listen to socket.");
        }

        long totalTimeFinish = System.nanoTime();
        serverRequestStats.addTotalRequestTimes(totalTimeFinish - totalTimeStart);
    }
}

