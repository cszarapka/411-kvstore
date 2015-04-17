package com.cam.test.eece411;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.cam.test.eece411.Messages.Message;

/**
 * To make life easier and cleaner.
 * @author cam
 *
 */
public class UDPSocket {
	private static final Logger log = Logger.getLogger(UDPSocket.class.getName());

	private static final int MAX_SIZE = 16000;
	private DatagramSocket socket;
	
	/**
	 * Construct a new UDP Client bound to the supplied port
	 * @param port
	 */
	public UDPSocket(int port) {
		setup(port);
	}
	
	/**
	 * Create a new datagram socket bound to the given port
	 * @param port	the port to bind to
	 */
	private void setup(int port) {
		try { socket = new DatagramSocket(port); }
		catch (SocketException e) { log.log(Level.SEVERE, e.toString(), e); }
	}
	
	/**
	 * Sends a byte array of data to the specified port at the specified host
	 * @param sendData	data to send
	 * @param destAddr	host to send to
	 * @param destPort	port to send to
	 */
	public void send(byte[] sendData, InetAddress destAddr, int destPort) {
		DatagramPacket packet = new DatagramPacket(sendData, sendData.length, destAddr, destPort);
		try { socket.send(packet); }
		catch (IOException e) { log.log(Level.SEVERE, e.toString(), e); }
	}
	
	/**
	 * Sends a byte array of data to all hosts at the specified port
	 * @param sendData		data to send
	 * @param destAddrs		hosts to send to
	 * @param destPort		port to send to
	 */
	public void broadcast(byte[] sendData, List<InetAddress> destAddrs, int destPort) {
		for (int i = 0; i < destAddrs.size(); i++) {
			DatagramPacket packet = new DatagramPacket(sendData, sendData.length, destAddrs.get(i), destPort);
			try { socket.send(packet); }
			catch (IOException e) { log.log(Level.SEVERE, e.toString(), e); }
		}
	}
	
	/**
	 * Listen and receive a packet on your port, returning the byte array data received
	 * @return	the data received
	 */
	public Message receive() throws SocketTimeoutException {
		byte[] data = new byte[MAX_SIZE];
		DatagramPacket packet = new DatagramPacket(data, data.length);
		
		// Wait for a packet
		try { socket.receive(packet); }
		catch (IOException e) { log.log(Level.SEVERE, e.toString(), e); }
		
		return new Message(packet);
	}
	
	/**
	 * Set the socket timeout
	 * @param msec	value to set the socket timeout to (milliseconds)
	 */
	public void setTimeout(int msec) {
		try { socket.setSoTimeout(msec); }
		catch (SocketException e) { log.log(Level.SEVERE, e.toString(), e); }
	}
	
	/**
	 * Close the socket
	 */
	public void close() {
		socket.close();
	}
}
