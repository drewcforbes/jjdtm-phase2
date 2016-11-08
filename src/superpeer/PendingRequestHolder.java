package superpeer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO Find a better name for this
public class PendingRequestHolder {

    private final Map<String, List<String>> pendingRequests;

    public PendingRequestHolder() {
        pendingRequests = new HashMap<>();
    }

    /**
     * Adds the given ip address to the list of
     * requests waiting for the given chapter.
     *
     * Note: This method is threadsafe
     *
     * @param chapterName The chapter the ip address is waiting on
     * @param ipAddress The ip address to add
     */
    public void addPendingRequest(String chapterName, String ipAddress) {

        synchronized (pendingRequests) {
            if (!pendingRequests.containsKey(chapterName)) {
                pendingRequests.put(chapterName, new ArrayList<>());
            }

            pendingRequests.get(chapterName).add(ipAddress);
        }
    }

    /**
     * Gets all the ip addresses of clients that are waiting for
     * the location of the given chapter.
     * Also clears the list for the given chapter.
     *
     * Note: This method is threadsafe
     *
     * @param chapterName The chapter to get the pending requests for
     * @return The list of ip addresses that have requested the chapter
     */
    public List<String> getPendingRequestsForChapter(String chapterName) {

        synchronized (pendingRequests) {
            List<String> ipAddresses = pendingRequests.get(chapterName);
            pendingRequests.put(chapterName, new ArrayList<>());
            return ipAddresses;
        }
    }
}
