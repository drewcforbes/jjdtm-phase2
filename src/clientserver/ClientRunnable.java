package clientserver;

/**
 * Runnable to make requests to the super peer and
 * other clients
 */
public class ClientRunnable implements Runnable {

    private final static int CLIENT_AND_SUPERNODE_PORT = 5555;

    private final String supernodeIp;

    public ClientRunnable(String supernodeIp) {
        this.supernodeIp = supernodeIp;
    }

    @Override
    public void run() {
        //TODO client things here
    }
}
