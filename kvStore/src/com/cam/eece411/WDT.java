package com.cam.eece411;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.cam.eece411.Communication.Builder;
import com.cam.eece411.Communication.UDPSocket;
import com.cam.eece411.Structures.DHT;
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
			
			// TODO: check all our nodes for some timestamp based on is-alive messages.
			// TODO: if one of the nodes has a really old timestamp, ping him
			// TODO: if he doesn't respond, broadcast an IS-DEAD
		}
	}
	
	
}
