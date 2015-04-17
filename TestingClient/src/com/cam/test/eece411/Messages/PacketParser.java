package com.cam.test.eece411.Messages;

import java.net.DatagramPacket;
import java.util.Arrays;

public class PacketParser {

	public PacketParser() {}
	
	public static void printMessage() {
		String output = "";
	}
	
	public static byte[] getUniqueID(DatagramPacket packet) {
		return Arrays.copyOfRange(packet.getData(), 0, 16);
	}
	
	public static byte getCommand(DatagramPacket packet) {
		return packet.getData()[16];
	}
	
	
}
