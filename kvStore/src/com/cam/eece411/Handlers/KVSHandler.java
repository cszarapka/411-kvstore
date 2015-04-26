package com.cam.eece411.Handlers;

import java.util.logging.Logger;

import com.cam.eece411.Server;
import com.cam.eece411.Communication.AppResponse;
import com.cam.eece411.Communication.Builder;
import com.cam.eece411.Communication.Message;
import com.cam.eece411.Communication.UDPSocket;
import com.cam.eece411.Structures.DHT;
import com.cam.eece411.Structures.KVS;
import com.cam.eece411.Structures.Node;
import com.cam.eece411.Utilities.Commands;
import com.cam.eece411.Utilities.Protocols;
import com.cam.eece411.Utilities.Utils;

public class KVSHandler implements Runnable {
	private static final Logger log = Logger.getLogger(KVSHandler.class.getName());
	
	private Message msg;
	private UDPSocket socket;
	
	public KVSHandler(Message msg, UDPSocket socket) {
		this.msg = msg;
		this.socket = socket;//new UDPSocket(Utils.KVS_PORT);
	}

	public void run() {
		log.setLevel(Protocols.LOGGER_LEVEL);
		log.info("KVSHandler launched");
		
		// If it is a replicated PUT command we don't bother checking
		// if it is in our key range.
		if (msg.getCommand() == Commands.REP_PUT) {
			handleREPLICATED_PUT();
			//socket.close();
			return;
		}
		
		if(msg.getCommand() == Commands.ECHOED) {
			//TODO: don't know if it should be handled this way
			
			socket.send(Builder.echo_return(msg,ECHOEDResponse()), msg.getReturnAddress(), Utils.MAIN_PORT);
			log.info("Sending response to echoed put to " + msg.getReturnAddress() + ":" + Utils.MAIN_PORT);
			return;
		}
		
		if(msg.getCommand() == Commands.ECHO_RETURN) { //
			log.info("Sending message: " + Utils.bytesToHexString(Builder.echoedResponseToClient(msg)));
			byte[] x = {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
			socket.send(Builder.echoedResponseToClient(msg), msg.getEchoReturnAddress(), msg.getEchoReturnPort());
			log.info("Echo_Return received and sent to " +  msg.getEchoReturnAddress() + ":" + msg.getEchoReturnPort());
			return;
		}
		
		// Find the node who is responsible for servicing this command
		Node servicingNode = DHT.findNodeFor(msg.getKey());
		
		if (servicingNode == null) {
			log.severe("No node found for key.");
			//socket.close();
			return;
		} else {
			// If I'm responsible for the command
			if (Server.me.nodeID == servicingNode.nodeID) {
				// Respond to the command appropriately
				
				switch (msg.getCommand()) {
					case Commands.PUT: send(PUTResponse()); break;
					case Commands.GET: send(GETResponse()); break;
					case Commands.REMOVE: send(REMOVEResponse()); break;
				}
			} else {
				// Send it to the node who is responsible for it
				UDPSocket newSocket = new UDPSocket(Utils.KVS_PORT);
				newSocket.send(Builder.echo(msg), servicingNode.addr, Utils.MAIN_PORT);
				log.info("ECHOed " + Utils.byteCmdToString(msg.getCommand()) + " sent to " + servicingNode.nodeID + "@" + servicingNode.addr.getHostName() + ":" + Utils.MAIN_PORT);
				newSocket.close();
			}
		}
		//socket.close();
	}

	private AppResponse ECHOEDResponse() {
		int command = msg.getAppCommand();
		if(command == Commands.GET) {
			return GETResponse();
		} else if(command == Commands.PUT) {
			return PUTResponse();
		} else if(command == Commands.REMOVE) {
			return REMOVEResponse();
		} else {
			return null;
		}
	}
	
	private AppResponse PUTResponse() {
		log.setLevel(Protocols.LOGGER_LEVEL);
		byte responseCode;
		String output;
		synchronized (KVS.class) {
			// Update/add the key-value pair into our store
			responseCode = KVS.put(msg.getKey(), msg.getValue());
		}
		if (responseCode == Commands.SUCCESS) {
			output = "PUT key: " + Utils.bytesToHexString(msg.getKey()) + "\n";
			
			// Get your immediate neighbours on both sides of you
			Node cw = DHT.getPrevNodeOf(Server.me);
			Node ccw = DHT.getNextNodeOf(Server.me);
			
			// Replicate this PUT if they are not you and are distinct
			
			if (cw.nodeID != Server.me.nodeID) {
				socket.send(Builder.replicatedPut(msg), cw.addr, Utils.MAIN_PORT);
				output += "and REPLICATED to " + cw.nodeID + "@" + cw.addr.getHostName() + ":" + Utils.MAIN_PORT;
			}
			
			if (ccw.nodeID != Server.me.nodeID && ccw.nodeID != cw.nodeID) {
				socket.send(Builder.replicatedPut(msg), ccw.addr, Utils.MAIN_PORT);
				output += " and to " + ccw.nodeID + "@" + ccw.addr.getHostName() + ":" + Utils.MAIN_PORT;
			}
			log.info(output);
		}
		
		return new AppResponse(msg, responseCode);
	}
	
	private void REP_PUTResponse(){
		log.setLevel(Protocols.LOGGER_LEVEL);
		byte responseCode;
		String output;
		log.info("[REPLICATED PUT]: key is " + msg.getKey());
		log.info("[REPLICATED PUT]: value is " + msg.getValue());
		synchronized (KVS.class) {
			// Update/add the key-value pair into our store
			responseCode = KVS.put(msg.getKey(), msg.getValue());
		}
		log.info("[REPLICATED PUT]: received from " + msg.getReturnAddress());
		log.info("[REPLICATED PUT]: response code: " + responseCode);
	}
	
	private AppResponse GETResponse() {
		// Get the value from our key-value pair
		byte[] value = KVS.get(msg.getKey());
		
		// Based on the value, build our response
		if (value != null) {
			return new AppResponse(msg, value);
		} else {
			return new AppResponse(msg, Commands.KEY_DNE);
		}
	}
	
	private AppResponse REMOVEResponse() {
		byte responseCode;
		synchronized (KVS.class) {
			// Remove the value (and key) from our key-value store
			responseCode = KVS.remove(msg.getKey());
		}
		// Build and send the response
		return new AppResponse(msg, responseCode);
	}
	
	private void handleREPLICATED_PUT() {
		REP_PUTResponse();
	}
	
	private void send(AppResponse r) {
		log.setLevel(Protocols.LOGGER_LEVEL);
		socket.send(r.buffer, r.ipToSendTo, r.portToSendTo);
		log.info("Sending message: " + Utils.bytesToHexString(r.buffer));
		log.info("Response: " + Utils.byteCodeToString(r.responseCode) + " sent to " + r.ipToSendTo.getHostName() + ":" + r.portToSendTo);
	}
}
