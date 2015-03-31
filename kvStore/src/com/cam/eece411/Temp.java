package com.cam.eece411;

import java.util.Iterator;

import com.cam.eece411.Messages.NodeMessage;
import com.cam.eece411.Utilities.Protocols;

/**
 * A place where I hold temporary code I'm not sure if I want to keep
 * @author cam
 *
 */
public class Temp {

	/**
	 * Constructs a DHT with a view of the system accepted from the node bringing
	 * you into it.
	 * @param nodeIDs		the node ID's received in the join response message
	 * @param nextNodeIDs	the next-node ID's received in the join response message
	 * @param IPs			the IP's of the nodes (4 byte array)
	 */
	//	public DHT(NodeMessage joinResponse) {
	//		circle = Collections.synchronizedSortedMap(new  TreeMap<Integer, Node>());
	//		KVStore = new ConcurrentHashMap<byte[], byte[]>();
	//		for (int i = 0; i < nodeIDs.length; i++) {
	//			try {
	//				addNode(nodeIDs[i], new Node(nodeIDs[i], nextNodeIDs[i], InetAddress.getByAddress(Arrays.copyOfRange(IPs, (i*4), (i+1)*4))));
	//			} catch (UnknownHostException e) {
	//				System.out.println("Node: " + nodeIDs[i] + " could not be added.");
	//			}
	//		}
	//	}



	/**
	 * This could be done in the responseHandler thread constructor
	 */
	//public void reactTo(DatagramPacket packet) {
	//	// Determine the message type (app request, or node message)
	//	if (packet.getData()[16] <= Protocols.APP_CMD_SHUTDOWN) {
	//		// TODO: launch the thread from here
	//		respondToAppRequest(new ReceivedMessage(packet));
	//	} else {
	//		// TODO: launch the thread here
	//		respondToNodeMessage(new NodeMessage(packet));
	//	}
	//}

	public void respondToNodeMessage(NodeMessage msg) {

	}

	// Change the node who brought us in
//	Iterator<Node> nodes = Circle.nodes().iterator();
//	int distance = Protocols.MAX_NUMBER_OF_NODES;
//	Node nodeToEdit = null;
//	Node currNode;
//	while (nodes.hasNext()) {
//		currNode = nodes.next();
//		if(currNode.nodeNumber != Server.me.nodeNumber) {
//			if (((currNode.nodeNumber - Server.me.nodeNumber) % Protocols.MAX_NUMBER_OF_NODES) < distance) {
//
//				distance = (currNode.nodeNumber - Server.me.nodeNumber) % Protocols.MAX_NUMBER_OF_NODES;
//				nodeToEdit = currNode;
//			}
//		}
//	}
//	nodeToEdit.nextNodeNumber = Server.me.nodeNumber;
//	Node nodeToAdd = new Node(nodeToEdit.nodeNumber,Server.me.nodeNumber,nodeToEdit.ip);
//	Circle.add(nodeToAdd);
}
