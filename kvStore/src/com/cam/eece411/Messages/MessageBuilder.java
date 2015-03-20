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
	
	/**
	 * Returns a byte[] buffer that is a ready-to-send response to a join
	 * request-message
	 * @return
	 */
	public static byte[] responseToJoinRequest() {
		byte[] buffer;
		
		return buffer;
	}
}
