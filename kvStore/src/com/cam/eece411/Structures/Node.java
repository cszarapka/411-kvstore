package com.cam.eece411.Structures;

import java.net.InetAddress;

/**
 * A representation of a node in our DHT_KVStore system
 * 
 * @author cam
 *
 */
public class Node {
	public int id;
	public InetAddress addr;
	public String name;
	public long timestamp;
	public int missedACKs;
	
	public int nextID;
	public int prevID;

	/**
	 * Constructs a new node with the specified fields
	 * 
	 * @param nodeID
	 *            the node number of the node
	 * @param addr
	 *            the IP address of this node
	 */
	public Node(int nodeID, InetAddress addr) {

		this.id = nodeID;
		this.addr = addr;
		this.name = addr.getHostAddress();
		this.missedACKs = 0;
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
	
	/**
	 * Returns true if the specified node is a neighbor of this node.
	 * Neighbor means the node is immediately CCW or CW of this node
	 * @param node	the node to consider
	 * @return		true if it is a neighbor, false otherwise
	 */
	public boolean isNeighbor(Node node) {
		int nID = node.id;
		if (nID == nextID || nID == prevID) {
			return true;
		}
		return false;
	}
}
