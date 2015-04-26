package com.cam.test.eece411;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.sql.Date;

import com.cam.test.eece411.Messages.CreateTableRequest;
import com.cam.test.eece411.Messages.GetRequest;
import com.cam.test.eece411.Messages.PutRequest;
import com.cam.test.eece411.Messages.GetResponse;
import com.cam.test.eece411.Messages.RemoveRequest;
import com.cam.test.eece411.Messages.SendMessage;
import com.cam.test.eece411.Messages.ShutdownRequest;
import com.cam.test.eece411.Messages.SimpleResponse;
import com.cam.test.eece411.Messages.StartJoinRequests;


public class TestingClient {

	public static final byte PUT		= 1;
	public static final byte GET		= 2;
	public static final byte REMOVE		= 3;
	public static final byte SHUTDOWN	= 4;
	public static final byte CREATE_DHT = 20;
	public static final byte START_JOIN_REQUESTS = 21;


	public static void main(String[] args) {
		System.out.println("The client awakens.");
		//new Thread(new Version1()).start();
		try {
			Version2.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

