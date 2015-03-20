package com.cam.eece411;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import com.cam.eece411.Messages.AppResponse;
import com.cam.eece411.Messages.ReceivedMessage;

/**
 * A representation of our network of nodes in our Key/Value store
 * @author cam
 *
 */
public class ConsistenHash {
	private SortedMap<Integer, Node> circle;
	private ConcurrentHashMap<ByteBuffer, byte[]> KVStore;

	/**
	 * Constructs an empty DHT with an empty Key/Value store, and
	 * then adds itself (this node) to the DHT.
	 * This assumes that we are the first node in the DHT.
	 */
	public ConsistenHash() {
		circle = Collections.synchronizedSortedMap(new  TreeMap<Integer, Node>());
		KVStore = new ConcurrentHashMap<ByteBuffer, byte[]>();
		try {
			addNode(Protocols.MAX_NODE_NUMBER, new Node(Protocols.MAX_NODE_NUMBER, 0, InetAddress.getLocalHost()));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Constructs a DHT with a view of the system accepted from the node bringing
	 * you into it.
	 * @param nodeIDs		the node ID's received in the join response message
	 * @param nextNodeIDs	the next-node ID's received in the join response message
	 * @param IPs			the IP's of the nodes (4 byte array)
	 */
	//	public DHT(NodeMessage joinResponse) {
	//		circle = Collections.synchronizedSortedMap(new  TreeMap<Integer, Node>());
	//		KVStore = new ConcurrentHashMap<byte[], byte[]>();
	//		for (int i = 0; i < nodeIDs.length; i++) {
	//			try {
	//				addNode(nodeIDs[i], new Node(nodeIDs[i], nextNodeIDs[i], InetAddress.getByAddress(Arrays.copyOfRange(IPs, (i*4), (i+1)*4))));
	//			} catch (UnknownHostException e) {
	//				System.out.println("Node: " + nodeIDs[i] + " could not be added.");
	//			}
	//		}
	//	}

	/*
	 * Methods for interacting with this node's view of the system
	 */

	/**
	 * Adds the node to our DHT
	 * @param nodeID	the ID of the node to add
	 * @param node		the node (Node object) to add
	 */
	public void addNode(int nodeID, Node node) {
		circle.put(nodeID, node);
	}

	/**
	 * Removes the node from our DHT
	 * @param nodeID	the ID of the node to remove
	 */
	public void removeNode(int nodeID) {
		circle.remove(nodeID);
	}

	/*
	 * Methods for interacting with the Key-Value Store
	 */

	/**
	 * Puts a value into the KVStore
	 * @param key	the key of the value
	 * @param value	the value to put
	 * @return		the resulting response code of this action, possible values:
	 * 					Protocols.CODE_SUCCESS
	 * 					Protocols.CODE_OUT_OF_SPACE
	 */
	public byte put(byte[] key, byte[] value) {
		byte responseCode = Protocols.CODE_SUCCESS;
		KVStore.put(ByteBuffer.wrap(key), Arrays.copyOf(value, value.length));
		// TODO: change the response code if the put fails due to overflow
		// TODO: test what happens when you set the max heap size, and then overflow it
		return responseCode;
	}

	/**
	 * Returns the value corresponding to key, or null if there is
	 * no value corresponding to the key
	 * @param key	the key of the value to get
	 * @return		the value belonging to the key, or null if there is none
	 */
	public byte[] get(byte[] key) {
		return KVStore.get(ByteBuffer.wrap(key));
	}

	/**
	 * Removes a value from the KVStore
	 * @param key	the key of the value to remove
	 * @return		the resulting response code of this action, possible values:
	 * 					Protocols.CODE_SUCCESS
	 * 					Protocols.CODE_KEY_DNE
	 */
	public byte remove(byte[] key) {
		byte responseCode = Protocols.CODE_SUCCESS;

		if (KVStore.remove(ByteBuffer.wrap(key)) == null) {
			responseCode = Protocols.CODE_KEY_DNE;
		}
		// TODO: add other possible codes
		return responseCode;
	}

	/*
	 * Methods for responding to and handling messages
	 */

	public void reactTo(DatagramPacket packet) {
		// Determine the message type (app request, or node message)
		if (packet.getData()[16] <= Protocols.APP_CMD_SHUTDOWN) {
			// TODO: launch the thread from here
			respondToAppRequest(new ReceivedMessage(packet));
		} else {
			// TODO: launch the thread here
			respondToNodeMessage(new NodeMessage(packet));
		}
	}

	/**
	 * Responds to an app-layer request-message (GET, PUT, REMOVE, SHUTDOWN) by building
	 * and sending a response message
	 * @param msg	the message object (constructed from the packet data) to respond to
	 */
	public void respondToAppRequest(ReceivedMessage msg) {
		// Print out the received message
		System.out.println("- - - - - - - - - - - - - - - - - -");
		System.out.println("Received message:");
		System.out.print(msg.toString());

		switch(msg.getCommand()) {
			case Protocols.APP_CMD_GET:
				byte[] value = get(msg.getKey());
				System.out.println("The command was a GET");
				System.out.println("The value: " + Helper.bytesToHexString(value));
				if (value != null) {
					sendMessage(new AppResponse(msg, value));
				} else {
					sendMessage(new AppResponse(msg, Protocols.CODE_KEY_DNE));
				}
				break;
			case Protocols.APP_CMD_PUT:
				sendMessage(new AppResponse(msg, put(msg.getKey(), msg.getValue())));
				break;
			case Protocols.APP_CMD_REMOVE:
				sendMessage(new AppResponse(msg, remove(msg.getKey())));
				break;
			case Protocols.APP_CMD_SHUTDOWN:
				sendMessage(new AppResponse(msg, Protocols.CODE_SUCCESS));
				System.out.println("It shuts down and simulates a crash now.. :(");
				System.exit(0);
				break;
		}
	}

	public void respondToNodeMessage(NodeMessage msg) {

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
