package com.cam.eece411;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.cam.eece411.Messages.AppResponse;
import com.cam.eece411.Utilities.Protocols;

/**
 * The main process that will be running an instance of our DHT-KVStore
 * @author cam
 *
 */
public class Server {

	public static Node me;
	public static Integer state = Protocols.OUT_OF_TABLE;

	public static void main(String[] args) throws SocketException, IOException {
		System.out.println("\n\n\n\n\n\n\n");
		System.out.println("It has begun.");

		// Instantiate ourself as a node
		try {
			me = new Node(Protocols.MAX_NODE_NUMBER, 0, InetAddress.getLocalHost());
		} catch (UnknownHostException e) {
			System.out.println("Failed to get local IP");
			e.printStackTrace();
		}

		// Set up our socket and packet objects
		byte[] receivedPacket = new byte[Protocols.MAX_MSG_SIZE];
		DatagramSocket socket = new DatagramSocket(Protocols.LISTENING_PORT);
		DatagramPacket packet = new DatagramPacket(receivedPacket, receivedPacket.length);

		// Listen for commands (GET, PUT, REMOVE, SHUTDOWN)
		while(true) {
			System.out.println("Waiting for a message...");
			socket.receive(packet);
			System.out.println("Messaged received!");
			// Launch thread based on state we are in
			switch (state) {
				case Protocols.OUT_OF_TABLE: (new Thread(new ActivationHandler(packet))).start(); break;
				case Protocols.IN_TABLE: (new Thread(new ResponseHandler(packet))).start(); break;
				case Protocols.LEFT_TABLE:
					// TODO: leave system gracefully
					socket.close();
			}
		}
	}

	/**
	 * Sends a message to the specified IP
	 * @param data	the data to put in the sending packet
	 * @param ip	the IP to send the packet to
	 * @param port	the port to send the packet to
	 */
	public static void sendMessage(byte[] data, InetAddress ip, int port) {
		try {
			DatagramSocket socket = new DatagramSocket(Protocols.SENDING_PORT);
			DatagramPacket packet = new DatagramPacket(data, data.length, ip, port);
			socket.send(packet);
			System.out.println("It sended a packet.........");
			socket.close();
		} catch (Exception e) {
			System.out.println("It failed to create a sending socket, so it gave up.");
			e.printStackTrace();
			return;
		}
	}

	public static void sendMessage(AppResponse msg) {
		DatagramSocket socket;
		DatagramPacket packet;
		try {
			socket = new DatagramSocket(Protocols.SENDING_PORT);
			packet = new DatagramPacket(msg.buffer, msg.buffer.length, msg.ipToSendTo, msg.portToSendTo);
			socket.send(packet);
			System.out.println("- - It responded with:");
			System.out.print(msg.toString());
			System.out.println("- - - - - - - - - - - - - - - - - -");
			socket.close();
		} catch (Exception e) {
			System.out.println("It failed to create a sending socket, so it gave up.");
			e.printStackTrace();
			return;
		}
	}
}
