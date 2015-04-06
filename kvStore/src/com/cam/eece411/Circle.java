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

import com.cam.eece411.Utilities.Helper;
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
	 * @param node	the node to add to the circle
	 */
	public static void add(Node node) {
		circle.put(node.nodeNumber, node);
		// Print out the nodes in the circle
		if(Server.VERBOSE) System.out.println("\nCircle contents:\n" + toText());
	}

	/**
	 * Adds nodes to the circle from a byte array
	 * @param nodes	the nodes to add to the circle
	 */
	public static void add(byte[] nodes)  {
		int index = 0;
		while (index < nodes.length) {
			try {
				add(new Node(Helper.unsignedByteToInt(nodes[index+4]),
						InetAddress.getByAddress(Arrays.copyOfRange(nodes, index, index+4))));
			} catch (UnknownHostException e) {
				if(Server.VERBOSE) System.out.println("Tried to add a node that ain't got no host.");
			}
			index += 5;
		}
	}

	/**
	 * Removes the node from the circle
	 * @param nodeID	the ID of the node to remove
	 */
	public static void remove(int nodeID) {
		circle.remove(nodeID);
		// Print out the nodes in the circle
		if(Server.VERBOSE) System.out.println("\nCircle contents:\n" + toText());
	}

	/**
	 * Returns true or false depending on if this node is in our view of the system
	 * @param node	the node to check for
	 * @return		true or false
	 */
	public static boolean containsNode(Node node) {
		return circle.containsValue(node);
	}
	
	/**
	 * Returns the node associated with the node number, or null if not in circle
	 * @param nodeNumber the number to check for
	 * @return the associated node or null
	 */
	public static Node getNodeByNumber(int nodeNumber) {
		return circle.get(nodeNumber);
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
		if(Server.VERBOSE) System.out.println("\n keyHash: " + keyHash);
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
	 * Returns the closest CCW node to the specified node
	 * @param node	the node to find the closest CCW node of
	 * @return	the closest CCW node to the specified node
	 */
	public static Node getNextNodeOf(Node node) {
		int nextNodeNumber;
		// Get all the nodes strictly lower than us, exclusive
		SortedMap<Integer, Node> headMap = circle.headMap(node.nodeNumber);
		
		if (headMap.isEmpty()) {
			// if there are no nodes in the head map, get the largest valued node
			nextNodeNumber = circle.lastKey();
			if(Server.VERBOSE) System.out.println("head map's empty\n");
		} else {
			// if there are nodes smaller than us, get the largest one (closest)
			nextNodeNumber = headMap.lastKey();
			if(Server.VERBOSE) System.out.println("head map'sn't empty\n");
		}
		return circle.get(nextNodeNumber);
	}
	
	/**
	 * Returns the closest CW node to the specified node
	 * @param node	the node to find the closest CW node of
	 * @return	the closest CCW node to the specified node
	 */
	public static Node getPrevNodeOf(Node node){
		int nextNodeNumber;
		//Get all the nodes strictly higher than us, exclusive
		SortedMap<Integer, Node> tailMap = circle.tailMap(node.nodeNumber);
		
		if(tailMap.isEmpty()){
			// if there are no nodes in the tail map, get the smallest valued node
			nextNodeNumber = circle.firstKey();
			System.out.println("tail map's empty\n");
		} else {
			// if there are nodes smaller than us, get the smallest one (closest)
			nextNodeNumber = tailMap.firstKey();
			System.out.println("tail map'sn't empty\n");
		}
		return circle.get(nextNodeNumber);
	}

	/**
	 * Returns this node's view of the system as a byte array.
	 * The format of the byte array is as follows:
	 * | IP | Node # | ...
	 * @return	all the nodes in this node's circle as a byte array
	 */
	public static byte[] getView() {
		// 4 bytes for IP and 1 for node number
		byte[] buffer = new byte[circle.size()*5];
		byte[] ip;
		int index = 0;
		Iterator<Node> nodes = circle.values().iterator();
		Node node;
		
		while(nodes.hasNext()) {
			node = nodes.next();
			ip = node.ip.getAddress();
			for (int i = 0; i < 4; i++) {
				buffer[index++] = ip[i];
			}
			buffer[index++] = (byte) node.nodeNumber;
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

	/**
	 * Returns the nodes in the circle as collection
	 * @return	a collection of the nodes in this circle
	 */
	public static Collection<Node> nodes() {
		return circle.values();
	}

	/**
	 * A toString() for a static member
	 * @return	the node #'s and node name's of the node in this circle
	 */
	public static String toText() {
		Iterator<Node> nodes = circle.values().iterator();
		Node currNode;

		String string = "| Node # | Node Name |\n";

		while (nodes.hasNext()) {
			currNode = nodes.next();
			string += "| " + currNode.nodeNumber + " | " + currNode.name + " |\n";
		}

		return string;
	}
}
