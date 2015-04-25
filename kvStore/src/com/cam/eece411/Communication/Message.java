package com.cam.eece411.Communication;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.cam.eece411.Utilities.Commands;
import com.cam.eece411.Utilities.Utils;

public class Message {
	private static final Logger log = Logger.getLogger(Message.class.getName());
	private static final int KEY_LENGTH = 32;
	private static final int UID_LENGTH = 16;

	private byte[] uid, key, value, bytes;
	private byte cmd;
	private short valueLength;

	private int srcPort;
	private byte[] srcPortBytes;
	private byte[] srcAddr;

	private int nid;	// node ID
	private byte[] nAddr;	// node address
	private int numNodes;
	private byte[] nodes;

	private byte appCmd;
	private int index;

	/**
	 * Build a Message object from a received packet
	 * @param packet	the received packet
	 */
	public Message(DatagramPacket packet) {
		// Get the raw data from the packet
		bytes = packet.getData();

		// Get info about the sender
		srcAddr = packet.getAddress().getAddress();
		srcPort = packet.getPort();

		// Get the UniqueID of the message
		uid = Arrays.copyOfRange(bytes, 0, UID_LENGTH);

		// Get the command of the message
		cmd = bytes[UID_LENGTH];
		appCmd = cmd;
		index = UID_LENGTH + 1;

		if (Commands.isKVSCommand(cmd)) {
			parseKVSCommand();
		}
		else if (Commands.isUpdate(cmd)) {
			parseUpdate();
		}
		else if (Commands.isJoinMessage(cmd)) {
			parseJoinMessage();
		}
	}

	/**
	 * Builds an empty message for sending
	 */
	public Message() {
		uid = Utils.generateRandomByteArray(UID_LENGTH);
	}

	/*
	 * Helper parsing functions for the constructor
	 */

	private void parseKVSCommand() {
		// In the echoed case, get the address info about the source of the request
		if (cmd == Commands.ECHOED) {
			// Get the source of the app-level command
			srcAddr = Arrays.copyOfRange(bytes, index, index+4); index += 4;
			srcPortBytes = Arrays.copyOfRange(bytes, index, index+4); index += 4;
			srcPort = Utils.byteArrayToInt(srcPortBytes);

			// Get the KVS command being echoed
			appCmd = bytes[index++];
		}

		if (appCmd < Commands.SHUTDOWN) {
			// Get the key
			key = Arrays.copyOfRange(bytes, index, index+KEY_LENGTH); index += KEY_LENGTH;

			if (appCmd == Commands.PUT) {
				// Get the value length
				valueLength = Utils.byteArrayToShort(Arrays.copyOfRange(bytes, index, index+2));
				index += 2;

				// Get the value
				value = Arrays.copyOfRange(bytes, index, index+valueLength);
			}
		}
	}

	private void parseUpdate() {
		// Get the node ID (of the alive/dead node)
		nid = Utils.unsignedByteToInt(bytes[index++]);

		// Get the address (of the alive/dead node)
		nAddr = Arrays.copyOfRange(bytes, index, index+4);
	}

	private void parseJoinMessage() {
		if (cmd == Commands.JOIN_RESPONSE) {
			// Get the node ID (being offered)
			nid = Utils.unsignedByteToInt(bytes[index++]);

			// Get the number of nodes being sent your way
			numNodes = bytes[index++];

			// Get the nodes (their IP's and ID's)
			nodes = Arrays.copyOfRange(bytes, index, index+(numNodes*5));
		}
	}

	/*
	 * Getters
	 */

	public byte getCommand() {
		return this.cmd;
	}
	
	public byte getAppCommand() {
		return this.appCmd;
	}

	public int getReturnPort() {
		return this.srcPort;
	}

	public InetAddress getReturnAddress() {
		try { return InetAddress.getByAddress(this.srcAddr); }
		catch (UnknownHostException e) { log.log(Level.SEVERE, e.toString(), e); }
		
		return null;
	}

	public int getNodeID() {
		return this.nid;
	}
	
	public InetAddress getNodeAddress() {
		try { return InetAddress.getByAddress(this.nAddr); }
		catch (UnknownHostException e) { log.log(Level.SEVERE, e.toString(), e); }
		
		return null;
	}

	public byte[] getData() {
		return this.bytes;
	}

	public byte[] getUID() {
		return this.uid;
	}

	public short getValueLength() {
		return this.valueLength;
	}

	public byte[] getKey() {
		return this.key;
	}

	public byte[] getValue() {
		return this.value;
	}

	public int getNumberOfNodes() {
		return this.numNodes;
	}

	public byte[] getNodes() {
		return this.nodes;
	}
	
	
	/*
	 * Setters
	 */
	
	public void setCommand(byte command) {
		// Change the command
		this.cmd = command;
		
		// Importnat! Don't forget to change the bytes to reflect this
		this.bytes[UID_LENGTH] = command;
	}
}
