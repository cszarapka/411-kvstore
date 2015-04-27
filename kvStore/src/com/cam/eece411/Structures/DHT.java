package com.cam.eece411.Structures;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.cam.eece411.Server;
import com.cam.eece411.Utilities.HashFunction;
import com.cam.eece411.Utilities.Protocols;
import com.cam.eece411.Utilities.Utils;

/**
 * A representation of our consistent hash - the circle-like structure on which a
 * node is located at a specific position.
 * To assist with load balancing, each machine could be mapped to multiple locations, but we
 * will use the 255/2 method.
 * @author cam
 *
 */
public class DHT {
	private static final Logger log = Logger.getLogger(DHT.class.getName());
	
	private static SortedMap<Integer, Node> circle = Collections.synchronizedSortedMap(new  TreeMap<Integer, Node>());

	// Obligatory constructor
	public DHT() {}

	/**
	 * Adds a node to the circle
	 * @param node	the node to add to the circle
	 */
	public static void add(Node node) {
		log.setLevel(Protocols.LOGGER_LEVEL);
		circle.put(node.nodeID, node);
		// Print out the nodes in the circle
		log.info("Circle contents after ADD(" + node.nodeID + ")\n" + toText());
	}

	/**
	 * Adds nodes to the circle from a byte array
	 * @param nodes	the nodes to add to the circle
	 */
	public static void add(byte[] nodes)  {
		log.setLevel(Protocols.LOGGER_LEVEL);
		int index = 0;
		while (index < nodes.length) {
			try {
				add(new Node(Utils.unsignedByteToInt(nodes[index+4]),
						InetAddress.getByAddress(Arrays.copyOfRange(nodes, index, index+4))));
			} catch (UnknownHostException e) {
				log.log(Level.SEVERE, e.toString(), e);
			}
			index += 5;
		}
	}

	/**
	 * Removes the node from the circle
	 * @param nodeID	the ID of the node to remove
	 */
	public static Node remove(int nodeID) {
		log.setLevel(Protocols.LOGGER_LEVEL);
		Node removedNode = circle.remove(nodeID);
		if (removedNode == null) {
			log.info("Node " + nodeID + " was not here to remove");
		} else {
			log.info("Circle contents after REMOVE(" + nodeID + ")\n" + toText());
		}
		return removedNode;
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
	public static Node getNode(int nodeNumber) {
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
		int keyHash = HashFunction.hash(key);
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
		SortedMap<Integer, Node> headMap = circle.headMap(node.nodeID);
		
		if (headMap.isEmpty()) {
			// if there are no nodes in the head map, get the largest valued node
			nextNodeNumber = circle.lastKey();
		} else {
			// if there are nodes smaller than us, get the largest one (closest)
			nextNodeNumber = headMap.lastKey();
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
		SortedMap<Integer, Node> tailMap = circle.tailMap(node.nodeID);
		
		if(tailMap.size() == 1){
			// if there are no nodes in the tail map, get the smallest valued node
			nextNodeNumber = circle.firstKey();
		} else {
			// if there are nodes greater than us, get the smallest one (closest)
			tailMap = circle.tailMap(node.nodeID+1);
			nextNodeNumber = tailMap.firstKey();
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
			ip = node.addr.getAddress();
			for (int i = 0; i < 4; i++) {
				buffer[index++] = ip[i];
			}
			buffer[index++] = (byte) node.nodeID;
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
	
	public static List<InetAddress> broadcastList() {
		List<InetAddress> list = new ArrayList<InetAddress>();
		Iterator<Node> nodes = nodes().iterator();
		
		while (nodes.hasNext()) {
			list.add(nodes.next().addr);
		}
		
		return list;
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
			string += "| " + currNode.nodeID + " | " + currNode.name + " |\n";
		}

		return string;
	}
}
