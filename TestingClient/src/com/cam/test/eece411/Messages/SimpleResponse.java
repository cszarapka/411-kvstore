package com.cam.test.eece411.Messages;

import java.net.DatagramPacket;

public class SimpleResponse extends ReceivedMessage {
	
	public SimpleResponse(DatagramPacket packet) {
		super(packet);
	}
	
	@Override
	public String toString() {
		return super.toString();
	}
}
