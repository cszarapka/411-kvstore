package com.cam.eece411;

import java.net.DatagramPacket;

import com.cam.eece411.Messages.AppResponse;
import com.cam.eece411.Messages.MessageBuilder;
import com.cam.eece411.Messages.ReceivedMessage;
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
		
		// Check to see if it's one of the commands that isn't serviceable by a specific node
		switch (rcvdMsg.getCommand()) {
			case Protocols.APP_CMD_SHUTDOWN: respondToSHUTDOWN(); break;
			case Protocols.CMD_JOIN_REQUEST: respondToJOIN_REQUEST(); return;
			case Protocols.CMD_IS_ALIVE: respondToIS_ALIVE(); break;
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

			// Build the response based on the success of the put, then send it
			AppResponse response = new AppResponse(rcvdMsg, responseCode);
			response.portToSendTo = Protocols.IS_ALIVE_RESPONSE_PORT;
			Server.sendMessage(response);
		
	}
	
	private void respondToJOIN_REQUEST() {
		
	}
}