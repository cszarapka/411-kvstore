package com.cam.eece411;

import java.net.DatagramPacket;

import com.cam.eece411.Messages.AppResponse;
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

		// Firstly, if it's a shutdown command, do that and be done with it
		if (rcvdMsg.getCommand() == Protocols.APP_CMD_SHUTDOWN) {
			respondToSHUTDOWN();
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
			// TODO: send it (maybe as a node message?)
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
}