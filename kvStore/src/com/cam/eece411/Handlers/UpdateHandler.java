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
import com.cam.eece411.Utilities.Protocols;
import com.cam.eece411.Utilities.Utils;

public class UpdateHandler implements Runnable {
	private static final Logger log = Logger.getLogger(UpdateHandler.class
			.getName());

	private Message msg;
	private UDPSocket updateSocket;
	private UDPSocket repSocket;

	public UpdateHandler(Message msg, UDPSocket repS, UDPSocket updS) {
		this.msg = msg;
		this.updateSocket = updS;
		this.repSocket = repS;
	}

	public synchronized void run() {
		log.setLevel(Protocols.LOGGER_LEVEL);
		log.info("UpdateHandler launched");

		switch (msg.getCommand()) {
		case Commands.IS_ALIVE:
			try {
				handleIS_ALIVE();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case Commands.IS_DEAD:
			handleIS_DEAD();
			break;
		}
		//updateSocket.close();
		//repSocket.close();
	}

	private void handleIS_ALIVE() throws InterruptedException {
		log.setLevel(Protocols.LOGGER_LEVEL);
		if (nodeIsInDHT()) {
			// update the timestamp
			synchronized(DHT.class){
				DHT.getNode(msg.getNodeID()).updateTimestamp();
			}
			log.info("Node " + msg.getNodeID()
					+ " is already in the local DHT.");
		} else {
			synchronized(DHT.class){
				DHT.add(new Node(msg.getNodeID(), msg.getNodeAddress()));
				DHT.getNode(msg.getNodeID()).updateTimestamp();
			}
			log.info("Node " + msg.getNodeID() + " at "
					+ msg.getNodeAddress().getHostName()
					+ " was added to the local DHT.");
			
			int prevNodeID;
			int nextNodeID;
			// get this node's neighbours
			synchronized(DHT.class){
				prevNodeID = DHT.getPrevNodeOf(Server.me).nodeID;
				nextNodeID = DHT.getNextNodeOf(Server.me).nodeID;
			}
			int newNodeID = msg.getNodeID();

			// check if the new node is a neighbour
			if (prevNodeID == newNodeID || nextNodeID == newNodeID) {
				sendAllKeysTo(newNodeID, newNodeID);
			}
		}
	}

	public void sendAllKeysTo(int nodeIDToSendTo, int nodeIDRangeToSend) throws InterruptedException {
		//sends all keys in the given nodeIDRangeToSend's range to nodeIDToSendTo
		//nodeIDRangeToSend and nodeIDRangeToSend must be in the table, or no keys will be sent
		//both params represent a nodeID
		
		// iterate through all keys
		for (ByteBuffer key : KVS.getKeys()) {
			//if this key is supposed to be on our neighbour then send it
			if(DHT.findNodeFor(key.array()).nodeID == nodeIDRangeToSend){
				repSocket.send(Builder.replicatedPut(msg), DHT.getNode(nodeIDToSendTo).addr, Utils.MAIN_PORT);
				// TODO: Do we need to wait for a response?
				Thread.sleep(100);
			}
		}
	}
	
	private void handleIS_DEAD() {
		//we have been messaged saying that some node is dead
		//need to remove that node from the table
		//also, if removing that node makes responsible for its files, we need to replicate on our watchers.
		if(DHT.getNextNodeOf(Server.me).nodeID == msg.getNodeID()) {
			//if the dead node is our next node
			Node myPreviousNode = DHT.getPrevNodeOf(Server.me);
			for (ByteBuffer key : KVS.getKeys()) {
				//for each key in the table
                
                //if this key was the responsibility of the dead node
                if(DHT.findNodeFor(key.array()).nodeID == msg.getNodeID()){
                    repSocket.send(Builder.replicatedPut(msg),myPreviousNode.addr, Utils.MAIN_PORT);
                    // then we need to put it on our previous node for replication.
                    
                }
            }
		}
		if (DHT.remove(msg.getNodeID()) != null) {
			log.setLevel(Protocols.LOGGER_LEVEL);
			// after taking care of that, remove the node from our circle.
			log.info("Node " + msg.getNodeID() + " at " + msg.getNodeAddress().getHostName() + " was removed from the local DHT.");

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
