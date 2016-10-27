package superpeer;

import java.net.DatagramPacket;

public class SuperpeerToSuperpeerRequestHandler implements Runnable {

    private final DatagramPacket incomingPacket;

    public SuperpeerToSuperpeerRequestHandler(DatagramPacket incomingPacket) {
        this.incomingPacket = incomingPacket;
    }

    @Override
    public void run() {

    }
}
