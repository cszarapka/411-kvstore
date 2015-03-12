package com.cam.eece411;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * The main process that will be running an instance of our DHT-KVStore
 * @author cam
 *
 */
public class Server {
	// Step 1.
	// Create a DHT where we are the first (and only) node
	public static DHT myDHT = new DHT();
	
	// Step 2.
	// Create an empty Key-Value store
	public static KVStore myStore = new KVStore();

	public static void main(String[] args) {
		System.out.println("It has begun.");
		
		// Step 3.
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
			System.out.println("It waits for your command now..");
			try {
				socket.receive(packet);
				System.out.println("It received a packet.");
				if (packet.getData()[0] <= Protocols.APP_CMD_SHUTDOWN) {
					// Step 5.
					// Respond to commands
					respondToAppRequest(new AppMessage(packet));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Responds to an app-layer request-message (GET, PUT, REMOVE, SHUTDOWN) by building
	 * and sending a response message
	 * @param msg	the message object (constructed from the packet data) to respond to
	 */
	public static void respondToAppRequest(AppMessage msg) {
		switch(msg.getCommand()) {
		case Protocols.APP_CMD_GET:
			byte[] value = myStore.get(msg.getKey());
			if (value != null) {
				sendMessage(AppMessage.buildResponse(Protocols.CODE_SUCCESS, value.length, value), msg.getIP());
			} else {
				sendMessage(AppMessage.buildResponse(Protocols.CODE_KEY_DNE), msg.getIP());
			}
			break;
		case Protocols.APP_CMD_PUT:
			sendMessage(AppMessage.buildResponse(myStore.put(msg.getKey(), msg.getValue())), msg.getIP());
			break;
		case Protocols.APP_CMD_REMOVE:
			sendMessage(AppMessage.buildResponse(myStore.remove(msg.getKey())), msg.getIP());
			break;
		case Protocols.APP_CMD_SHUTDOWN:
			sendMessage(AppMessage.buildResponse(Protocols.CODE_SUCCESS), msg.getIP());
			System.out.println("It shuts down now.");
			System.exit(0);
			break;
		}
	}
	
	/**
	 * Sends a packet to the specified IP
	 * @param data	the data to put in the sending packet
	 * @param ip	the IP to send the packet to
	 */
	public static void sendMessage(byte[] data, InetAddress ip) {
		DatagramSocket socket;
		DatagramPacket packet;
		try {
			socket = new DatagramSocket(Protocols.LISTENING_PORT);
			packet = new DatagramPacket(data, data.length, ip, Protocols.SENDING_PORT);
			socket.send(packet);
			socket.close();
		} catch (Exception e) {
			System.out.println("It failed to create a sending socket, so it gave up.");
			return;
		}
	}
}
