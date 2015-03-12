package com.cam.eece411;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A representation of our network of nodes in our DHT
 * @author cam
 *
 */
public class DHT {
	private SortedMap<Integer, Node> circle;
	private ConcurrentHashMap<byte[], byte[]> KVStore;
	
	/**
	 * Constructs an empty DHT with an empty Key/Value store, and
	 * then adds itself (this node) to the DHT.
	 * This assumes that we are the first node in the DHT.
	 */
	public DHT() {
		circle = Collections.synchronizedSortedMap(new  TreeMap<Integer, Node>());
		KVStore = new ConcurrentHashMap<byte[], byte[]>();
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
		KVStore.put(key, value);
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
		return KVStore.get(key);
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

		if (KVStore.remove(key) == null) {
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
			respondToAppRequest(new AppMessage(packet));
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
	public void respondToAppRequest(AppMessage msg) {
		// Determine the command, and respond accordingly
		switch(msg.getCommand()) {
		case Protocols.APP_CMD_GET:
			byte[] value = get(msg.getKey());
			
			if (value != null) {
				sendMessage(AppMessage.buildResponse(msg.getUniqueID(), Protocols.CODE_SUCCESS, value.length, value), msg.getIP());
			} else {
				sendMessage(AppMessage.buildResponse(msg.getUniqueID(), Protocols.CODE_KEY_DNE), msg.getIP());
			}
			break;
		case Protocols.APP_CMD_PUT:
			sendMessage(AppMessage.buildResponse(msg.getUniqueID(), put(msg.getKey(), msg.getValue())), msg.getIP());
			break;
		case Protocols.APP_CMD_REMOVE:
			sendMessage(AppMessage.buildResponse(msg.getUniqueID(), remove(msg.getKey())), msg.getIP());
			break;
		case Protocols.APP_CMD_SHUTDOWN:
			sendMessage(AppMessage.buildResponse(msg.getUniqueID(), Protocols.CODE_SUCCESS), msg.getIP());
			System.out.println("It shuts down now.");
			System.exit(0);
			break;
		}
	}
	
	public void respondToNodeMessage(NodeMessage msg) {
		
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
			System.out.println("Message sent!");
			socket.close();
		} catch (Exception e) {
			System.out.println("It failed to create a sending socket, so it gave up.");
			return;
		}
	}
}
