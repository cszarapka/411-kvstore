package com.cam.eece411.Messages;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Arrays;

import com.cam.eece411.Utilities.Helper;
import com.cam.eece411.Utilities.Protocols;

public class ReceivedMessage {
	protected InetAddress senderIP;
	protected int senderPort;
	protected byte[] uniqueID;
	protected byte command;
	protected byte[] data;

	private byte[] key;
	private int valueLength;
	private byte[] value;
	private int nodeNumber;

	private int offeredNodeNumber;
	private int numberOfNodes;
	private byte[] nodes;

	private byte[] nodeIP;

	/**
	 * Builds a message object from a received packet.
	 * The message contents are parsed and put into local variables to
	 * be accessed via getter methods. Based on the command in the message
	 * we know the format of the message and use that knowledge to correctly
	 * retrieve the data
	 * @param packet
	 */
	public ReceivedMessage(DatagramPacket packet) {
		// Get the guaranteed data
		senderIP = packet.getAddress();
		senderPort = packet.getPort();
		data = packet.getData();
		uniqueID = Arrays.copyOfRange(data, 0, 16);
		command = data[16];

		// Get the key, if there is one
		if (command < Protocols.APP_CMD_SHUTDOWN) {
			key = Arrays.copyOfRange(data, 17, 49);

			// Get the value length and value, if there are any
			if (command == Protocols.APP_CMD_PUT) {
				valueLength = Helper.valueLengthBytesToInt(Arrays.copyOfRange(data, 49, 51));
				value = Arrays.copyOfRange(data, 51, 51+valueLength);
			}
		}

		// Get the offered node number from the join protocol
		if (command == Protocols.CMD_JOIN_RESPONSE || command == Protocols.CMD_JOIN_CONFIRM) {
			offeredNodeNumber = Helper.unsignedByteToInt(data[17]);
			
			// Get the number of nodes, and then the nodes (IP's and #'s)
			if (command == Protocols.CMD_JOIN_RESPONSE) {
				numberOfNodes = data[18];
				nodes = Arrays.copyOfRange(data, 19, 19+(numberOfNodes*5));
			}
		}

		// Get the message details specific to IS-DEAD messages
		if (command == Protocols.CMD_IS_DEAD) {
			nodeNumber = Helper.valueLengthBytesToInt(Arrays.copyOfRange(data, 17, 18));
		}
		
		// Get the message details specific to IS-ALIVE messages
		if(command == Protocols.CMD_IS_ALIVE) {
			offeredNodeNumber = Helper.unsignedByteToInt(data[21]);

			nodeIP = new byte[4];
			for(int i = 0; i < 4; i++) {
				nodeIP[i] = data[17+i];
			}
		}
	}

	
	public int getNodeNumber() {
		return this.nodeNumber;
	}

	public byte[] getData() {
		return this.data;
	}

	/**
	 * Returns the unique ID of the request message
	 * @return	the unique ID
	 */
	public byte[] getUniqueID() {
		return this.uniqueID;
	}

	/**
	 * Returns the command of the request message
	 * @return	either a GET, PUT, REMOVE or SHUTDOWN command
	 */
	public byte getCommand() {
		return this.command;
	}

	/**
	 * Returns the IP address of the immediate sender of this message
	 * @return	the IP address of the sender
	 */
	public InetAddress getSenderIP() {
		return this.senderIP;
	}

	/**
	 * Returns the port this message was sent on
	 * @return	the port
	 */
	public int getSenderPort() {
		return this.senderPort;
	}

	public int getValueLength() {
		return this.valueLength;
	}

	public byte[] getKey() {
		return this.key;
	}

	public byte[] getValue() {
		return this.value;
	}

	public int getOfferedNodeNumber() {
		return this.offeredNodeNumber;
	}

	public byte[] getNodeIP() {
		return this.nodeIP;
	}

	public int getNumberOfNodes() {
		return this.numberOfNodes;
	}

	public byte[] getNodes() {
		return this.nodes;
	}

	/**
	 * Based on the command, gets the possible message contents
	 */
	public String toString() {
		String string =	"- - Message contents:\n" +
				"Unique ID: " + Helper.bytesToHexString(uniqueID) + "\n" +
				"Command: " + Helper.byteCodeToString(command) + "\n";

		// Get the key, if there is one
		if (command < Protocols.APP_CMD_SHUTDOWN) {
			string += "Key: " + Helper.bytesToHexString(key) + "\n";
		}

		// Get the value length and value, if there are any
		if (command == Protocols.APP_CMD_PUT) {
			string += "Value-Length: " + valueLength + "\n";
			string += "Value: " + Helper.bytesToHexString(value) + "\n";
		}

		if (command == Protocols.CMD_JOIN_RESPONSE) {
			string += "Offered Node Number: " + offeredNodeNumber + "\n";
			string += "Number of nodes sent: " + numberOfNodes + "\n";
			string += "# | IP:\n";
			int i = 0;
			try {
				while (i < nodes.length) {
					string += nodes[i+4] + " | " + InetAddress.getByAddress(Arrays.copyOfRange(nodes, i, i+4)) + "\n";
					i += 5;
				}
			} catch (java.net.UnknownHostException e) {
				e.printStackTrace();
			} 
		}

		if (command == Protocols.CMD_JOIN_CONFIRM) {
			string += "Offered node number: " + offeredNodeNumber + "\n";
		}

		if (command == Protocols.CMD_IS_DEAD) {
			nodeNumber = Helper.valueLengthBytesToInt(Arrays.copyOfRange(data, 17, 18));
		}
		
		if(command == Protocols.CMD_IS_ALIVE) {
			string += "Offered node number " + offeredNodeNumber + "\n";
			string += "Node IP: " + nodeIP.toString() + "\n";
		}

		return string + "\n";
	}
}
