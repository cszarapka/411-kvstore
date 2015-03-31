package com.cam.eece411;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Iterator;

import com.cam.eece411.Messages.AppResponse;
import com.cam.eece411.Messages.MessageBuilder;
import com.cam.eece411.Messages.PutRequest;
import com.cam.eece411.Messages.ReceivedMessage;
import com.cam.eece411.Utilities.MD5HashFunction;
import com.cam.eece411.Utilities.Protocols;

public class ResponseHandler implements Runnable {
	private ReceivedMessage rcvdMsg;
	@SuppressWarnings("unused")
	private AppResponse appResponse;

	public ResponseHandler(DatagramPacket packet) {
		rcvdMsg = new ReceivedMessage(packet);
	}

	/**
	 * Responds to an app-layer request-message by building and sending
	 * a response-message
	 */
	public void run() {
		System.out.println("- - Response thread launched to handle:");
		System.out.println(rcvdMsg.toString());
		
		if (Server.state == Protocols.HANDLING_JOIN) {
			if (rcvdMsg.getCommand() == Protocols.CMD_JOIN_CONFIRM) {
				respondToJOIN_CONFIRM();
			}
			return;
		}

		// Check to see if it's one of the commands that isn't serviceable by a specific node
		switch (rcvdMsg.getCommand()) {
			case Protocols.APP_CMD_SHUTDOWN: respondToSHUTDOWN(); return;
			case Protocols.CMD_JOIN_REQUEST: respondToJOIN_REQUEST(); return;
			case Protocols.CMD_IS_DEAD: respondToISDEAD(); return;
			case Protocols.CMD_IS_ALIVE: respondToIS_ALIVE(); return;
		}

		// Find the node that should be servicing this command ** causes null pointer exception when there is no key
		Node servicingNode;
		synchronized(Circle.class) {
			servicingNode = Circle.findNodeFor(rcvdMsg.getKey());
		}

		// Check if we are the servicing node
		if (Server.me.nodeNumber == servicingNode.nodeNumber) {
			// Determine the command and respond to it
			switch (rcvdMsg.getCommand()) {
				case Protocols.APP_CMD_PUT: respondToPUT(); break;
				case Protocols.APP_CMD_GET: respondToGET(); break;
				case Protocols.APP_CMD_REMOVE: respondToREMOVE(); break;
				
			}
		} else {
			// Send it to the servicing node
			Server.sendMessage(MessageBuilder.echoedCommand(rcvdMsg), servicingNode.ip, Protocols.LISTENING_PORT);
		}
	}

	private void respondToISDEAD() {
		synchronized(Circle.class) {
			// Put the key-value pair into our store, or
			Circle.remove(rcvdMsg.getNodeID());
		}
	}
	
	private void respondToPUT() {
		byte responseCode;
		synchronized(KeyValueStore.class) {
			// Put the key-value pair into our store, or
			responseCode = KeyValueStore.put(rcvdMsg.getKey(), rcvdMsg.getValue());
		}
		// Build the response based on the success of the put, then send it
		Server.sendMessage(new AppResponse(rcvdMsg, responseCode));
	}

	private void respondToGET() {
		byte[] value;
		synchronized(KeyValueStore.class) {
			// Get the value from our key-value store
			value = KeyValueStore.get(rcvdMsg.getKey());
		}
		// Based on the value, build our response and send it
		if (value != null) {
			Server.sendMessage(new AppResponse(rcvdMsg, value));
		} else {
			Server.sendMessage(new AppResponse(rcvdMsg, Protocols.CODE_KEY_DNE));
		}
	}

	private void respondToREMOVE() {
		byte responseCode;
		synchronized(KeyValueStore.class) {
			// Remove the pair from our key-value store
			responseCode = KeyValueStore.remove(rcvdMsg.getKey());
		}
		// Build our response and send it
		Server.sendMessage(new AppResponse(rcvdMsg, responseCode));
	}

	private void respondToSHUTDOWN() {
		Server.sendMessage(new AppResponse(rcvdMsg, Protocols.CODE_SUCCESS));
		System.out.println("It shuts down and simulates a crash now.. :(");
		System.exit(0);
	}

