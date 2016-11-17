package clientserver;

import java.io.FileInputStream;
import java.io.IOException;
//<<<<<<< HEAD
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
//=======
import java.util.*;
//>>>>>>> origin/master
import java.util.logging.Logger;

/**
 * Runnable to make requests to the super peer and
 * other clients
 */
public class ClientRunnable implements Runnable {

    private final static int CLIENT_AND_SUPERNODE_PORT = 5555;

    private static final Logger LOGGER = Logger.getLogger(ClientRunnable.class.getName());

    private final String supernodeIp;
//<<<<<<< HEAD
    private InetAddress SuperAddress;
    private String nodeId;
//=======
//>>>>>>> origin/master

    private static Properties properties = new Properties();

    //private String nodeId;

    public ClientRunnable(String supernodeIp) {
        this.supernodeIp = supernodeIp;
    }

    @Override
    public void run() {
        /*
        Read properties to use as configs.
         */
        try {
            properties.load(new FileInputStream("src/application.properties"));
            
        } catch (IOException e) {
            LOGGER.warning("Failed to load properties file.");
            e.printStackTrace();
        }
        // Read the identity of this node
        nodeId = properties.getProperty("mymachine");

        // Make a list of files to download
        Set<Integer> chaptersToDownload = makeChaptersToDownload();

        //Print the list of files to download to the console
        StringBuilder stringBuilder = new StringBuilder();
        for(Integer entry: chaptersToDownload) {
            stringBuilder.append(entry + " ");
        }
        LOGGER.info("ClientServer " + nodeId + " will download chapters " + stringBuilder.toString());


        // TODO: Download the list of files

        /*
        Start loop
         */
        
        
        
        // Send a UDP request to your Superpeer for the IP address of the ClientServer that has the desired chapter

        //chaptersToDownload
         Integer Chapter;
         
         try {
         SuperAddress = InetAddress.getByName(supernodeIp);}
         catch (IOException e) {
	            System.err.println(e);
	        }
         
         
         //TODO Research and implement a way to throttle this for loop. Possible problem
         //is by sending the request out without some locking mechanism like this:
         //(1) Need chapter x 
         //(2) Send to super....back from super is ip having chapter
         //(3) TCP connect to peer
         //(4) Process file 
         //(5) Continue for loop to process next chapter 
         
 		// iterate via "iterator loop"
 		
 		Iterator<Integer> ChapterIterator = chaptersToDownload.iterator();
 		       
         
        for (int i = 0; i < chaptersToDownload.size(); i++) {
        	Chapter = ((Iterator<Integer>) chaptersToDownload).next();
			try {
	            //send request to local cluster super peer'
	        	byte[] buffer = new byte[Chapter];        	
	            DatagramSocket sock = new DatagramSocket();
	            DatagramPacket pack = new DatagramPacket(buffer, buffer.length,SuperAddress , CLIENT_AND_SUPERNODE_PORT);
	            
	            sock.send(pack);
	            sock.close();
	        }
	        catch (IOException e) {
	            System.err.println(e);
	        }
		}
        
        
        
         
        
        
        // Make a TCP connection to that IP address and download the file.
        /*
        End loop
         */
    }

    /**
     * Returns a Set of Integers representing all chapters that a ClientServer needs to download.
     * @return
     */
    private Set<Integer> makeChaptersToDownload() {

        // Make a list of all chapters needed to be finished, including the ones this ClientServer starts with
        Set<Integer> allChaptersNeeded = makeAllChaptersNeeded();

        Set<Integer> chaptersNotNeeded = makeChaptersNotNeeded();

        /* Subtract the chapters the ClientServer starts with from the set of all chapters to get the list of chapters
         to download
          */
        allChaptersNeeded.removeAll(chaptersNotNeeded);
        return allChaptersNeeded;
    }

    /**
     * Returns a Set of Integers representing the chapters that this ClientServer does not need to download.
     * @return
     */
    private Set<Integer> makeChaptersNotNeeded() {

        /* Set a clientServerIndex according to the identity of the ClientServer. clientServerIndex is used to set the
        correct chapters that this ClientServer does not need to download.
         */
        int clientServerIndex = 0;
        if (nodeId.equalsIgnoreCase("A1")) {
            clientServerIndex = 0;
        } else if (nodeId.equalsIgnoreCase("A2")) {
            clientServerIndex = 1;
        } else if (nodeId.equalsIgnoreCase("B1")) {
            clientServerIndex = 2;
        } else if (nodeId.equalsIgnoreCase("B2")) {
            clientServerIndex = 3;
        } else {
            LOGGER.severe("Error in application.properties. mymachine must be A1, A2, B1, or B2. mymachine was wrongly " +
                    "set to " + nodeId);
            System.exit(0);
        }

        /* Set the starting and ending chapters in the set of chapters this ClientServer does not need to download.
         */
        int chaptersPerClientServer = Integer.parseInt(properties.getProperty("chaptersperclientserver"));
        int startingChapter = 1 + clientServerIndex * chaptersPerClientServer;
        int endingChapter = startingChapter + chaptersPerClientServer - 1;

        /* Build the set of chapters not needed by this ClientServer
         */
        Set<Integer> chaptersNotNeeded = new HashSet<>();
        for (int i = startingChapter; i <= endingChapter; i++) {
            chaptersNotNeeded.add(new Integer(i));
        }
        return chaptersNotNeeded;
    }

    /**
     * Makes a Set of Integers representing all chapters each ClientServer will need. EG with 25 chapters per
     * ClientServer, returns a Set of Integers 1 to 100. Or, with 10 chapters per ClientServer, returns a Set of
     * Integers 1 to 40.
     * @return
     */
    private Set<Integer> makeAllChaptersNeeded() {
        Set<Integer> allChaptersNeeded = new HashSet<>();
        int chaptersPerClientServer = Integer.parseInt(properties.getProperty("chaptersperclientserver"));
        // Always assume the number of ClientServers is 4.
        int totalNumberOfChapters = 4 * chaptersPerClientServer;
        for (int i = 1; i <= totalNumberOfChapters; i++) {
            allChaptersNeeded.add(new Integer(i));
        }
        return allChaptersNeeded;
    }
}
