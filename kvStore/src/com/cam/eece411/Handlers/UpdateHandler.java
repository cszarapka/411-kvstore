package com.cam.eece411.Handlers;

import java.nio.ByteBuffer;
import java.util.logging.Level;
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
	private static final Logger log = Logger.getLogger(UpdateHandler.class.getName());

	private Message msg;
	private UDPSocket updateSocket;
	private UDPSocket repSocket;

	public UpdateHandler(Message msg, UDPSocket repS, UDPSocket updS) {
		log.setLevel(Protocols.LOGGER_LEVEL);
		this.msg = msg;
		this.updateSocket = updS;
		this.repSocket = repS;
	}

	public synchronized void run() {
		log.info("UpdateHandler launched");

		switch (msg.getCommand()) {
			case Commands.IS_ALIVE: handleIS_ALIVE(); break;
			case Commands.IS_DEAD: handleIS_DEAD(); break;
		}
		
		updateSocket.close();
		repSocket.close();
	}

	private void handleIS_ALIVE() {
		if (nodeIsInDHT()) {
			// Only update the timestamp in this case
			synchronized (DHT.class){
				DHT.getNode(msg.getNodeID()).updateTimestamp();
			}
			log.info("Node " + msg.getNodeID() + " is already in the local DHT - timestamp updated.");
		} else {
			Node newNode = new Node(msg.getNodeID(), msg.getNodeAddress());
			synchronized (DHT.class) {
				// Add the new node to our table and update its timestamp
				DHT.add(newNode);
				DHT.getNode(msg.getNodeID()).updateTimestamp();
				log.info("Node " + msg.getNodeID() + " at " + msg.getNodeAddress().getHostName() + " was added to the local DHT.");
				
				// Update our neighbors (because they may have changed with the addition)
				Server.me.nextID = DHT.getNextNodeOf(newNode).id;
				Server.me.prevID = DHT.getPrevNodeOf(newNode).id;
				
				// If the new node is a neighbor
				if (Server.me.isNeighbor(newNode)) {
					// If the new node is CCW of us
					if (newNode.id == DHT.getNextNodeOf(Server.me).id) {
						// Send him keys from our KVStore that he will now be responsible for
						unloadKeysTo(newNode);
					}
					// Replicate our own keys to him as well
					repKeysTo(newNode);
				}
			}
		}
	}

	/**
	 * PUT's and REMOVE's all keys that we have that we no
	 * longer are responsible for.
	 * @param node	the node to dump our load onto 
	 */
	public void unloadKeysTo(Node node) {
		int count = 0;
		// Iterate through all our keys
		for (ByteBuffer key : KVS.getKeys()) {
			// If the new node is responsible for this key
			if(DHT.findNodeFor(key.array()) == node){
				// Send the node a PUT with the key
				updateSocket.send(Builder.put(key, node), node.addr, Utils.MAIN_PORT);
				count++;
				
				// Sleep for a tiny amount
				try { Thread.sleep(100); }
				catch (InterruptedException e) { log.log(Level.SEVERE, e.toString(), e); }
			}
		}
		log.info(count + " keys were PUT to new node " + node.id + "@" + node.addr.getHostName());
	}
	
	public void repKeysTo(Node node) {
		repSocket.send(Builder.replicatedPut(msg), DHT.getNode(nodeIDToSendTo).addr, Utils.MAIN_PORT);
	}

	private void handleIS_DEAD() {
		//we have been messaged saying that some node is dead
		//need to remove that node from the table
		//also, if removing that node makes responsible for its files, we need to replicate on our watchers.
		if(DHT.getNextNodeOf(Server.me).id == msg.getNodeID()) {
			//if the dead node is our next node
			Node myPreviousNode = DHT.getPrevNodeOf(Server.me);
			for (ByteBuffer key : KVS.getKeys()) {
				//for each key in the table

				//if this key was the responsibility of the dead node
				if(DHT.findNodeFor(key.array()).id == msg.getNodeID()){
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
