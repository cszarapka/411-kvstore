package com.cam.eece411;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import com.cam.eece411.Messages.MessageBuilder;
import com.cam.eece411.Utilities.Protocols;

public class WatchdogThread implements Runnable {

	public void run() {
		for(;;) {
			Set<Entry<Integer,Node>>nodelist;
			synchronized(Circle.class) {
				nodelist = Circle.getNodes();
			}
			Set<Integer> dead = new HashSet();
			Set<Integer> alive = new HashSet();
			Node node;
			byte[] message;
			byte[] receivedPacket = new byte[Protocols.MAX_MSG_SIZE];
			DatagramSocket socket = null;
			Iterator<Entry<Integer,Node>> nodeIterator = nodelist.iterator();
			while(nodeIterator.hasNext()) {
				node = nodeIterator.next().getValue();
				message = MessageBuilder.isAlive();
				Server.sendMessage(message, node.ip, Protocols.LISTENING_PORT);

					try {
						socket = new DatagramSocket(Protocols.IS_ALIVE_RESPONSE_PORT);
						socket.setSoTimeout(Protocols.IS_ALIVE_TIMEOUT);
						DatagramPacket packet = new DatagramPacket(receivedPacket, receivedPacket.length);
						socket.receive(packet);
						alive.add(node.nodeNumber);
					} catch (SocketException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();

					} catch (SocketTimeoutException e) {
						// Messaged server failed to respond. 
						// Treat it as dead
						dead.add(node.nodeNumber);
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				
				
			}
			//now we have a set of node numbers "alive" and a set "dead"
			for(int i = 0; i < dead.size(); i++) {
				
			}
			for(int i = 0; i < alive.size(); i++) {
				
			}
			
			
			
		}
	}

}
