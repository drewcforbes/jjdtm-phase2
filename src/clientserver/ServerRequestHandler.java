package clientserver;

import stats.ClientServerStats;

import java.net.Socket;

/**
 * Handles an incoming server request from a client.
 */
public class ServerRequestHandler implements Runnable {

    private final Socket clientRequestSocket;
    private final ClientServerStats stats;

    public ServerRequestHandler(Socket clientRequestSocket, ClientServerStats stats) {
        this.clientRequestSocket = clientRequestSocket;
        this.stats = stats;
    }

    @Override
    public void run() {
        //TODO Handle incoming request here
        // TODO: Reference the filestoserve folder created in ClientServer main()
    	
    	
      

    }
}
