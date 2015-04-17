package com.cam.test.eece411.Messages;

import java.net.DatagramPacket;
import java.util.Arrays;

import com.cam.test.eece411.Helper;

public class ReceivedMessage {
	public byte[] uniqueID;
	public byte responseCode;
	public byte[] data;
	
	public ReceivedMessage(Message msg) {
		data = msg.getData();
		uniqueID = Arrays.copyOfRange(data, 0, 16);
		responseCode = data[16];
	}
	
	@Override
	public String toString() {
		return 	"Unique ID: " + Helper.bytesToHexString(uniqueID) + "\n" +
				"Response Code: " + Integer.toHexString(responseCode) + "\n";
	}
}
