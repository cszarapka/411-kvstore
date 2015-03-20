package com.cam.eece411;

import com.cam.eece411.Messages.NodeMessage;

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
}
