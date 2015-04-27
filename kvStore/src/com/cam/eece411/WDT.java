package com.cam.eece411;

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
	private int[] missedACKs = new int[Server.broadcastAddresses.size()];

	public WDT(int port) {
		log.setLevel(Protocols.LOGGER_LEVEL);
		socket = new UDPSocket(port);
		
		// Set the timeout for receiving ACKS
		socket.setTimeout(Utils.WDT_TIMEOUT);
		
		// initialize all ack counts to 0
		for (int i = 0; i < missedACKs.length; i++) {
			missedACKs[i] = 0;
		}
	}

	public void run() {
		log.info("Watchdog Thread launched");
		while (true) {
			// Wait for a bit
			sleep(Utils.WDT_TIMEOUT);

			// Message every node an isAlive and wait for an ACK
			for (int i = 0; i < Server.broadcastAddresses.size(); i++) {
				// Send the IS-ALIVE message
				socket.send(Builder.isAlive(Server.me), Server.broadcastAddresses.get(i), Utils.MAIN_PORT);
				
				// Wait for the ACK and adjust the count accordingly, timeout was set in constructor
				if (socket.receive() == null) {
					missedACKs[i]++;
				} else {
					missedACKs[i] = 0;
				}
			}
			
			for (int i = 0; i < missedACKs.length; i++) {
				if (missedACKs[i] >= 3) {
					Node deadNode = DHT.getNodeByAddr(Server.broadcastAddresses.get(i).getHostAddress());
					if (deadNode != null) {
						socket.broadcast(Builder.isDead(deadNode), Server.broadcastAddresses, Utils.MAIN_PORT);
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
