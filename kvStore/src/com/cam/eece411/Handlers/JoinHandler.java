package com.cam.eece411.Handlers;

import java.util.logging.Logger;

import com.cam.eece411.Server;
import com.cam.eece411.Communication.Builder;
import com.cam.eece411.Communication.Message;
import com.cam.eece411.Communication.UDPSocket;
import com.cam.eece411.Structures.DHT;
import com.cam.eece411.Utilities.Protocols;
import com.cam.eece411.Utilities.Utils;

public class JoinHandler implements Runnable {
	private static final Logger log = Logger.getLogger(JoinHandler.class.getName());
	private Message msg;
	private UDPSocket socket;

	public JoinHandler(Message msg) {
		this.msg = msg;
	}

	public synchronized void run() {
		log.setLevel(Protocols.LOGGER_LEVEL);
		log.info("JoinHandler launched");
		socket = new UDPSocket(Utils.JOIN_PORT);
		respondToJOIN_REQUEST();
		socket.close();
	}

	private void respondToJOIN_REQUEST() {
		log.setLevel(Protocols.LOGGER_LEVEL);
		int myID = Server.me.nodeID;
		int nextNodeID = DHT.getNextNodeOf(Server.me).nodeID;
		int maxNodeCount = Utils.MAX_NUMBER_OF_NODES;
		int offeredNodeID;

		// Determine the ID to offer the node - the number halfway between us
		// and our next node
		if(myID == nextNodeID) {
			offeredNodeID = (myID - (maxNodeCount / 2) + maxNodeCount) % maxNodeCount;
		} else {
			offeredNodeID = (myID - (((myID - nextNodeID + maxNodeCount) % maxNodeCount) / 2) + maxNodeCount) % maxNodeCount;
		}
		
		// Respond to the requesting node
		socket.send(Builder.joinResponse(msg, offeredNodeID), msg.getReturnAddress(), msg.getReturnPort());
		log.info("JOIN-RESPONSE sent to " + msg.getReturnAddress().getHostName() + ":" + msg.getReturnPort() + " offering node ID: " + offeredNodeID);
		
	}
}
