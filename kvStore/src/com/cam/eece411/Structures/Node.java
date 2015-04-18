package com.cam.eece411.Structures;

import java.net.InetAddress;

/**
 * A representation of a node in our DHT_KVStore system
 * 
 * @author cam
 *
 */
public class Node {
	public int nodeID;
	public InetAddress addr;
	public String name;
	public long timestamp;

	/**
	 * Constructs a new node with the specified fields
	 * 
	 * @param nodeID
	 *            the node number of the node
	 * @param addr
	 *            the IP address of this node
	 */
	public Node(int nodeID, InetAddress addr) {

		this.nodeID = nodeID;
		this.addr = addr;
		this.name = addr.getHostName();
		updateTimestamp();
	}

	/**
	 * Sets/updates the timestamp to the current unix time on the machine.
	 * Relative time to other machines doesn't matter because the timestamps are
	 * only used locally
	 */
	public void updateTimestamp() {
		this.timestamp = System.currentTimeMillis() / 1000L;
	}
}
