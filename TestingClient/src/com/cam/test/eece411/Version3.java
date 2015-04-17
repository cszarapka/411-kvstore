package com.cam.test.eece411;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.cam.test.eece411.Messages.GetRequest;
import com.cam.test.eece411.Messages.GetResponse;
import com.cam.test.eece411.Messages.Message;
import com.cam.test.eece411.Messages.PutRequest;
import com.cam.test.eece411.Messages.SendMessage;
import com.cam.test.eece411.Messages.ShutdownRequest;
import com.cam.test.eece411.Messages.SimpleResponse;
import com.cam.test.eece411.Messages.StartJoinRequests;

public class Version3 {
	private static final Logger log = Logger.getLogger(Version3.class.getName());
	private static final Level i = Level.INFO;
	public static UDPSocket socket;
	public static List<InetAddress> nodes;
	public static List<String> nodeStrings;
	
	public static int serverPort = 7010;
	
	public static InetAddress serverIP;
	public static InetAddress joinerIP;


	// Array for holding all the messages we are going to send
	public static SendMessage[] messages = new SendMessage[200];

	public Version3() {}

	public static void run() throws Exception {
		
//		nodeStrings = readFrom("/Users/cam/Dev/411-kvstore/Testing/ReplicationTesting/repTestNodes.txt");
//		for (int i = 0; i < nodeStrings.size(); i++) {
//			try {
//				nodes.add(nodeStrings.get(i))
//			}
//		}
		
		socket = new UDPSocket(serverPort);

		try {
			serverIP = InetAddress.getByName("planetlab-2.research.netlab.hut.fi");
			joinerIP = InetAddress.getByName("pl2.6test.edu.cn");
		} catch (UnknownHostException e) {
			System.out.println("\nX X\nException: " + e.getMessage() + "\nX X\n\nIt ends now.");
			return;
		}
		int index = 0;

		// Arrays for holding key/value pairs
		byte[][] keys = new byte[100][32];
		byte[][] values = new byte[100][100];
		int[] valueLengths = new int[100];

		// Step 2. Send that lonely node a bunch of PUTs
		// Make 10 PUTs
		for (int i = 0; i < 10; i++) {
			// Create the Key/Value pairs
			keys[i] = Helper.generateRandomByteArray(32);
			valueLengths[i] = Helper.randInt(0, 2000);
			values[i] = Helper.generateRandomByteArray(valueLengths[i]);

			// Create the messages
			messages[index++] = new PutRequest(keys[i], values[i], valueLengths[i]);
		}

		// Step 3. Wait.

		// Step 4. Tell another node to start sending join requests
		messages[index++] = new StartJoinRequests();

		// Step 5. Wait for things to settle (hopefully they are replicating and distributing)

		// Step 6. Message each of them 10 GETs
		for (int i = 0; i < 10; i++) {
			// Create the GET requests
			messages[index++] = new GetRequest(keys[i]);
		}

		// Step 7. Shutdown the original
		messages[index++] = new ShutdownRequest();

		// Step 8. Wait.

		// Step 9 Message the lonely node GETs, should be successful for all 10
		for (int i = 0; i < 10; i++) {
			messages[index++] = new GetRequest(keys[i]);
		}

		int pindex = 0;

		// Implement the steps:

		// Step 1. Send the create message and don't wait for a response
		sendPacket(serverIP, pindex++);
		Thread.sleep(5000);

		// Step 2. Send that lonely node a bunch of PUTs
		for (int i = 0; i < 10; i++) {
			// Send the PUT request-messages and wait for a response
			sendPacket(serverIP, pindex++);
			waitForResponse();
		}

		// Step 3. Wait (10 seconds)
		Thread.sleep(10000);

		// Step 4. Tell another node to start sending join requests
		sendPacket(joinerIP, pindex++);

		// Step 5. Wait for things to settle (hopefully they are replicating and distributing)
		Thread.sleep(20000);

		// Step 6. Message each of them 10 GETs
		for (int i = 0; i < 10; i++) {
			// Send GET to the original node and wait for response
			sendPacket(serverIP, pindex);
			waitForGetResponse();
			// Send GET to the new node and wait for response
			sendPacket(joinerIP, pindex);
			waitForGetResponse();
			pindex++;
		}

		// Step 7. Shutdown the original
		sendPacket(serverIP, pindex++);
		waitForResponse();

		// Step 8. Wait.
		Thread.sleep(30000);

		// Step 9 Message the lonely node GETs, should be successful for all 10
		for (int i = 0; i < 10; i++) {
			// Send 10 GETs to the joined node and wait for responses
			sendPacket(joinerIP, pindex++);
			waitForGetResponse();
		}
	}
	
	public static List<String> readFrom(String file) {
		try {
			// Read all lines from specified file
			return Files.readAllLines(Paths.get(file), Charset.defaultCharset());
		} catch (IOException e) {
			log.log(Level.SEVERE, e.toString(), e);
		}
		return null;
	}

	private static void sendPacket(InetAddress ip, int i) throws Exception {
		socket.send(messages[i].data, ip, serverPort);
		System.out.println("- - - - - - - - - - - - - - - - - -");
		System.out.println("It sent a " + Helper.cmdToString(messages[i].command) + " to " + ip.getHostName() + ":" + serverPort);
		System.out.print(messages[i].toString());
	}

	private static void waitForResponse() throws Exception {
		System.out.println("Waiting for message");
		Message msg = socket.receive();
		System.out.println("Received response from " + msg.getReturnAddress().getHostName() + ":" + msg.getReturnPort());
		SimpleResponse rcvdMsg = new SimpleResponse(msg);
		System.out.println(rcvdMsg.toString());
	}

	private static void waitForGetResponse() throws Exception {
		System.out.println("Waiting for message");
		Message msg = socket.receive();
		System.out.println("Received response from " + msg.getReturnAddress().getHostName() + ":" + msg.getReturnPort());
		GetResponse rcvdMsg = new GetResponse(msg);
		System.out.println(rcvdMsg.toString());
	}
}
