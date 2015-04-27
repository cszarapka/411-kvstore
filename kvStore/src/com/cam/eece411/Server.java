package com.cam.eece411;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.cam.eece411.Communication.AppResponse;
import com.cam.eece411.Communication.Builder;
import com.cam.eece411.Communication.Message;
import com.cam.eece411.Communication.UDPSocket;
import com.cam.eece411.Handlers.JoinHandler;
import com.cam.eece411.Handlers.KVSHandler;
import com.cam.eece411.Handlers.UpdateHandler;
import com.cam.eece411.Structures.DHT;
import com.cam.eece411.Structures.Node;
import com.cam.eece411.Utilities.Commands;
import com.cam.eece411.Utilities.Protocols;
import com.cam.eece411.Utilities.Utils;

/**
 * The main process that will be running an instance of our DHT-KVStore
 * @author cam
 *
 */
public class Server {
	private static final Logger log = Logger.getLogger(Server.class.getName());
	

	public static Node me;
	public static Integer state;
	public static UDPSocket socket;
	public static UDPSocket joinSocket;
	public static UDPSocket repSocket;
	public static UDPSocket updateSocket;
	public static List<String> nodes = null;

	public static void main(String[] args) throws SocketException, IOException, InterruptedException {
		
		log.info("And so it begins. (V14)");

		// Instantiate ourself as a node and set our state
		setup();

		// Setup the main listening socket
		socket = new UDPSocket(Utils.MAIN_PORT);
		joinSocket = new UDPSocket(Utils.JOIN_PORT);
		updateSocket = new UDPSocket(Utils.UPDATE_PORT);
		repSocket = new UDPSocket(Utils.REP_PORT);

		// Setup some local variables
		Message msg;
		byte cmd;

		// Check if we were given the CREATE-DHT command
		if (args.length >= 1) {
			log.info(args[0]);
			if (args[0].equalsIgnoreCase("create")) {
				createDHT();
			}
			if(args.length == 2) {
				if(args[1].equalsIgnoreCase("log")) {
					Protocols.LOGGER_LEVEL = java.util.logging.Level.ALL;
				}
			}
		}
		log.setLevel(Protocols.LOGGER_LEVEL);
		// Try to join the DHT
		readFrom(Utils.NODE_LIST);
		while (state == Utils.OUT_OF_DHT) {
			attemptToJoin();
		}
		
		// Launch the Watchdog Thread
		(new Thread(new WDT(Utils.WDT_PORT))).start();

		// Listen for commands
		while (state == Utils.IN_DHT) {
			// Wait for a message
			log.info("Waiting for a message on " + Utils.MAIN_PORT);
			msg = socket.receive();
			cmd = msg.getCommand();
			log.info(Utils.byteCmdToString(cmd) + " received from " + msg.getReturnAddress() + ":" + msg.getReturnPort());
			//log.info("Message received: " + Utils.bytesToHexString(msg.getData()));
			// TODO: Send back an acknowledgement?

			// React to message
			if (Commands.isKVSCommand(cmd)) {
				// Launch the KVS Handler thread
				(new Thread(new KVSHandler(msg, socket))).start();
				
			}
			else if (Commands.isJoinMessage(cmd)) {
				// Launch the Join Handler thread
				(new Thread(new JoinHandler(msg, joinSocket))).start();
			}
			else if (Commands.isUpdate(cmd)) {
				// Launch the Update Handler thread
				(new Thread(new UpdateHandler(msg, repSocket, updateSocket))).start();
			}
			else if (cmd == Commands.SHUTDOWN) {
				respondToSHUTDOWN(msg);
				System.exit(0);
			}
		}

		socket.close();
	}

	
	public static void setup() {
		// Instantiate ourself as a node
		try { me = new Node(Utils.MAX_NODE_NUMBER, InetAddress.getLocalHost()); }
		catch (UnknownHostException e) { log.log(Level.SEVERE, e.toString(), e); }

		// Initially our state will always be OUT_OF_TABLE
		state = Utils.OUT_OF_DHT;
	}

	
	public static void readFrom(String file) {
		// Declare the list object
		nodes = new ArrayList<String>();
		
		BufferedReader br = null;
		try {
			String currentLine;
			
			// Open the file
			br = new BufferedReader(new FileReader(file));
			
			// Read all lines
			while ((currentLine = br.readLine()) != null) {
				// Add each line to our list
				nodes.add(currentLine.trim());
			}
		} catch (IOException e) {
			log.log(Level.SEVERE, e.toString(), e);
		} finally {
			// Cloase the file if it was opened
			try { if (br != null) br.close(); }
			catch (IOException e) { log.log(Level.SEVERE, e.toString(), e); }
			log.info("Finished reading from " + file);
		}
	}

	
	public static void createDHT() {
		// Add ourself as the first node in the circle
		DHT.add(me);
		state = Utils.IN_DHT;
		log.info("DHT created");
	}

	
	public static void attemptToJoin() {
		messageRandomNode();
		
		Message msg = receiveJoinResponse();
		if (msg != null) {
			if (msg.getCommand() == Commands.JOIN_RESPONSE) {
				// Set our node ID
				me.nodeID = msg.getNodeID();
				
				// Copy the DHT
				DHT.add(msg.getNodes());
				
				
				// Add ourself to our DHT
				DHT.add(me);
				

				//give each node a timestamp
				for (Node node : DHT.nodes()) {
					node.updateTimestamp();
				}
				
				// Set our state to IN DHT
				state = Utils.IN_DHT;
				
				// Broadcast an IS ALIVE message 
				socket.broadcast(Builder.isAlive(me), DHT.broadcastList(), Utils.MAIN_PORT);
				log.info("Joined table as node " + me.nodeID + " and broadcasted the fact");
			}
		}
	}
	

	public static void messageRandomNode() {
		// Get a random number from 0 to (NUM_NODES - 1)
		int random = (int)(Math.random()*(nodes.size() - 1));
		
		InetAddress addr = null;

		// Send the node at random a join request if it isn't us
		if (!me.name.equals(nodes.get(random))) {
			try {
				addr = InetAddress.getByName(nodes.get(random));
				socket.send(Builder.joinRequest(), addr, Utils.MAIN_PORT);
				log.info("JOIN-REQUEST sent to " + addr.getHostName() + ":" + Utils.MAIN_PORT);
			} catch (UnknownHostException e) {
				log.log(Level.SEVERE, e.toString(), e);
			}
			
		}
	}
	
	
	public static Message receiveJoinResponse() {
		socket.setTimeout(Utils.JOIN_TIMEOUT);
		Message msg = socket.receive();
		if (msg == null) {
			log.info("JOIN-REQUEST timed out.");
		} else {
			log.info("JOIN-RESPONSE received from " + msg.getReturnAddress().getHostName() + ":" + msg.getReturnPort());
			// Disable the timeout
			socket.setTimeout(0);
		}
		return msg;
	}
	
	
	public static void respondToSHUTDOWN(Message msg) {
		send(new AppResponse(msg, Commands.SUCCESS));
		log.info("Shutting down on purpose.");
	}
	
	
	public static void send(AppResponse r) {
		socket.send(r.buffer, r.ipToSendTo, r.portToSendTo);
		log.info("Response: " + Utils.byteCodeToString(r.responseCode) + " sent to " + r.ipToSendTo.getHostName() + ":" + r.portToSendTo);
	}
}
