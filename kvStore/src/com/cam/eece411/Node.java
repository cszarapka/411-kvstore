package com.cam.eece411;

import java.net.InetAddress;

/**
 * A representation of a node in our DHT_KVStore system
 * @author cam
 *
 */
public class Node {
	public int nodeNumber;
	public int nextNodeNumber;
	public InetAddress ip;
	public String name;
	
	/**
	 * Constructs a new node with the specified fields
	 * @param nodeNumber		the node number of the node
	 * @param nextNodeNumber	the next closest (CCW) node's number
	 * @param ip				the IP address of this node
	 */
	public Node(int nodeNumber, int nextNodeNumber, InetAddress ip) {
		if(nodeNumber < 0) {
			//System.out.println("Node number was negative. This should never happen.");
			nodeNumber += 256;
		}
		if(nextNodeNumber < 0) {
			nextNodeNumber += 256;
			System.out.println("nextNodeNumber was negative. This should never happen.");
		}
		this.nodeNumber = nodeNumber;
		this.nextNodeNumber = nextNodeNumber;
		this.ip = ip;
		this.name = ip.getHostName();
	}
}