	private void respondToIS_ALIVE() {

		byte responseCode = Protocols.CODE_SUCCESS;
		InetAddress inet = null;
		try {
			inet = InetAddress.getByAddress(rcvdMsg.getNodeIP());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Node node = new Node(rcvdMsg.getOfferedNodeNumber(),inet);
		synchronized(Circle.class) {
			// Put the key-value pair into our store, or
			Circle.add(node);
		}
		// Build the response based on the success of the put, then send it
		AppResponse response = new AppResponse(rcvdMsg, responseCode);
		response.portToSendTo = Protocols.IS_ALIVE_RESPONSE_PORT;
		Server.sendMessage(response);
	}

	private void respondToJOIN_REQUEST() {
		int myNumber, nextNodeNumber, offeredNodeNumber;
		int maxNodeCount = Protocols.MAX_NUMBER_OF_NODES;
		System.out.println("I'M IN THIS PART NOW\n");
		synchronized(Server.state) {
			synchronized(Circle.class) {
				Server.state = Protocols.HANDLING_JOIN;
				myNumber = Server.me.nodeNumber;
				nextNodeNumber = Circle.getNextNodeOf(Server.me).nodeNumber;
			}	
		}
		
		// Determine number half way between us and next node number
		if(myNumber == nextNodeNumber) {
			offeredNodeNumber = (myNumber - (maxNodeCount / 2) + maxNodeCount) % maxNodeCount;
		} else {
			offeredNodeNumber = (myNumber - (((myNumber - nextNodeNumber + maxNodeCount) % maxNodeCount) / 2) + maxNodeCount) % maxNodeCount;
		}
		System.out.println((myNumber - (((myNumber - nextNodeNumber + maxNodeCount) % maxNodeCount) / 2) + maxNodeCount) % maxNodeCount);
		System.out.println("My node number: " + myNumber + "\nNext node number: " + nextNodeNumber + "\noffered node number: " + offeredNodeNumber + "\n");
		// Add this node to our table
		Node newNode = new Node(offeredNodeNumber, rcvdMsg.getSenderIP());
		synchronized(Circle.class) {
			Circle.add(newNode);
		}
		
		Server.sendMessage( MessageBuilder.responseToJoinRequest(rcvdMsg, offeredNodeNumber),
				rcvdMsg.getSenderIP(), Protocols.JOIN_RESPONSE_PORT );
	}
	
	private void respondToJOIN_CONFIRM() {
		Iterator<ByteBuffer> keys = KeyValueStore.getKeys().iterator();
		byte[] currentKey = new byte[32];
		int hash, hisNextNodeNumber;
		int hisNodeNumber = rcvdMsg.getOfferedNodeNumber();
		Node newNode = new Node(hisNodeNumber, rcvdMsg.getSenderIP());

		synchronized(Circle.class) {
			hisNextNodeNumber = Circle.getNextNodeOf(newNode).nodeNumber;
		}
		
		synchronized(Server.state) {
			Server.state = Protocols.IN_TABLE;
		}
		
		// TODO: Optimization: make it so that nodes reply to a new type of put message with a remove
		// Go through all of the keys
		while (keys.hasNext()) {
			// Unwrap the array from the byte buffer
			currentKey = keys.next().array();
			
			// Hash the key
			hash = MD5HashFunction.hash(currentKey);
			
			// Only PUT this key/value pair to the new node if he should have it
			if (hisNodeNumber > hisNextNodeNumber) {
				if (hisNodeNumber >= hash && hash >= hisNextNodeNumber) {
					Server.sendMessage((new PutRequest(currentKey, KeyValueStore.get(currentKey))).data, rcvdMsg.getSenderIP(), Protocols.LISTENING_PORT);
					KeyValueStore.remove(currentKey);
				}
 			} else {
 				if (hisNodeNumber >= hash || hash >= hisNextNodeNumber) {
 					Server.sendMessage((new PutRequest(currentKey, KeyValueStore.get(currentKey))).data, rcvdMsg.getSenderIP(), Protocols.LISTENING_PORT);
 					KeyValueStore.remove(currentKey);
 				}
 			}
		}
		
		// Now broadcast to all the other nodes the update - send an isAlive message to all nodes in circle
		// Nodes that don't have it will add it
		Iterator<Node> nodes = Circle.nodes().iterator();
		Node node;
		while (nodes.hasNext()) {
			node = nodes.next();
			if (node.nodeNumber != Server.me.nodeNumber) {
				Server.sendMessage(MessageBuilder.isAlive(newNode), node.ip, Protocols.LISTENING_PORT);
			}
		}
	}
}