package com.cam.test.eece411.Messages;

import java.net.DatagramPacket;
import java.util.Arrays;

import com.cam.test.eece411.Helper;

public class GetResponse extends ReceivedMessage {
	public int valueLength;
	public byte[] value;
	
	public GetResponse(Message msg) {
		super(msg);
		valueLength = Helper.valueLengthBytesToInt(Arrays.copyOfRange(data, 17, 19));
		value = Arrays.copyOfRange(data, 19, 19+valueLength);
	}
	
	@Override
	public String toString() {
		return 	super.toString() +
				"Value-Length: " + valueLength + "\n"; 
				//"Value: " + Helper.bytesToHexString(value) + "\n";
	}
}
