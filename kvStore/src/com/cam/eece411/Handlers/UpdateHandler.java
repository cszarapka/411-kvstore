package com.cam.eece411.Handlers;

import java.nio.ByteBuffer;
import java.util.logging.Logger;

import com.cam.eece411.Server;
import com.cam.eece411.Communication.Builder;
import com.cam.eece411.Communication.Message;
import com.cam.eece411.Communication.UDPSocket;
import com.cam.eece411.Structures.DHT;
import com.cam.eece411.Structures.KVS;
import com.cam.eece411.Structures.Node;
import com.cam.eece411.Utilities.Commands;
import com.cam.eece411.Utilities.Utils;

public class UpdateHandler implements Runnable {
	private static final Logger log = Logger.getLogger(UpdateHandler.class
			.getName());

	private Message msg;

	// private UDPSocket socket;

	public UpdateHandler(Message msg) {
		this.msg = msg;
		// socket = new UDPSocket(Utils.UPDATE_PORT);
	}

	public void run() {
		log.info("UpdateHandler launched");
		switch (msg.getCommand()) {
		case Commands.IS_ALIVE:
			handleIS_ALIVE();
			break;
		case Commands.IS_DEAD:
			handleIS_DEAD();
			break;
		}
	}

	private void handleIS_ALIVE() {
		if (nodeIsInDHT()) {
			// update the timestamp
			DHT.getNode(msg.getNodeID()).updateTimestamp();
			log.info("Node " + msg.getNodeID()
					+ " is already in the local DHT.");
		} else {
			DHT.add(new Node(msg.getNodeID(), msg.getNodeAddress()));
			log.info("Node " + msg.getNodeID() + " at "
					+ msg.getNodeAddress().getHostName()
					+ " was added to the local DHT.");

			// get this node's neighbours
			int prevNodeID = DHT.getPrevNodeOf(Server.me).nodeID;
			int nextNodeID = DHT.getNextNodeOf(Server.me).nodeID;
			int newNodeID = msg.getNodeID();

			// check if the new node is a neighbour
			if (prevNodeID == newNodeID || nextNodeID == newNodeID) {
				// iterate through all keys
				for (ByteBuffer key : KVS.getKeys()) {
					
					//if this key is supposed to be on our neighbour then send it
					if(DHT.findNodeFor(key.array()).nodeID == newNodeID){
						UDPSocket socket = new UDPSocket(Utils.REP_PORT);
						socket.send(Builder.replicatedPut(msg), DHT.getNode(newNodeID).addr, Utils.MAIN_PORT);
						// TODO: Do we need to wait for a response?
						
					}
				}
			}
		}
	}

	private void handleIS_DEAD() {
		if (DHT.remove(msg.getNodeID()) != null) {
			log.info("Node " + msg.getNodeID() + " at "
					+ msg.getNodeAddress().getHostName()
					+ " was removed from the local DHT.");
		}
	}

	private boolean nodeIsInDHT() {
		// Get the node belonging to the supplied node ID
		Node node = DHT.getNode(msg.getNodeID());

		if (node != null) {
			// Ensure the addresses match
			if (node.addr.equals(msg.getNodeAddress())) {
				return true;
			}
		}

		return false;
	}
}
