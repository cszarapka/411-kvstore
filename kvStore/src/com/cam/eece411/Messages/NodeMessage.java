package com.cam.eece411.Messages;

import java.net.DatagramPacket;

public class NodeMessage extends ReceivedMessage {
	
	public NodeMessage(DatagramPacket packet) {
		super(packet);
	}
}
