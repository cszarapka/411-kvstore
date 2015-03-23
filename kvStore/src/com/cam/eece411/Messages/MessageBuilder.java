package com.cam.eece411.Messages;

import com.cam.eece411.Circle;
import com.cam.eece411.Node;
import com.cam.eece411.Utilities.Helper;
import com.cam.eece411.Utilities.Protocols;

public class MessageBuilder {
	
	/**
	 * Returns a byte[] buffer that is a ready-to-send request to join the
	 * DHT/table/circle/system/whatever-the-fuck-we-call-it
	 * @return
	 */
	public static byte[] requestToJoin() {
		byte[] buffer = new byte[17];
		byte[] uniqueID = Helper.generateRandomByteArray(16);
		for (int i = 0; i < uniqueID.length; i++) {
			buffer[i] = uniqueID[i];
		}
		buffer[uniqueID.length] = Protocols.CMD_JOIN_REQUEST;
		return buffer;
	}

	// Shouldn't isAlive take a node?
	public static byte[] isAlive() {
		byte[] buffer = new byte[17];
		byte[] uniqueID = Helper.generateRandomByteArray(16);
		for(int i=0;i<uniqueID.length;i++){
			buffer[i] = uniqueID[i];
		}
		buffer[16] = Protocols.CMD_IS_ALIVE;
		return buffer;
	}
	
	public static byte[] isAlive(Node node) {
		byte[] buffer = new byte[21];
		byte[] uniqueID = Helper.generateRandomByteArray(16);
		byte[] ip;
		int index = 0;
		// Add the unique ID
		for (int i = 0; i < uniqueID.length; i++) {
			buffer[index++] = uniqueID[i];
		}
		// Add the command
		buffer[index++] = Protocols.CMD_IS_ALIVE;
		
		// Add the node's IP
		ip = node.ip.getAddress();
		for (int i = 0; i < ip.length; i++) {
			buffer[index] = ip[i];
		}
		
		// Add the node number and next node number
		buffer[index++] = (byte) node.nodeNumber;
		buffer[index++] = (byte) node.nextNodeNumber;
		
		// all done!
		return buffer;
	}
	
	public static byte[] isDead( Node n ) {
		byte[] buffer = new byte[19];
		byte[] nodeID = new byte[2];
		byte[] uniqueID = Helper.generateRandomByteArray(16);
		byte command = Protocols.CMD_IS_DEAD;
		for(int i=0;i<uniqueID.length;i++){
			buffer[i] = uniqueID[i];
		}
		buffer[16] = command;
		nodeID = Helper.intToByteArray(n.nodeNumber);
		buffer[17] = nodeID[0];
		buffer[18] = nodeID[1];
		
		return buffer;
	}
	
	/**
	 * Returns a byte[] buffer that is a ready-to-send response to a join
	 * request-message
	 * @param	the join request message to respond to
	 * @return	buffer to place into a packet that will be sent to a node wanting
	 * 			to join the system
	 * 
	 * The response has the following format:
	 * | ID | CMD | OfferedNode # | OfferedNextNode # | # of nodes | Nodes (IP, Node #) | 
	 * 
	 */
	public static byte[] responseToJoinRequest(ReceivedMessage msg, int offeredNodeNumber, int offeredNextNodeNumber) {
		byte[] buffer;
		byte[] uniqueID = msg.getUniqueID();
		byte[] circleView;
		int index = 0;
		
		// Lock the circle
		synchronized (Circle.class) {
			buffer = new byte[uniqueID.length + 4 + Circle.getSize()*5];
			// Fill the buffer
			// Start with the unique ID
			for (int i = 0; i < uniqueID.length; i++) {
				buffer[index++] = uniqueID[i];
			}
			// Add the command
			buffer[index++] = Protocols.CMD_JOIN_RESPONSE;
			
			// Add offered node numbers and number of nodes
			buffer[index++] = (byte) offeredNodeNumber;
			buffer[index++] = (byte) offeredNextNodeNumber;
			buffer[index++] = (byte) Circle.getSize();
			
			// Add all the nodes from the circle
			circleView = Circle.getView();
			for (int i = 0; i < circleView.length; i++) {
				buffer[index++] = circleView[i];
			}
		}
		
		return buffer;
	}
	
	/**
	 * Returns a byte[] buffer that is a ready-to-send echoed app-layer request-message.
	 * The command Protocols.CMD_ECHOED, sender IP and port are added to the buffer immediately 
	 * after the unique ID (command = 1, IP = 4, port = 2)
	 * @param msg
	 * @return
	 */
	public static byte[] echoedCommand(ReceivedMessage msg) {
		byte[] buffer;
		byte[] uniqueID = msg.getUniqueID();
		byte[] data = msg.getData();
		int length = 0;
		int index = 0;
		
		// Determine the length of the message based on the command
		if (msg.getCommand() == Protocols.APP_CMD_PUT) {
			length = uniqueID.length + 7 + 1 + msg.getKey().length + 2 + msg.getValueLength();
		} else {
			length = uniqueID.length + 7 + 1 + msg.getKey().length;
		}
		buffer = new byte[length];
		
		// Assemble the buffer
		// Start with the unique ID
		for (int i = 0; i < uniqueID.length; i++) {
			buffer[index++] = uniqueID[i];
		}
		// Add the command
		buffer[index++] = Protocols.CMD_ECHOED;
		
		// Add the IP (4 byte array)
		byte[] originIP = msg.getSenderIP().getAddress();
		for (int i = 0; i < 4; i++) {
			buffer[index++] = originIP[i];
		}
		
		// Add the port
		byte[] originPort = Helper.intToByteArray(msg.getSenderPort());
		buffer[index++] = originPort[0];
		buffer[index++] = originPort[1];
		
		// Add the remaining of the rest of the original message
		for (int i = uniqueID.length; i < data.length; i++) {
			buffer[index++] = data[i];
		}
		
		return buffer;
	}
	
	public static byte[] joinConfirm(ReceivedMessage msg) {
		byte[] buffer = new byte[19];
		byte[] uniqueID = msg.getUniqueID();
		int index = 0;
		for (int i = 0; i < uniqueID.length; i++) {
			buffer[index++] = uniqueID[i];
		}
		buffer[index++] = Protocols.CMD_JOIN_CONFIRM;
		buffer[index++] = (byte) msg.getOfferedNodeNumber();
		buffer[index++] = (byte) msg.getOfferedNextNodeNumber();
		return buffer;
	}
}
