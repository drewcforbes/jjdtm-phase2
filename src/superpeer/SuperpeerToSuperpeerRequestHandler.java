package superpeer;

import config.SuperpeerConfig;
import stats.superpeer.SuperpeerLookupRequestStats;
import stats.superpeer.SuperpeerRoutingTableLookupStats;
import util.Pair;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;

public class SuperpeerToSuperpeerRequestHandler implements Runnable {

	private final DatagramPacket incomingPacket;
	private final SuperpeerConfig config;
	private final PendingRequestHolder pendingRequestHolder;

	private final SuperpeerRoutingTableLookupStats routingTableLookupStats;
	private final SuperpeerLookupRequestStats lookupRequestStats;

	public SuperpeerToSuperpeerRequestHandler(
			DatagramPacket incomingPacket,
			SuperpeerConfig config,
			SuperpeerRoutingTableLookupStats routingTableLookupStats,
            SuperpeerLookupRequestStats lookupRequestStats,
            PendingRequestHolder pendingRequestHolder
	) {
		this.incomingPacket = incomingPacket;
		this.config = config;
		this.routingTableLookupStats = routingTableLookupStats;
		this.pendingRequestHolder = pendingRequestHolder;

		this.lookupRequestStats = lookupRequestStats;
	}

	@Override
	public void run() {
		String[] content = new String(incomingPacket.getData()).trim().split(" ");
		System.out.println("Got packet from Superpeer: " + new String(incomingPacket.getData()));
		int chapter = Integer.parseInt(content[0]);
		Map<Integer, String> routingTable = config.getClientChapterLookupTable();

		//See if this superpeer has the ip address the client is looking for
		long routingTableLookupStart = System.nanoTime();
		boolean hasIp = routingTable.containsKey(chapter);
		String clientIp = hasIp ? routingTable.get(chapter) : null;
		long routingTableLookupFinish = System.nanoTime();
		routingTableLookupStats.addRoutingTableLookupTime(routingTableLookupFinish - routingTableLookupStart);

		// if DatagramPacket only contains chapter number
		if (content.length == 2) {
			if (hasIp) {
				try {
					String message = chapter + " " + clientIp + " r";
					byte[] bufferArr = message.getBytes();
					DatagramSocket sock = new DatagramSocket();
                    DatagramPacket pack = new DatagramPacket(bufferArr, bufferArr.length, InetAddress.getByName(content[0]),
							5556);
					sock.send(pack);
					sock.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			// DatagramPacket contains chapter number and IpAddress of client
			// with chapter
		} else if (content.length == 3) {
			try {
				List<Pair<String, Long>> clients = pendingRequestHolder.getPendingRequestsForChapter(content[0]);
				byte[] bufferArr = content[1].getBytes();
				DatagramSocket sock = new DatagramSocket();
				for (Pair<String, Long> client : clients) {

					// create the message and send it to the client
					InetAddress clientAdr = InetAddress.getByName(client.getFirst());
					DatagramPacket pack = new DatagramPacket(bufferArr, bufferArr.length, clientAdr, 5555);
					sock.send(pack);

					//Record how long the entire transaction took
					lookupRequestStats.addSuperpeerLookupRequestTime(System.nanoTime() - client.getSecond());
				}
				sock.close();
			} catch (IOException e) {
				System.err.println("ERROR: SuperpeerToSuperpeerRequestHandler: While sending IP back to client: " + e.getMessage());
			}
		} else {
			throw new RuntimeException("Malformed request: " + new String(incomingPacket.getData()).trim());
		}
	}
}
