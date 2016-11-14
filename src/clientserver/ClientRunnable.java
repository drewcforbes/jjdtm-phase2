package clientserver;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Runnable to make requests to the super peer and
 * other clients
 */
public class ClientRunnable implements Runnable {

    private final static int CLIENT_AND_SUPERNODE_PORT = 5555;

    private static final Logger LOGGER = Logger.getLogger(ClientRunnable.class.getName());

    private final String supernodeIp;

    private String nodeId;

    private static Properties properties = new Properties();

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

        List<Integer> chaptersToDownload = new ArrayList<Integer>();

        // Make a list of files to download
        if (nodeId.equalsIgnoreCase("A1")) {
            for (int i = 1; i < 26; i++) {
                chaptersToDownload.add(new Integer(i));
            }
        } else if (nodeId.equalsIgnoreCase("A2")) {
            for (int i = 26; i < 51; i++) {
                chaptersToDownload.add(new Integer(i));
            }
        } else if (nodeId.equalsIgnoreCase("B1")) {
            for (int i = 51; i < 76; i++) {
                chaptersToDownload.add(new Integer(i));
            }
        } else if (nodeId.equalsIgnoreCase("B2")) {
            for (int i = 76; i < 101; i++) {
                chaptersToDownload.add(new Integer(i));
            }
        } else {
            LOGGER.severe("Error in application.properties. mymachine must be A1, A2, B1, or B2. mymachine was wrongly " +
                    "set to " + nodeId);
        }

        // TODO: Download the list of files

        /*
        Start loop
         */
        // Send a UDP request to your Superpeer for the IP address of the ClientServer that has the desired chapter

        // Make a TCP connection to that IP address and download the file.
        /*
        End loop
         */
    }
}
