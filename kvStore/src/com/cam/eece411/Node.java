package com.cam.eece411;

import java.net.InetAddress;

/**
 * A representation of a node in our DHT_KVStore system
 * @author cam
 *
 */
public class Node {
	public int nodeNumber;
	public InetAddress ip;
	public String name;
	
	/**
	 * Constructs a new node with the specified fields
	 * @param nodeNumber		the node number of the node
	 * @param ip				the IP address of this node
	 */
	public Node(int nodeNumber, InetAddress ip) {

		this.nodeNumber = nodeNumber;
		this.ip = ip;
		this.name = ip.getHostName();
	}
}
