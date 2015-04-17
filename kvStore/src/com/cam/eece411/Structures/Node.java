package com.cam.eece411.Structures;

import java.net.InetAddress;

/**
 * A representation of a node in our DHT_KVStore system
 * @author cam
 *
 */
public class Node {
	public int nodeID;
	public InetAddress addr;
	public String name;
	
	/**
	 * Constructs a new node with the specified fields
	 * @param nodeID		the node number of the node
	 * @param addr				the IP address of this node
	 */
	public Node(int nodeID, InetAddress addr) {

		this.nodeID = nodeID;
		this.addr = addr;
		this.name = addr.getHostName();
	}
}
