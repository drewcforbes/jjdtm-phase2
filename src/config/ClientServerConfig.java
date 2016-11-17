package config;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Properties;

public class ClientServerConfig {

    private List<Integer> chaptersNeeded;
    private InetAddress superpeerAddress;
    private String directoryChar;

    public ClientServerConfig() {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("application.properties"));
        } catch (IOException e) {
            System.err.println("Couldn't load ClientServer properties: " + e.getMessage());
            return;
        }

        //Parse the chapters needed range
        String chapterRange = properties.getProperty("chaptersNeeded");
        String[] bounds = chapterRange.split("-");
        chaptersNeeded = RangeUtil.makeRangeList(
                Integer.parseInt(bounds[0]),
                Integer.parseInt(bounds[1])
        );

        //Parse the superpeer ip address
        String superpeerIp = properties.getProperty("superpeerIp");
        try {
            superpeerAddress = InetAddress.getByName(superpeerIp);
        } catch (UnknownHostException e) {
            System.err.println("ClientServerConfig: Couldn't parse superpeer ip address: " + superpeerIp);
        }

        //Get the directory character
        properties.getProperty("directoryCharacter");
    }

    public List<Integer> getChaptersNeeded() {
        return chaptersNeeded;
    }

    public InetAddress getSuperpeerAddress() {
        return superpeerAddress;
    }

    public String getDirectoryChar() {
        return directoryChar;
    }
}
