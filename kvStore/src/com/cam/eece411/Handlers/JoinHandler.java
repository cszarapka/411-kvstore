package com.cam.eece411.Handlers;

import java.util.logging.Logger;

import com.cam.eece411.Server;
import com.cam.eece411.Communication.Builder;
import com.cam.eece411.Communication.Message;
import com.cam.eece411.Communication.UDPSocket;
import com.cam.eece411.Structures.DHT;
import com.cam.eece411.Structures.Node;
import com.cam.eece411.Utilities.Protocols;
import com.cam.eece411.Utilities.Utils;

public class JoinHandler implements Runnable {
	private static final Logger log = Logger.getLogger(JoinHandler.class
			.getName());
	private Message msg;
	private UDPSocket socket;

	public JoinHandler(Message msg, UDPSocket s) {
		this.msg = msg;
		this.socket = s;
	}

	public synchronized void run() {
		log.setLevel(Protocols.LOGGER_LEVEL);
		log.info("JoinHandler launched");
		respondToJOIN_REQUEST();
		// socket.close();
	}

	private void respondToJOIN_REQUEST() {
		synchronized (DHT.class) {
			log.setLevel(Protocols.LOGGER_LEVEL);
			int myID = Server.me.nodeID;
			int nextNodeID = DHT.getNextNodeOf(Server.me).nodeID;
			int maxNodeCount = Utils.MAX_NUMBER_OF_NODES;
			int offeredNodeID;

			// Determine the ID to offer the node - the number halfway between
			// us
			// and our next node
			if (myID == nextNodeID) {
				offeredNodeID = (myID - (maxNodeCount / 2) + maxNodeCount)
						% maxNodeCount;
			} else if (nextNodeID == myID - 1
					|| (myID == 0 && nextNodeID == 255)) {
				return;
			} else {
				offeredNodeID = (myID
						- (((myID - nextNodeID + maxNodeCount) % maxNodeCount) / 2) + maxNodeCount)
						% maxNodeCount;
			}

			// Respond to the requesting node
			socket.send(Builder.joinResponse(msg, offeredNodeID),
					msg.getReturnAddress(), msg.getReturnPort());
			log.info("JOIN-RESPONSE sent to "
					+ msg.getReturnAddress().getHostName() + ":"
					+ msg.getReturnPort() + " offering node ID: "
					+ offeredNodeID);

			DHT.add(new Node(offeredNodeID, msg.getReturnAddress()));
			DHT.getNode(offeredNodeID).updateTimestamp();

			log.info("Node " + offeredNodeID + " at "
					+ msg.getReturnAddress().getHostName()
					+ " was added to the local DHT.");
			/*
			 * int prevNdeID; int nextNdeID; // get this node's neighbours
			 * synchronized(DHT.class){ prevNdeID =
			 * DHT.getPrevNodeOf(Server.me).nodeID; nextNdeID =
			 * DHT.getNextNodeOf(Server.me).nodeID; }
			 * 
			 * // check if the new node is a neighbour if (prevNdeID ==
			 * offeredNodeID || nextNdeID == offeredNodeID) {
			 * //UpdateHandler.sendAllKeysTo(offeredNodeID, offeredNodeID); }
			 */
		}

	}
}
