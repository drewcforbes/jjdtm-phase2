package config;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class SuperpeerConfig {

    private Map<Integer, InetAddress> clientChapterLookupTable;
    private List<String> otherSuperpeerIps;

    public SuperpeerConfig() {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("application.properties"));
        } catch (IOException e) {
            System.err.println("Couldn't load ClientServer properties: " + e.getMessage());
            return;
        }

        //Load the client's chapter lookup table
        String[] clientIps = properties.getProperty("clientIps").split(",");
        String[] clientChapters = properties.getProperty("clientChapters").split(",");

        //Check to make sure that all the client ips have chapters as well
        if (clientIps.length != clientChapters.length) {
            System.err.println("FATAL: SuperpeerConfig: Unequal number of client ips and chapters");
            return;
        }

        clientChapterLookupTable = new HashMap<>();
        for (int i = 0; i < clientIps.length; i++) {

            //Parse the client address
            InetAddress clientAddress;
            try {
                clientAddress = InetAddress.getByName(clientIps[i]);
            } catch (UnknownHostException e) {
                System.err.println("FATAL: SuperpeerConfig: Couldn't parse client ip address: " + clientIps[i]);
                return;
            }

            //Create the chapters and assign them to the client address
            String[] bounds = clientChapters[i].split("-");
            List<Integer> chapters = RangeUtil.makeRangeList(
                    Integer.parseInt(bounds[0]),
                    Integer.parseInt(bounds[1])
            );
            for (Integer chapter : chapters) {
                clientChapterLookupTable.put(chapter, clientAddress);
            }
        }
    }

    public Map<Integer, InetAddress> getClientChapterLookupTable() {
        return clientChapterLookupTable;
    }

    public List<String> getOtherSuperpeerIps() {
        return otherSuperpeerIps;
    }
}
