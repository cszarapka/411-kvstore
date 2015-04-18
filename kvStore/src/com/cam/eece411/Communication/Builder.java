package com.cam.eece411.Communication;

import java.util.logging.Logger;

import com.cam.eece411.Structures.DHT;
import com.cam.eece411.Structures.Node;
import com.cam.eece411.Utilities.Utils;
import com.cam.eece411.Utilities.Commands;

/**
 * This class is for building messages to be sent between nodes.
 * Each method returns a ready-to-send byte array for a socket.
 * @author cam
 *
 */
public final class Builder {
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(Builder.class.getName());

	/**
	 * Builds a join-request message.
	 * This message is an outsider's method of entry into the DHT.
	 * @return	byte array of the message
	 */
	public static byte[] joinRequest() {
		byte[] buffer = new byte[17];

		// Get a new random Unique ID
		// TODO: (here and all below) use proper unique id???
		byte[] uniqueID = Utils.generateRandomByteArray(16);

		// Add the Unique ID to the buffer
		for (int i = 0; i < uniqueID.length; i++) {
			buffer[i] = uniqueID[i];
		}

		// Add the command to the buffer
		buffer[uniqueID.length] = Commands.JOIN_REQUEST;

		return buffer;
	}

	/**
	 * Builds an is-alive message.
	 * These messages are periodically sent by nodes to let everyone know they are
	 * alive, and to either add the node to our DHT or if it is already in, update it's timestamp.
	 * @param node	the node that is alive
	 * @return		byte array of the message
	 */
	public static byte[] isAlive(Node node) {
		byte[] buffer = new byte[22];
		byte[] ip;
		int index = 0;

		// Get a new random Unique ID
		byte[] uniqueID = Utils.generateRandomByteArray(16);

		// Add the Unique ID to the buffer
		for (int i = 0; i < uniqueID.length; i++) {
			buffer[index++] = uniqueID[i];
		}

		// Add the command to the buffer
		buffer[index++] = Commands.IS_ALIVE;

		// Add the node ID
		buffer[index++] = (byte) node.nodeID;

		// Add the node's IP to the buffer
		ip = node.addr.getAddress();
		for (int i = 0; i < ip.length; i++) {
			buffer[index++] = ip[i];
		}

		return buffer;
	}

	/**
	 * Builds an is-dead message.
	 * These messages are broadcasted by a node when it discovers the death of a node.
	 * @param node	the dead node
	 * @return		byte array of the message
	 */
	public static byte[] isDead(Node node) {
		byte[] buffer = new byte[22];
		byte[] ip;
		int index = 0;

		// Get a new random Unique ID
		byte[] uniqueID = Utils.generateRandomByteArray(16);

		// Add the Unique ID to the buffer
		for (int i = 0; i < uniqueID.length; i++) {
			buffer[index++] = uniqueID[i];
		}

		// Add the command to the buffer
		buffer[index++] = Commands.IS_DEAD;

		// Add the node ID
		buffer[index++] = (byte) node.nodeID;

		// Add the node's IP to the buffer
		ip = node.addr.getAddress();
		for (int i = 0; i < ip.length; i++) {
			buffer[index++] = ip[i];
		}

		return buffer;
	}

	/**
	 * Builds a response to a join-request
	 * @param msg		the join-request message to respond to
	 * @param nodeID	the node ID to offer the outside node
	 * @return			byte array of the message
	 * 
	 * The response has the following format:
	 * | ID | CMD | OfferedNode # | OfferedNextNode # | # of nodes | Nodes (IP, Node #) | 
	 */
	public static byte[] joinResponse(Message msg, int nodeID) {
		byte[] buffer;
		byte[] uniqueID = msg.getUID();
		byte[] circleView;
		int index = 0;

		// Lock the circle
		// TODO: not sure if it should be locked for the whole thing? What the hey why not
		synchronized (DHT.class) {
			buffer = new byte[uniqueID.length + 3 + DHT.getSize()*5];

			// Add the Unique ID to the buffer
			for (int i = 0; i < uniqueID.length; i++) {
				buffer[index++] = uniqueID[i];
			}
			// Add the command to the buffer
			buffer[index++] = Commands.JOIN_RESPONSE;

			// Add offered node number and number of nodes
			buffer[index++] = (byte) nodeID;
			buffer[index++] = (byte) DHT.getSize();

			// Add all the nodes from the circle
			circleView = DHT.getView();
			for (int i = 0; i < circleView.length; i++) {
				buffer[index++] = circleView[i];
			}
		}	
		return buffer;
	}

	/**
	 * Builds an echoed app-layer message.
	 * @param msg	the message to echo
	 * @return		byte array of the message
	 */
	public static byte[] echo(Message msg) {
		byte[] buffer;
		byte[] uniqueID = msg.getUID();
		byte[] data = msg.getData();
		int length = 0;
		int index = 0;

		// Determine the length of the message based on the command
		if (msg.getCommand() == Commands.PUT) {
			length = uniqueID.length + 9 + 1 + msg.getKey().length + 2 + msg.getValueLength();
		} else {
			length = uniqueID.length + 9 + 1 + msg.getKey().length;
		}
		buffer = new byte[length];

		// Assemble the buffer
		// Add the Unique ID
		for (int i = 0; i < uniqueID.length; i++) {
			buffer[index++] = uniqueID[i];
		}
		// Add the command
		buffer[index++] = Commands.ECHOED;

		// Add the IP (4 byte array)
		byte[] originIP = msg.getReturnAddress().getAddress();
		for (int i = 0; i < 4; i++) {
			buffer[index++] = originIP[i];
		}

		// Add the port
		byte[] originPort = Utils.intToByteArray(msg.getReturnPort());
		buffer[index++] = originPort[0];
		buffer[index++] = originPort[1];
		buffer[index++] = originPort[2];
		buffer[index++] = originPort[3];

		// Add the remaining of the rest of the original message
		for (int i = uniqueID.length; index < buffer.length; i++) {
			buffer[index++] = data[i];
		}

		return buffer;
	}

	/**
	 * Builds a replicated put message.
	 * @param msg	the put message to replicate
	 * @return		byte array of the message
	 */
	public static byte[] replicatedPut(Message msg) {
		msg.setCommand(Commands.REP_PUT);
		return msg.getData();
	}
}