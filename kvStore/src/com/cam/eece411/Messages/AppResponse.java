package com.cam.eece411.Messages;

import java.net.InetAddress;

import com.cam.eece411.Utilities.Helper;
import com.cam.eece411.Utilities.Protocols;

/**
 * Representation of a message that is a response to an app-layer
 * request message: PUT, GET, REMOVE or SHUTDOWN
 * @author cam
 *
 */
public class AppResponse {
	public byte[] uniqueID;
	public byte responseCode;
	public byte[] buffer;
	public InetAddress ipToSendTo;
	public int portToSendTo;
	private int valueLength;
	private byte[] value;
	
	
	/**
	 * Constructs a new response message that is a reply to a PUT, REMOVE or SHUTDOWN
	 * @param msg			the message to respond to (ID, IP, port)
	 * @param responseCode	the response code to reply with
	 */
	public AppResponse(ReceivedMessage msg, byte responseCode) {
		uniqueID 			= msg.getUniqueID();
		ipToSendTo 			= msg.getSenderIP();
		portToSendTo 		= msg.getSenderPort();
		this.responseCode 	= responseCode;
		valueLength 		= 0;
		value 				= null;
		buffer = new byte[uniqueID.length + 1];
		assembleUniqueID();
		assembleResponseCode();
	}
	
	/**
	 * Constructs a new response message that is a reply to a successful GET request
	 * @param msg	the message to respond to (where we get the ID, IP and port from)
	 * @param value	the value requested in the original request
	 */
	public AppResponse(ReceivedMessage msg, byte[] value) {
		uniqueID 		= msg.getUniqueID();
		ipToSendTo 		= msg.getSenderIP();
		portToSendTo 	= msg.getSenderPort();
		responseCode 	= Protocols.CODE_SUCCESS;
		valueLength 	= value.length;
		this.value 		= value;
		buffer = new byte[uniqueID.length + 1 + 2 + valueLength];
		assembleUniqueID();
		assembleResponseCode();
		assembleValue();
	}
	
	/**
	 * Adds the unique ID to the message buffer
	 */
	private void assembleUniqueID() {
		for (int i = 0; i < uniqueID.length; i++) {
			buffer[i] = uniqueID[i];
		}
	}
	
	/**
	 * Adds the response code to the message buffer
	 */
	private void assembleResponseCode() {
		buffer[uniqueID.length] = responseCode;
	}
	
	/**
	 * Adds the value length and value to the message buffer
	 */
	private void assembleValue() {
		int index = uniqueID.length + 1;
		// Add the value length
		buffer[index++] = Helper.intToByteArray(valueLength)[0];
		buffer[index++] = Helper.intToByteArray(valueLength)[1];

		// Add the value
		for (int i = 0; i < valueLength; i++) {
			buffer[index++] = value[i];
		}		
	}
	
	/**
	 * Return the fields of this message as a string
	 */
	public String toString() {
		String string = "Unique ID: " + Helper.bytesToHexString(uniqueID) + "\n" +
						"Response Code: " + Integer.toHexString(responseCode) + "\n";
		
		// If this is a reply to a GET request, we ought to print out those values we got
		if (valueLength > 0) {
			string += "Value-Length: " + valueLength + "\n";
			string += "Value: " + Helper.bytesToHexString(value) + "\n";
		}
		return string;
	}
}
