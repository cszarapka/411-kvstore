package com.cam.eece411.Messages;

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
		byte command = Protocols.CMD_JOIN_REQUEST;
		for (int i = 0; i < uniqueID.length; i++) {
			buffer[i] = uniqueID[i];
		}
		buffer[uniqueID.length] = command;
		return buffer;
	}
	
	public static byte[] isAlive() {
		byte[] buffer = new byte[17];
		byte[] uniqueID = Helper.generateRandomByteArray(16);
		byte command = Protocols.CMD_IS_ALIVE;
		for(int i=0;i<uniqueID.length;i++){
			buffer[i] = uniqueID[i];
		}
		buffer[16] = command;
		return buffer;
	}
	
	/**
	 * Returns a byte[] buffer that is a ready-to-send response to a join
	 * request-message
	 * @return
	 */
	public static byte[] responseToJoinRequest() {
		byte[] buffer;
		
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
}
