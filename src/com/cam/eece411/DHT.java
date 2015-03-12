package com.cam.eece411;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * A representation of our network of nodes in our DHT
 * @author cam
 *
 */
public class DHT {
	private SortedMap<Integer, Node> circle;
	
	/**
	 * Constructs an empty DHT, and then adds itself (this node) to it.
	 * This assumes that we are the first node in the DHT.
	 */
	public DHT() {
		circle = Collections.synchronizedSortedMap(new  TreeMap<Integer, Node>());
		try {
			add(Protocols.MAX_NODE_NUMBER, new Node(Protocols.MAX_NODE_NUMBER, 0, InetAddress.getLocalHost()));
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
	public DHT(byte[] nodeIDs, byte[] nextNodeIDs, byte[] IPs) {
		circle = Collections.synchronizedSortedMap(new  TreeMap<Integer, Node>());
		for (int i = 0; i < nodeIDs.length; i++) {
			try {
				add(nodeIDs[i], new Node(nodeIDs[i], nextNodeIDs[i], InetAddress.getByAddress(Arrays.copyOfRange(IPs, (i*4), (i+1)*4))));
			} catch (UnknownHostException e) {
				System.out.println("Node: " + nodeIDs[i] + " could not be added.");
			}
		}
	}
	
	/**
	 * Adds the node to our DHT
	 * @param nodeID	the ID of the node to add
	 * @param node		the node (Node object) to add
	 */
	public void add(int nodeID, Node node) {
		circle.put(nodeID, node);
	}
	
	/**
	 * Removes the node from our DHT
	 * @param nodeID	the ID of the node to remove
	 */
	public void remove(int nodeID) {
		circle.remove(nodeID);
	}
}
