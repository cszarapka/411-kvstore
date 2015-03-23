package com.cam.eece411.Messages;

import com.cam.eece411.Utilities.Helper;


/**
 * Representation of a message to be sent to the KVStore
 * @author cam
 *
 */
public class SendMessage {
	public byte[] 	uniqueID;
	public byte 	command;
	public byte[] 	data;
	protected int	index;

	/**
	 * Constructs the base of any message to be sent to the KVStore
	 * @param command	the command of the message
	 */
	public SendMessage(byte command) {
		this.uniqueID = Helper.generateRandomByteArray(16);
		this.command = command;
	}

	/**
	 * Prepares the message data to be built into a packet
	 */
	protected void assembleData() {
		// Start with the unique ID
		for (int i = 0; i < uniqueID.length; i++) {
			data[i] = uniqueID[i];
		}
		// Then the command
		index = uniqueID.length;
		data[index++] = command;
	}
	
	@Override
	public String toString() {
		return "Unique ID: " + Helper.bytesToHexString(uniqueID) + "\n" +
				"Command: " + Integer.toHexString(command) + "\n";
	}
}

