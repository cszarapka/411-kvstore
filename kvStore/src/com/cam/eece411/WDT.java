package com.cam.eece411;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.cam.eece411.Communication.Builder;
import com.cam.eece411.Communication.UDPSocket;
import com.cam.eece411.Structures.DHT;
import com.cam.eece411.Structures.Node;
import com.cam.eece411.Utilities.Utils;

/**
 * The Watchdog Thread - simply broadcasts an IS-ALIVE
 * message every now and then.
 * @author cam
 *
 */
public class WDT implements Runnable {
	private static final Logger log = Logger.getLogger(WDT.class.getName());
	
	private UDPSocket socket;
	
	public WDT(int port) {
		socket = new UDPSocket(port);
	}

	public void run() {
		log.info("Watchdog Thread launched");
		while (true) {
			// Wait for a bit
			try { Thread.sleep(Utils.WDT_TIMEOUT); }
			catch (InterruptedException e) { log.log(Level.SEVERE, e.toString(), e); }
			
			// Broadcast an IsAlive message
			socket.broadcast(Builder.isAlive(Server.me), DHT.broadcastList(), Utils.MAIN_PORT);
			
			
			
			
			//get current time
			long currentTimestamp = System.currentTimeMillis() / 1000L;
			//max difference allowed is 2.5*WDT_TIMEOUT / 1000 [seconds]
			int maxDiff = (Utils.WDT_TIMEOUT * 2500) / 1000;
			
			//iterate through each node
			for (Node node : DHT.nodes()) {
				//find difference between last time the node was updated and the current time
				long timestampDiff = currentTimestamp - node.timestamp;
				
				// TODO: if one of the nodes has a really old timestamp, ping him <-- needed?
				
				//any node with with a timestamp older than maxDiff is declared dead 
				if(timestampDiff > maxDiff)
					//remove node from local dht
					DHT.remove(node.nodeID);
					//broadcast an isDead message
					socket.broadcast(Builder.isDead(node), DHT.broadcastList(), Utils.MAIN_PORT);
		    }
			
		}
	}
	
	
}
