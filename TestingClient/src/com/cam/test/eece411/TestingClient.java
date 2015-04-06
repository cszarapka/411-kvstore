package com.cam.test.eece411;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

import com.cam.test.eece411.Messages.CreateTableRequest;
import com.cam.test.eece411.Messages.GetRequest;
import com.cam.test.eece411.Messages.PutRequest;
import com.cam.test.eece411.Messages.GetResponse;
import com.cam.test.eece411.Messages.RemoveRequest;
import com.cam.test.eece411.Messages.SendMessage;
import com.cam.test.eece411.Messages.ShutdownRequest;
import com.cam.test.eece411.Messages.SimpleResponse;

public class TestingClient {

	public static final byte PUT		= 1;
	public static final byte GET		= 2;
	public static final byte REMOVE		= 3;
	public static final byte SHUTDOWN	= 4;
	public static final byte CREATE_DHT = 20;
	public static final byte START_JOIN_REQUESTS = 21;


	public static void main(String[] args) {
		System.out.println("\n\n\n\n\n");
		System.out.println("The client awakens.");

		DatagramSocket socket;
		DatagramPacket sendPacket;
		DatagramPacket receivePacket;
		InetAddress serverIP;
		try {
			serverIP = InetAddress.getByName("pl1.pku.edu.cn");
		} catch (UnknownHostException e) {
			System.out.println("\nX X\nException: " + e.getMessage() + "\nX X\n\nIt ends now.");
			return;
		}
		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			System.out.println("\nX X\nException: " + e.getMessage() + "\nX X\n\nIt ends now.");
			return;
		}
		int serverPort = 5000;

		// Build some test data
		byte[] key = Helper.generateRandomByteArray(32);
		byte[] key1 = Helper.generateRandomByteArray(32);
		byte[] key2 = Helper.generateRandomByteArray(32);
		byte[] key3 = Helper.generateRandomByteArray(32);
		byte[] value = Helper.generateRandomByteArray(50);
		byte[][] keys = new byte[98][32];
		byte[][] values = new byte[98][50];

		boolean[] shouldWait = new boolean[199];
		SendMessage[] messages = new SendMessage[199];
		messages[0] = new CreateTableRequest();
		shouldWait[0] = false;
		messages[1] = new GetRequest(key);			// should get KEY-DNE back
		shouldWait[1] = true;
		messages[2] = new RemoveRequest(key);		// should get KEY-DNE back
		shouldWait[2] = true;
		for(int i = 3; i < 101; i++) {
			values[i-3] = Helper.generateRandomByteArray(50);
			keys[i-3] = Helper.generateRandomByteArray(32);
			messages[i] = new PutRequest(keys[i - 3],values[i-3]);
			shouldWait[i] = true;
		}
		for(int i = 101;i<199;i++) {
			messages[i] = new GetRequest(keys[i - 101]);
			shouldWait[i] = true;
		}
		/*
		messages[3] = new PutRequest(key, value);	// should get SUCCESS back
		shouldWait[3] = true;
		messages[4] = new PutRequest(key1,value);					// should get SUCCESS back
		shouldWait[4] = true;
		messages[5] = new PutRequest(key2,value);;					// should get KEY-DNE back
		shouldWait[5] = true;
		messages[6] = new PutRequest(key3,value);;		// should get SUCCESS back
		shouldWait[6] = true;
		messages[7] = new GetRequest(key);					// should get nothing back
		shouldWait[7] = true;
		messages[8] = new GetRequest(key1);
		shouldWait[8] = true;
		messages[9] = new GetRequest(key2);
		shouldWait[9] = true;
		messages[10] = new GetRequest(key3);
		shouldWait[10] = true;*/
		System.out.println("It has created the messages.");

		// Send and receive messages
		for (int i = 0; i < messages.length; i++) {
			sendPacket = new DatagramPacket(messages[i].data, messages[i].data.length, serverIP, serverPort);

			try {
				socket.send(sendPacket);
				System.out.println("- - - - - - - - - - - - - - - - - -");
				System.out.println("It sent message " + i + ":");
				System.out.print(messages[i].toString());
				byte[] rcvData = new byte[15500];
				receivePacket = new DatagramPacket(rcvData, rcvData.length);
				if(shouldWait[i]) { 
					socket.receive(receivePacket);
					System.out.println("- - Response:");
					if (messages[i].command == GET && receivePacket.getData()[16] == 0 && i > 50) {
						boolean correct = true;
						for(int j = 0; j < 50; j++) {
							if(receivePacket.getData()[j+19] != values[i - 101][j]) {
								System.out.println("\npacket " + receivePacket.getData()[j+17+32] + " \nand value " + values[i - 101][j]);
								correct = false;
							}
							
						}
						System.out.println("\n FILE CONTENTS ARE: " + correct);
						//System.out.print((new GetResponse(receivePacket)).toString());
					} else {
						//System.out.print((new SimpleResponse(receivePacket)).toString());
					}
				}
				System.out.println("- - - - - - - - - - - - - - - - - -");
			} catch (IOException e) {
				System.out.println("\nX X\nMessage " + i + " -Exception: " + e.getMessage() + "\nX X\n\n");
			}
			if(i == 0) {
				try {
					Thread.sleep(20000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		System.out.println("It finished sending and receiving test messages");
		socket.close();
		System.out.println("It ends.. for now");
		return;
	}
}

