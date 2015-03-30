package com.cam.eece411;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.cam.eece411.Utilities.MD5HashFunction;

/**
 * A representation of our consistent hash - the circle-like structure on which a
 * node is located at a specific position.
 * To assist with load balancing, each machine could be mapped to multiple locations, but we
 * will use the 255/2 method.
 * @author cam
 *
 */
public class Circle {
	private static SortedMap<Integer, Node> circle = Collections.synchronizedSortedMap(new  TreeMap<Integer, Node>());

	// Obligatory constructor
	public Circle() {}
	
	/**
	 * Adds a node to the circle
	 * @param node		the node to add to the circle
	 */
	public static void add(Node node) {
		circle.put(node.nodeNumber, node);
	}
	
	/**
	 * Adds nodes to the circle from a byte array
	 * @param nodes	the nodes to add to the circle
	 */
	public static void add(byte[] nodes)  {
		int index = 0;
		while (index < nodes.length) {
			try {
				add(new Node(nodes[index+4],
						nodes[index+5],
						InetAddress.getByAddress(Arrays.copyOfRange(nodes, index, index+4))));
			} catch (UnknownHostException e) {
				System.out.println("Tried to add a node that ain't got no host.");
			}
			index += 6;
		}
	}

	/**
	 * Removes the node from the circle
	 * @param nodeID	the ID of the node to remove
	 */
	public static void remove(int nodeID) {
		circle.remove(nodeID);
	}
	
	/**
	 * Returns true or false depending on if this node is in our view of the system
	 * @param node	the node to check for
	 * @return		true or false
	 */
	public static boolean containsNode(Node node) {
		return circle.containsValue(node);
	}
	
	public static Set<Entry<Integer, Node>> getNodes() {
		return circle.entrySet();
	}
	
	/**
	 * Returns the node in the circle who is responsible for the 
	 * specified key
	 * @param key	the key's owner we are after
	 * @return		the node who is responsible for the key
	 */
	public static Node findNodeFor(byte[] key) {
		// Hash the key, then find the node that is responsible for the key
		int keyHash = MD5HashFunction.hash(key);
		if(!circle.containsKey(keyHash)) {
			// A tail is all the nodes who are greater than or equal to this hash value
			SortedMap<Integer, Node> tailMap = circle.tailMap(keyHash);
			if (tailMap.isEmpty()) {
				keyHash = circle.firstKey();	// the first node after the "midnight" position
			} else {
				keyHash = tailMap.firstKey();	// the closest, greater than node
			}
		}
		return circle.get(keyHash);
	}
	
	/**
	 * Returns this node's view of the system as a byte array.
	 * The format of the byte array is as follows:
	 * | IP | Node # | Next Node # | ...
	 * @return	all the nodes this node is aware of as a byte array
	 */
	public static byte[] getView() {
		byte[] buffer = new byte[circle.size()*5];
		byte[] ip;
		int index = 0;
		Iterator<Node> nodes = circle.values().iterator();
		Node currNode;
		
		while(nodes.hasNext()) {
			currNode = nodes.next();
			ip = currNode.ip.getAddress();
			for (int i = 0; i < 4; i++) {
				buffer[index++] = ip[i];
			}
			buffer[index++] = (byte) currNode.nodeNumber;
			buffer[index++] = (byte) currNode.nextNodeNumber;
		}
		return buffer;
	}
	
	/**
	 * Returns the count of nodes we have in our circle
	 * @return	an integer count of the nodes in the circle
	 */
	public static int getSize() {
		return circle.size();
	}
	
	public static Collection<Node> nodes() {
		return circle.values();
	}
	
	public static String toText() {
		Iterator<Node> nodes = circle.values().iterator();
		Node currNode;
		
		String string = "| Node Name | Node # | Node's Next Node # |\n";
		
		while (nodes.hasNext()) {
			currNode = nodes.next();
			string += "| " + currNode.name + " | " + currNode.nodeNumber + " | " + currNode.nextNodeNumber + " |\n";
		}
		
		return string;
	}
}
