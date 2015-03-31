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
		System.out.println("- - - - - - - - - - - - - - - - - -");
		System.out.print(rcvdMsg.toString());
		
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

		System.out.println(Circle.toText());
		
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
		Node node = new Node(rcvdMsg.getOfferedNodeNumber(),rcvdMsg.getOfferedNextNodeNumber(),inet);
		synchronized(Circle.class) {
			// Put the key-value pair into our store, or
			Circle.add(node);
		}
		// Build the response based on the success of the put, then send it
		AppResponse response = new AppResponse(rcvdMsg, responseCode);
		response.portToSendTo = Protocols.IS_ALIVE_RESPONSE_PORT;
		Server.sendMessage(response);

		System.out.println(Circle.toText());

	}

	private void respondToJOIN_REQUEST() {
		synchronized(Server.state) {
			Server.state = Protocols.HANDLING_JOIN;
		}
		
		int offeredNodeNumber;
		if(Server.me.nodeNumber == Server.me.nextNodeNumber) {
			offeredNodeNumber = (Server.me.nodeNumber - (Protocols.MAX_NUMBER_OF_NODES / 2)) % Protocols.MAX_NUMBER_OF_NODES;
		} else {
			offeredNodeNumber = ((((Server.me.nextNodeNumber - Server.me.nodeNumber) % Protocols.MAX_NUMBER_OF_NODES) / 2) + Server.me.nodeNumber) % Protocols.MAX_NUMBER_OF_NODES;
		}
		int offeredNextNodeNumber = Server.me.nextNodeNumber;
		
		Server.sendMessage( MessageBuilder.responseToJoinRequest(rcvdMsg, offeredNodeNumber, offeredNextNodeNumber),
				rcvdMsg.getSenderIP(), Protocols.JOIN_RESPONSE_PORT );
	}
	
	private void respondToJOIN_CONFIRM() {
		Iterator<ByteBuffer> keys = KeyValueStore.getKeys().iterator();
		byte[] currentKey = new byte[32];
		int hash;
		
		// Add this node to our table
		Node aliveNode = new Node(rcvdMsg.getOfferedNodeNumber(), rcvdMsg.getOfferedNextNodeNumber(), rcvdMsg.getSenderIP());
		Circle.add(aliveNode);
		Server.state = Protocols.IN_TABLE;
		
		synchronized(Server.me) {
			Server.me.nextNodeNumber = aliveNode.nodeNumber;
		}
		
		// TODO: Optimization: make it so that nodes reply to a new type of put message with a remove
		// Go through all of the keys
		while (keys.hasNext()) {
			// Unwrap the array from the byte buffer
			currentKey = keys.next().array();
			
			// Hash the key
			hash = MD5HashFunction.hash(currentKey);
			
			// Only PUT this key/value pair to the new node if he should have it
			if (rcvdMsg.getOfferedNodeNumber() > rcvdMsg.getOfferedNextNodeNumber()) {
				if (rcvdMsg.getOfferedNodeNumber() >= hash && hash >=rcvdMsg.getOfferedNextNodeNumber()) {
					Server.sendMessage((new PutRequest(currentKey, KeyValueStore.get(currentKey))).data, rcvdMsg.getSenderIP(), Protocols.LISTENING_PORT);
					KeyValueStore.remove(currentKey);
				}
 			} else {
 				if (rcvdMsg.getOfferedNodeNumber() >= hash || hash >= rcvdMsg.getOfferedNextNodeNumber()) {
 					Server.sendMessage((new PutRequest(currentKey, KeyValueStore.get(currentKey))).data, rcvdMsg.getSenderIP(), Protocols.LISTENING_PORT);
 					KeyValueStore.remove(currentKey);
 				}
 			}
		}

		System.out.println(Circle.toText());
		
		// Now broadcast to all the other nodes the update - send an isAlive message to all nodes in circle
		// Nodes that don't have it will add it
		Iterator<Node> nodes = Circle.nodes().iterator();
		Node node;
		while (nodes.hasNext()) {
			node = nodes.next();
			Server.sendMessage(MessageBuilder.isAlive(aliveNode), node.ip, Protocols.LISTENING_PORT);
		}
	}
}