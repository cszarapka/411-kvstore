package com.cam.eece411;

import java.net.DatagramPacket;

public class Message {
	protected byte[] data;
	
	public Message(DatagramPacket packet) {
		this.data = packet.getData();
	}
	
	public byte[] getData() {
		return this.data;
	}
}
