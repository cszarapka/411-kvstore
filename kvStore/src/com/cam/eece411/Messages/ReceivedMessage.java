package com.cam.eece411.Messages;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Arrays;

import com.cam.eece411.Utilities.Helper;
import com.cam.eece411.Utilities.Protocols;

public class ReceivedMessage {
	protected InetAddress senderIP;
	protected int senderPort;
	protected byte[] uniqueID;
	protected byte command;
	protected byte[] data;
	
	private byte[] key;
	private int valueLength;
	private byte[] value;
	private int nodeID;
	
	public ReceivedMessage(DatagramPacket packet) {
		// Get the guaranteed data
		senderIP = packet.getAddress();
		senderPort = packet.getPort();
		data = packet.getData();
		uniqueID = Arrays.copyOfRange(data, 0, 16);
		command = data[16];
		
		// Get the key, if there is one
		if (command < Protocols.APP_CMD_SHUTDOWN) {
			key = Arrays.copyOfRange(data, 17, 49);
		}
		
		// Get the value length and value, if there are any
		if (command == Protocols.APP_CMD_PUT) {
			valueLength = Helper.valueLengthBytesToInt(Arrays.copyOfRange(data, 49, 51));
			value = Arrays.copyOfRange(data, 51, 51+valueLength);
		}
		
		if (command == Protocols.CMD_IS_DEAD) {
			nodeID = Helper.valueLengthBytesToInt(Arrays.copyOfRange(data, 17, 18));
		}
	}
	
	//used for isDead
	public int getNodeID() {
		return this.nodeID;
	}
	
	public byte[] getData() {
		return this.getData();
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
	 * Returns the IP address of the immediate sender of this message
	 * @return	the IP address of the sender
	 */
	public InetAddress getSenderIP() {
		return this.senderIP;
	}
	
	/**
	 * Returns the port this message was sent on
	 * @return	the port
	 */
	public int getSenderPort() {
		return this.senderPort;
	}
	
	public int getValueLength() {
		return this.valueLength;
	}
	
	public byte[] getKey() {
		return this.key;
	}
	
	public byte[] getValue() {
		return this.value;
	}
	
//	public AppResponse buildResponse() {
//		
//	}
	
	public String toString() {
		String string =	"Unique ID: " + Helper.bytesToHexString(uniqueID) + "\n" +
						"Command: " + Integer.toHexString(command) + "\n";
		
		// Get the key, if there is one
		if (command < Protocols.APP_CMD_SHUTDOWN) {
			string += "Key: " + Helper.bytesToHexString(key) + "\n";
		}

		// Get the value length and value, if there are any
		if (command == Protocols.APP_CMD_PUT) {
			string += "Value-Length: " + valueLength + "\n";
			string += "Value: " + Helper.bytesToHexString(value) + "\n";
		}
		
		return string;
	}
}
