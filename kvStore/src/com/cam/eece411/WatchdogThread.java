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
import com.cam.eece411.Messages.PutRequest;
import com.cam.eece411.Messages.PutRequestReplication;
import com.cam.eece411.Utilities.MD5HashFunction;
import com.cam.eece411.Utilities.Protocols;

public class WatchdogThread implements Runnable {

	public void run() {
		for(;;) {
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
			* 			- get the set of remote nodes that this local node covers
			* 			- copy all the keys that the remote node covers to the local node
			*		NOTE: this may be slow as balls if there are a large amount of keys on each covered node.
			*				might be better to put into it's own thread.
			*/
			
			//get local node number
			int myNodeNum = Server.me.nodeNumber;
			
			//holds neighbour nodes
			Node successorNode = new Node(-1, null);
			Node predecessorNode = new Node(-1, null);;
			Iterator<Node> aliveIter = alive.iterator();
			
			//check each node in the iterator and add accordingly
			while(aliveIter.hasNext()){
				Node nextNode = aliveIter.next();
				
				if(nextNode.nodeNumber > myNodeNum){
					if(successorNode.nodeNumber == -1){
						successorNode = nextNode;
					} else if(nextNode.nodeNumber < successorNode.nodeNumber){
						successorNode = nextNode;
					}
				} else if(nextNode.nodeNumber < myNodeNum){
					if(predecessorNode.nodeNumber == -1){
						predecessorNode = nextNode;
					} else if(nextNode.nodeNumber > predecessorNode.nodeNumber){
						predecessorNode = nextNode;
					}
				}
			}
			
			// check that a successor and predecessor have been found, if not, wrap around circle
			if(successorNode.nodeNumber == -1){ //successor needs to wrap
				aliveIter = alive.iterator(); //restart iterator
				while(aliveIter.hasNext()){
					Node nextNode = aliveIter.next();
					
					if(successorNode.nodeNumber == -1){
						successorNode = nextNode;
					} else if(nextNode.nodeNumber < successorNode.nodeNumber){
						successorNode = nextNode;
					}
				}
			}
			if(predecessorNode.nodeNumber == -1){ //predecessor needs to wrap
				aliveIter = alive.iterator(); //restart iterator	
				while(aliveIter.hasNext()){
					Node nextNode = aliveIter.next();
					
					if(predecessorNode.nodeNumber == -1){
						predecessorNode = nextNode;
					} else if(nextNode.nodeNumber > predecessorNode.nodeNumber){
						predecessorNode = nextNode;
					}
				}
			}
			
			//now we know our predecessor and successor [successorNode, predecessorNode], it is possible that they are the same
			//check if successor/predecessor are the same
			Vector<Node> replicators = new Vector<Node>();
			replicators.addElement(successorNode);
			if(successorNode.nodeNumber != predecessorNode.nodeNumber){
				replicators.addElement(predecessorNode);
			}
			
			//send puts of all covered keys to each node in the replicators vector
			KeyValueStore kvstore = new KeyValueStore();
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

}
