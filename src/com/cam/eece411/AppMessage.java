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
		
		// Get the command, it is always the first byte
		command = data[0];

		// Use the command to determine which fields come next
		switch (command) {
		case Protocols.APP_CMD_GET:
			key = Arrays.copyOfRange(data, 1, 33);
			valueLength = -1;
			value = null;
			break;
		case Protocols.APP_CMD_PUT:
			key = Arrays.copyOfRange(data, 1, 33);
			valueLength = Helper.byteArrayToInt(Arrays.copyOfRange(data, 33, 35));
			value = Arrays.copyOfRange(data, 35, 35+valueLength);
			break;
		case Protocols.APP_CMD_REMOVE:
			key = Arrays.copyOfRange(data, 1, 33);
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
	 * @param responseCode	the response code for the message
	 * @return				byte[] response message ready to send
	 */
	public static byte[] buildResponse(byte responseCode) {
		byte[] data = new byte[1];
		data[0] = responseCode;
		return data;
	}

	/**
	 * Returns a ready-to-send response message based on the parameters
	 * @param responseCode	the response code for the message
	 * @param valueLength	the length of the value in the message
	 * @param value			the value originally requested
	 * @return				byte[] response message ready to send
	 */
	public static byte[] buildResponse(byte responseCode, int valueLength, byte[] value) {
		byte[] data = new byte[1 + 2 + valueLength];
		data[0] = responseCode;
		data[1] = Helper.intToByteArray(valueLength)[0];
		data[2] = Helper.intToByteArray(valueLength)[1];
		for (int i = 0; i < valueLength; i++) {
			data[i+3] = value[i];
		}
		return data;
	}
}
