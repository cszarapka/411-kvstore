package com.cam.eece411.Utilities;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Iterator;
import java.util.Random;

import com.cam.eece411.Structures.KVS;

/**
 * This helper class defines static methods that make life easier, like byte conversions.
 * @author cam
 *
 */
public final class Utils {

	private Utils() {}

	// filenames of node lists
	public static final String NODE_LIST = "/home/ubc_eece411_5/repTestNodes.txt";

	/*
	 * Port Numbers
	 */

	/**
	 * The main thread's listening port. Nearly all messages are
	 * sent to this port. Outgoing messages from other threads are
	 * sent on their own port, and any specific responses they need
	 * are sent to that port as well.
	 */
	public static final int MAIN_PORT	= 7010;

	/**
	 * The Join-Handler's port for sending messages
	 */
	public static final int JOIN_PORT	= 7020;

	/**
	 * The KVS-Handler's port for sending messages
	 */
	public static final int KVS_PORT	= 7030;

	/**
	 * The Update-Handler's port for sending messages
	 */
	public static final int UPDATE_PORT	= 7040;

	/**
	 * The WDT's port for sending messages
	 */
	public static final int WDT_PORT 	= 7050;
	
	/**
	 * The UpdateHandler's port for sending isAlive replication PUTs
	 */
	public static final int REP_PORT	= 7060;

	/**
	 * The UpdateHandler's port for sending isAlive replication PUTs
	 */
	public static final int REP_PORT	= 7060;
	
	// Timeout values (msec)
	public static final int JOIN_TIMEOUT = 5000;
	public static final int WDT_TIMEOUT = 500000; // 8 minutes ish

	// Max message size
	public static final int MAX_MSG_SIZE	= 15100;

	// Relative to nodes
	public static final int MAX_NODE_NUMBER	= 255;
	public static final int MAX_NUMBER_OF_NODES = MAX_NODE_NUMBER + 1;

	// States the server is in during its lifetime
	public static final int OUT_OF_DHT = 0;
	public static final int IN_DHT = 1;

	/*
	 * Helpful Functions
	 */

	/**
	 * Returns an int from the byte array specified, in little endian
	 * @param b	a byte array to convert to an int
	 * @return	the integer corresponding to the byte array
	 */
	public static int byteArrayToInt(byte[] b) {
		final ByteBuffer bb = ByteBuffer.wrap(b);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		return bb.getInt();
	}

	public static void printKVStore() {
		Iterator<ByteBuffer> keys = KVS.getKeys().iterator();
		byte[] currentKey = new byte[32];
		int hash;
		//byte[] currentValue = null;
		while (keys.hasNext()) {
			// Unwrap the array from the byte buffer
			currentKey = keys.next().array();

			// Hash the key
			hash = HashFunction.hash(currentKey);
			KVS.get(currentKey);
			System.out.println("\nHash: " + hash + " Key: " + bytesToHexString(currentKey) + " Value: " + bytesToHexString(KVS.get(currentKey)));
		}
	}



	/*
	 * I don't quite know what this was for any more..
	 */
	public static int valueLengthBytesToInt(byte[] b) {
		byte[] a = new byte[4];
		a[2] = b[0];
		a[3] = b[1];
		return byteArrayToInt(a);
	}

	public static short byteArrayToShort(byte[] b) {
		final ByteBuffer bb = ByteBuffer.wrap(b);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		return bb.getShort();
	}

	/**
	 * Returns a byte array from the int specified, in little endian
	 * @param i	the int to convert to a byte array
	 * @return	the byte array corresponding to the int
	 */
	public static byte[] intToByteArray(int i) {
		final ByteBuffer bb = ByteBuffer.allocate(Integer.SIZE / Byte.SIZE);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.putInt(i);
		return bb.array();
	}

	/**
	 * Converts a byte array to a hex string
	 * @param bytes	the byte array
	 * @return		the corresponding hex string
	 */
	public static String bytesToHexString(byte[] bytes){
		String string = "";
		if (bytes != null) {
			for (int i = 0; i < bytes.length; i++) {
				string += String.format("%02X", bytes[i]);
			}
		}
		return string;
	}  

	/**
	 * Returns a randomized byte array of specified length
	 * @param length	the length of the byte array to return
	 * @return			the random byte array
	 */
	public static byte[] generateRandomByteArray(int length) {
		Random rand = new Random();
		byte[] bytes = new byte[length];
		rand.nextBytes(bytes);
		return bytes;
	}

	/**
	 * Returns an int from a byte value treated as unsigned
	 * @param b	unsigned byte
	 * @return	integer (from 0-255)
	 */
	public static int unsignedByteToInt(byte b) {
		return (int) b & 0xFF;
	}

	/**
	 * Converts a byte command code into the corresponding string
	 * @param b	the byte command
	 * @return	the string of that command
	 */
	public static String byteCmdToString(byte b) {
		String string = "UNKNOWN";
		switch(b) {
			case Commands.PUT: 		string = "PUT"; break;
			case Commands.GET: 		string = "GET"; break;
			case Commands.REMOVE: 		string = "REMOVE"; break;
			case Commands.SHUTDOWN: 	string = "SHUTDOWN"; break;
			case Commands.JOIN_REQUEST: 	string = "JOIN-REQUEST"; break;
			case Commands.ECHOED: 			string = "ECHOED"; break;
			case Commands.IS_ALIVE: 		string = "IS-ALIVE"; break;
			case Commands.IS_DEAD: 		string = "IS-DEAD"; break;
			case Commands.JOIN_RESPONSE:	string = "JOIN-RESPONSE"; break;
			case Commands.REP_PUT:	string = "REPLICATION-PUT"; break;
		}
		return string;
	}

	public static String byteCodeToString(byte b) {
		String string = "UNKNOWN";
		switch (b) {
			case Commands.SUCCESS:			string = "SUCCESS"; break;
			case Commands.KEY_DNE:			string = "KEY-DNE"; break;
			case Commands.OUT_OF_SPACE:		string = "OUT-OF-SPACE"; break;
			case Commands.SYSTEM_OVERLOAD: 	string = "SYSTEM-OVERLOAD"; break;
			case Commands.INTERNAL_FAILURE:	string = "INTERNAL-FAILURE"; break;
			case Commands.UNKNOWN_COMMAND:	string = "UNKNOWN-COMMAND"; break;
		}
		return string;
	}

	public static String stateToString(int s) {
		String string = "UNKNOWN";
		switch (s) {
			case OUT_OF_DHT:string = "OUT OF DHT"; break;
			case IN_DHT:	string = "IN DHT"; break;
		}
		return string;
	}
}
