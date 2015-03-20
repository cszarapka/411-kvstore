package com.cam.eece411;

import java.net.DatagramPacket;

import com.cam.eece411.Messages.ReceivedMessage;

public class NodeMessage extends ReceivedMessage {
	
	public NodeMessage(DatagramPacket packet) {
		super(packet);
	}
}
