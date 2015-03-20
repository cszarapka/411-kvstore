package com.cam.eece411;

import java.util.Collections;
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
}
