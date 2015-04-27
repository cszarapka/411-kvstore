package com.cam.eece411;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.cam.eece411.Communication.Builder;
import com.cam.eece411.Communication.UDPSocket;
import com.cam.eece411.Structures.DHT;
import com.cam.eece411.Structures.Node;
import com.cam.eece411.Utilities.Protocols;
import com.cam.eece411.Utilities.Utils;

/**
 * The Watchdog Thread - simply broadcasts an IS-ALIVE message every now and
 * then.
 * 
 * @author cam
 *
 */
public class WDT implements Runnable {
	private static final Logger log = Logger.getLogger(WDT.class.getName());

	private UDPSocket socket;

	public WDT(int port) {
		log.setLevel(Protocols.LOGGER_LEVEL);
		socket = new UDPSocket(port);
	}

	public void run() {
		log.info("Watchdog Thread launched");
		while (true) {
			// Wait for a bit
			sleep(Utils.WDT_TIMEOUT);

			// Broadcast an IsAlive message
			socket.broadcast(Builder.isAlive(Server.me), Server.broadcastList,
			 Utils.MAIN_PORT);
			synchronized(DHT.class){
				//socket.send(Builder.isAlive(Server.me),
				//		DHT.getNextNodeOf(Server.me).addr, Utils.MAIN_PORT);
				//socket.send(Builder.isAlive(Server.me),
				//		DHT.getPrevNodeOf(Server.me).addr, Utils.MAIN_PORT);
				log.info("SENDING IS-ALIVE TO: "+ DHT.getNextNodeOf(Server.me).addr);
				log.info("NODE-ID: "+ DHT.getNextNodeOf(Server.me).nodeID);
				log.info("SENDING IS-ALIVE TO: "+ DHT.getPrevNodeOf(Server.me).addr);
				log.info("NODE-ID: "+ DHT.getPrevNodeOf(Server.me).nodeID);
			}
			
			

			// get current time
			long currentTimestamp = System.currentTimeMillis() / 1000L;

			// max difference allowed is 2.5*WDT_TIMEOUT / 1000 / 1000 [seconds]
			int maxDiff = ((Utils.WDT_TIMEOUT * 5000) / 1000) / 1000;

			int numNodes;
			int[] nodeNum;
			long[] nodeTimestamp;
			int prevNode;
			int nextNode;

			synchronized (DHT.class) {
				numNodes = DHT.getSize();
				nodeNum = new int[numNodes];
				nodeTimestamp = new long[numNodes];
				prevNode = DHT.getPrevNodeOf(Server.me).nodeID;
				nextNode = DHT.getNextNodeOf(Server.me).nodeID;
				int i = 0;
				for (Node node : DHT.nodes()) {
					nodeNum[i] = node.nodeID;
					 if(node.nodeID != prevNode
					 && node.nodeID != nextNode){
					// if not our responsibility, update timestamp

					node.timestamp = currentTimestamp;

					 }

					nodeTimestamp[i] = node.timestamp;
					i++;
				}
			}
			
			//prints table size
			log.info("||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\n NODE TABLE SIZE: " + nodeNum+"\n||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\n");

			// iterate through each node
			for (int i = 0; i < numNodes; i++) {
				// find difference between last time the node was updated and
				// the current time
				long timestampDiff = currentTimestamp - nodeTimestamp[i];

				// TODO: if one of the nodes has a really old timestamp, ping
				// him <-- needed?
				
				// any node with with a timestamp older than maxDiff is declared
				// dead
				if (timestampDiff > maxDiff
						&& (nodeNum[i] == prevNode || nodeNum[i] == nextNode)) {
					log.info("TIMESTAMP DIFF:" + timestampDiff);
					log.info("MAX DIFF: " + maxDiff);
					log.info("Watchdog thread: broadcasting to all that node "
							+ nodeNum[i] + "is dead.");
					log.info("Current stamp: " + currentTimestamp
							+ "; node stamp: " + nodeTimestamp[i]);
					// remove node from local dht
					/*
					 * synchronized(DHT.class){ DHT.remove(node.nodeID); }
					 */
					// broadcast an isDead message
					synchronized (DHT.class) {
						socket.broadcast(
								
								Builder.isDead(DHT.getNode(nodeNum[i])),
								DHT.broadcastList(), Utils.MAIN_PORT);
						byte[] shutdownMessage = { 0, 0, 0, 0, 0, 0, 0, 0, 0,
								0, 0, 0, 0, 0, 0, 0, 4 };
						socket.send(shutdownMessage,
								DHT.getNode(nodeNum[i]).addr, Utils.MAIN_PORT);
					}
				}
			}

		}
	}
	
	private void sleep(int msec) {
		try {
			Thread.sleep(msec);
		} catch (InterruptedException e) {
			log.log(Level.SEVERE, e.toString(), e);
		}
	}
}
