package clientserver;

import config.ClientServerConfig;
import stats.clientserver.ServerPacketStats;
import stats.clientserver.ServerRequestStats;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Listens for requests coming to the server.
 * Spawns a new thread with a {@link ServerRequestHandler}
 * when a new request is received
 */
public class ServerListener implements Runnable {

    private static final int SERVER_LISTENER_PORT = 5554;

    private ClientServerConfig config;
    private final ServerPacketStats serverPacketStats;
    private final ServerRequestStats serverRequestStats;

    public ServerListener(
            ClientServerConfig config,
            ServerPacketStats serverPacketStats,
            ServerRequestStats serverRequestStats
    ) {
        this.config = config;
        this.serverPacketStats = serverPacketStats;
        this.serverRequestStats = serverRequestStats;
    }

    @Override
    public void run() {
        System.out.println("Starting server listening on port " + SERVER_LISTENER_PORT);

        //Start listening on the listening port
        ServerSocket listeningSocket;
        try {
            listeningSocket = new ServerSocket(SERVER_LISTENER_PORT);
        } catch (IOException e) {
            System.err.println("FATAL: Error opening listening server socket on port " + SERVER_LISTENER_PORT);
            throw new RuntimeException(e);
        }

        Socket clientRequestSocket = null;

        //Listen indefinitely for requests
        while (true) {

            //Accept incoming requests
            try {
                clientRequestSocket = listeningSocket.accept();
            } catch (IOException e) {
                System.err.println("Error: Error while listening for client requests");
                System.err.println("Error: " + e.getMessage());
            }

            //Start a handler thread for the request
            new Thread(new ServerRequestHandler(
                    clientRequestSocket,
                    config,
                    serverPacketStats,
                    serverRequestStats
            )).start();
        }
    }
}
