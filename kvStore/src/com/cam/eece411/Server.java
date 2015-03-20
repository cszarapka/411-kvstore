package com.cam.eece411;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * The main process that will be running an instance of our DHT-KVStore
 * @author cam
 *
 */
public class Server {

	public static void main(String[] args) {
		System.out.println("It has begun.");
		
		// Step 1.
		// Create a DHT where we are the first (and only) node
		ConsistenHash myHash = new ConsistenHash();
		
		// Step 2.
		// Set up the socket and packet
		byte[] receivedPacket = new byte[Protocols.MAX_MSG_SIZE];
		DatagramSocket socket;
		DatagramPacket packet;
		try {
			socket = new DatagramSocket(Protocols.LISTENING_PORT);
			packet = new DatagramPacket(receivedPacket, receivedPacket.length);
		} catch (SocketException e) {
			System.out.println("It failed to create a listening socket, so it gave up.");
			return;
		}
		
		// Step 4.
		// Listen for commands (GET, PUT, REMOVE, SHUTDOWN)
		while(true) {
			System.out.println("It waits for requests..");
			try {
				socket.receive(packet);
				myHash.reactTo(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
