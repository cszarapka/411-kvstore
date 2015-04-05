package com.cam.test.eece411;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

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
		byte[] value = Helper.generateRandomByteArray(50);

		boolean[] shouldWait = new boolean[10];
		SendMessage[] messages = new SendMessage[10];
		messages[0] = new CreateTableRequest();
		shouldWait[0] = false;
		messages[1] = new GetRequest(key);			// should get KEY-DNE back
		shouldWait[0] = true;
		messages[2] = new RemoveRequest(key);		// should get KEY-DNE back
		shouldWait[0] = true;
		messages[3] = new PutRequest(key, value);	// should get SUCCESS back
		shouldWait[0] = true;
		messages[4] = messages[1];					// should get SUCCESS back
		shouldWait[0] = true;
		messages[5] = messages[1];					// should get KEY-DNE back
		shouldWait[0] = true;
		messages[6] = new ShutdownRequest();		// should get SUCCESS back
		shouldWait[0] = false;
		messages[7] = messages[0];					// should get nothing back
		shouldWait[0] = true;
		messages[8] = messages[0];
		shouldWait[0] = true;
		messages[9] = messages[0];
		shouldWait[0] = true;
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
				if(!shouldWait[i]) { 
					socket.receive(receivePacket);
					System.out.println("- - Response:");
					if (messages[i].command == GET && receivePacket.getData()[16] == 0) {
						System.out.print((new GetResponse(receivePacket)).toString());
					} else {
						System.out.print((new SimpleResponse(receivePacket)).toString());
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

