package config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class SuperpeerConfig {

    private Map<Integer, String> clientChapterLookupTable;
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

            String clientAddress = clientIps[i];

            //Create the chapters and assign them to the client address
            String[] bounds = clientChapters[i].split("-");
            List<Integer> chapters = RangeUtil.makeRangeList(
                    Integer.parseInt(bounds[0]),
                    Integer.parseInt(bounds[1])
            );

            //Put the chapters in the lookuptable with the client's address
            for (Integer chapter : chapters) {
                clientChapterLookupTable.put(chapter, clientAddress);
            }
        }

        //Load the other superpeer ids
        String unparsedSuperpeerIps = properties.getProperty("otherSuperpeerIps");
        otherSuperpeerIps = Arrays.asList(unparsedSuperpeerIps.split(","));
    }

    public Map<Integer, String> getClientChapterLookupTable() {
        return clientChapterLookupTable;
    }

    public List<String> getOtherSuperpeerIps() {
        return otherSuperpeerIps;
    }
}
