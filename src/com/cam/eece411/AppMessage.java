package com.cam.eece411;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Arrays;

/**
 * Representation of a received request message at the app-layer.
 * App-layer meaning this message was sent to a node in our system by
 * a user, not another node.
 * 
 * Also provides static methods for building app-layer response messages.
 * @author cam
 *
 */
public class AppMessage extends Message {
	private InetAddress ip;
	private byte[] uniqueID;
	private byte command;
	private byte[] key;
	private int valueLength;
	private byte[] value;

	/**
	 * Constructs an AppMessage object from the received request
	 * @param data	the byte[] data of the packet received
	 */
	public AppMessage(DatagramPacket packet) {
		super(packet);
		ip = packet.getAddress();
		
		// Get the unique ID, it's always the first 16 bytes
		uniqueID = Arrays.copyOfRange(data, 0, 16);
		
		// Get the command, it is always the first byte after the unique ID
		command = data[16];

		// Use the command to determine which fields come next
		switch (command) {
		case Protocols.APP_CMD_GET:
			key = Arrays.copyOfRange(data, 17, 49);
			valueLength = -1;
			value = null;
			break;
		case Protocols.APP_CMD_PUT:
			key = Arrays.copyOfRange(data, 17, 49);
			valueLength = Helper.byteArrayToInt(Arrays.copyOfRange(data, 49, 51));
			value = Arrays.copyOfRange(data, 51, 51+valueLength);
			break;
		case Protocols.APP_CMD_REMOVE:
			key = Arrays.copyOfRange(data, 17, 49);
			valueLength = -1;
			value = null;
			break;
		case Protocols.APP_CMD_SHUTDOWN:
			key = null;
			valueLength = -1;
			value = null;
			break;
		}
	}
	
	/**
	 * Returns the unique ID of the request message
	 * @return	the unique ID
	 */
	public byte[] getUniqueID() {
		return this.uniqueID;
	}

	/**
	 * Returns the command of the request message
	 * @return	either a GET, PUT, REMOVE or SHUTDOWN command
	 */
	public byte getCommand() {
		return this.command;
	}

	/**
	 * Returns the key of the request message, or null if there is none
	 * @return	the key of the message, or null if there is none
	 */
	public byte[] getKey() {
		return this.key;
	}

	/**
	 * Returns the length of the value in the request message
	 * @return	if this is a put request, it will return the value-length
	 * 			else, it will return -1
	 */
	public int getValueLength() {
		return this.valueLength;
	}

	/**
	 * Returns the value of the request message, or null if there is none
	 * @return	the value if it is a put request
	 * 			null if it is any other request
	 */
	public byte[] getValue() {
		return this.value;
	}
	
	/**
	 * Returns the IP address of the immediate sender of this message
	 * @return	the IP address of the sender
	 */
	public InetAddress getIP() {
		return this.ip;
	}

	/**
	 * Returns a ready-to-send response message based on the parameters
	 * @param uniqueID		the unique ID to prepend to the message
	 * @param responseCode	the response code for the message
	 * @return				byte[] response message ready to send
	 */
	public static byte[] buildResponse(byte[] uniqueID, byte responseCode) {
		byte[] data = new byte[uniqueID.length + 1];
		
		// Prepend the message with the unique ID
		for (int i = 0; i < uniqueID.length; i++) {
			data[i] = uniqueID[i];
		}
		// Add the response code
		data[uniqueID.length] = responseCode;
		return data;
	}

	/**
	 * Returns a ready-to-send response message based on the parameters
	 * @param uniqueID		the unique ID to prepend to the message
	 * @param responseCode	the response code for the message
	 * @param valueLength	the length of the value in the message
	 * @param value			the value originally requested
	 * @return				byte[] response message ready to send
	 */
	public static byte[] buildResponse(byte[] uniqueID, byte responseCode, int valueLength, byte[] value) {
		byte[] data = new byte[uniqueID.length + 1 + 2 + valueLength];
		
		// Prepend the unique ID to the message
		for (int i = 0; i < uniqueID.length; i++) {
			data[i] = uniqueID[i];
		}
		int offset = uniqueID.length;
		
		// Add the response code and 2 byte value length
		data[offset] = responseCode;
		data[++offset] = Helper.intToByteArray(valueLength)[0];
		data[++offset] = Helper.intToByteArray(valueLength)[1];
		offset++;
		
		// Add the value to the message
		for (int i = 0; i < valueLength; i++) {
			data[i+offset] = value[i];
		}
		return data;
	}
}
