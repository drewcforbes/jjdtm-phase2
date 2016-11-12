package superpeer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;

public class SuperpeerToSuperpeerRequestHandler implements Runnable {

	private final DatagramPacket incomingPacket;
	// <chapter#,ipAddress>
	private final Map<String, String> localRoutingTable;
	private final PendingRequestHolder pendingRequestHolder;

	public SuperpeerToSuperpeerRequestHandler(DatagramPacket incomingPacket, Map<String, String> localRoutingTable,
			PendingRequestHolder pendingRequestHolder) {

		this.incomingPacket = incomingPacket;
		this.localRoutingTable = localRoutingTable;
		this.pendingRequestHolder = pendingRequestHolder;
	}

	@Override
	public void run() {
		// TODO Handle incoming superpeer to superpeer requests
		//test test
		String[] content = new String(incomingPacket.getData()).split(" ");

		// if DatagramPacket only contains chapter number
		if (content.length == 1) {
			if (localRoutingTable.containsKey(content[0])) {
				try {
					String message = content[0] + " " + localRoutingTable.get(content[0]);
					byte[] bufferArr = message.getBytes();
					DatagramSocket sock = new DatagramSocket();
					DatagramPacket pack = new DatagramPacket(bufferArr, bufferArr.length, incomingPacket.getAddress(),
							5556);
					sock.send(pack);
					sock.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			// if DatagramPacket contains chapter number and IpAddress of client
			// with chapter
		} else if (content.length == 2) {
			try {
				List<String> clients = pendingRequestHolder.getPendingRequestsForChapter(content[0]);
				byte[] bufferArr = content[1].getBytes();
				DatagramSocket sock = new DatagramSocket();
				for (String client : clients) {
					// create the message and send it to the client
					InetAddress clientAdr = InetAddress.getByName(client);
					DatagramPacket pack = new DatagramPacket(bufferArr, bufferArr.length, clientAdr, 5555);
					sock.send(pack);
					sock.close();

				}
			} catch (IOException e) {
				System.err.println(e);
			}
		}
	}
}
