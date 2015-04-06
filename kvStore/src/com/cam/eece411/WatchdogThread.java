package com.cam.eece411;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import com.cam.eece411.Messages.MessageBuilder;
import com.cam.eece411.Messages.PutRequestReplication;
import com.cam.eece411.Utilities.MD5HashFunction;
import com.cam.eece411.Utilities.Protocols;

public class WatchdogThread implements Runnable {

	public void run() {
		for(;;) {
			if(Server.state == Protocols.IN_TABLE) {
				Set<Entry<Integer,Node>>nodelist;
				synchronized(Circle.class) {
					nodelist = Circle.getNodes();
				}
				Set<Node> dead = new HashSet<Node>();
				Set<Node> alive = new HashSet<Node>();
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
						alive.add(node);
					} catch (SocketException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();

					} catch (SocketTimeoutException e) {
						// Messaged server failed to respond. 
						// Treat it as dead
						dead.add(node);

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}



				}
				//now we have a set of node numbers "alive" and a set "dead"
				Iterator<Node> deadIter = dead.iterator();
				while(deadIter.hasNext()) {
					Node d = deadIter.next();
					Iterator<Node> aliveIter = alive.iterator();
					while(aliveIter.hasNext()) {
						Node a = aliveIter.next();
						message = MessageBuilder.isDead(d);
						Server.sendMessage(message, a.ip, Protocols.LISTENING_PORT);
					}
				}

				/**
				 * file replication
				 * 		algorithm:	
				 * 			- get the set of remote nodes that this local node covers [1 successor, 1 predecessor]
				 * 			- send puts for all keys the local node covers to the 2 remote nodes
				 *		NOTE: this may be slow as balls if there are a large amount of keys on each covered node.
				 *				might be better to put into it's own thread.
				 */

				//get local node number
				int myNodeNum = Server.me.nodeNumber;

				//vector containing successor and predecessor nodes
				Vector<Node> replicators = new Vector<Node>();

				synchronized(Circle.class) {
					//add next node to vector
					replicators.add(Circle.getNextNodeOf(Server.me));

					//add prev node to vector if not the same as the first
					if( Circle.getNextNodeOf(Server.me).nodeNumber != Circle.getPrevNodeOf(Server.me).nodeNumber)
						replicators.add(Circle.getPrevNodeOf(Server.me));
				}
				//send all the keys covered by myNodeNum to the successor and predecessors in replicators
				sendAllKeys(replicators, myNodeNum);
			} else {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Used for data replication between nodes. <br><br>
	 * Sends a PUT message for each key covered by the local node to the successor and predecessor of the local node 
	 * @param replicators Vector<Node> containing the successor and predecessor nodes
	 * @param myNodeNum The local node's node number
	 */
	public void sendAllKeys(Vector<Node> replicators, int myNodeNum){
		//send puts of all covered keys to each node in the replicators vector
		Iterator<ByteBuffer> keys = KeyValueStore.getKeys().iterator();
		byte[] currentKey = new byte[32];

		// Go through all of the keys
		while (keys.hasNext()) {
			// Unwrap the array from the byte buffer
			currentKey = keys.next().array();

			// Hash the key
			int hash = MD5HashFunction.hash(currentKey);

			for(int i = 0; i < replicators.size(); i++){
				// Only PUT this key/value pair to the new node if the local node covers it
				if (myNodeNum > replicators.get(i).nodeNumber) {
					if (myNodeNum >= hash && hash >= replicators.get(i).nodeNumber) {
						Server.sendMessage((new PutRequestReplication(currentKey, KeyValueStore.get(currentKey))).data, replicators.get(i).ip, Protocols.LISTENING_PORT);
						KeyValueStore.remove(currentKey);
					}
				} else {
					if (myNodeNum >= hash || hash >= replicators.get(i).nodeNumber) {
						Server.sendMessage((new PutRequestReplication(currentKey, KeyValueStore.get(currentKey))).data, replicators.get(i).ip, Protocols.LISTENING_PORT);
						KeyValueStore.remove(currentKey);
					}
				}
			}

		}
	}

}
