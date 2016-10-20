package clientserver;

import java.net.Socket;

/**
 * Handles an incoming server request from a client.
 */
public class ServerRequestHandler implements Runnable {

    private final Socket clientRequestSocket;

    public ServerRequestHandler(Socket clientRequestSocket) {
        this.clientRequestSocket = clientRequestSocket;
    }

    @Override
    public void run() {
        //TODO Handle incoming request here
    }
}
