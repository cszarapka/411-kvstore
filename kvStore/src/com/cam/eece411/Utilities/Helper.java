package com.cam.eece411.Utilities;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Iterator;
import java.util.Random;

import com.cam.eece411.KeyValueStore;
import com.cam.eece411.Server;
import com.cam.eece411.Messages.PutRequest;

/**
 * This helper class defines static methods that make life easier, like byte conversions.
 * @author cam
 *
 */
public class Helper {
	/**
	 * The current number of nodes being used for testing/deployment
	 */
	public static int NUM_NODES = 5;
	
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
		Iterator<ByteBuffer> keys = KeyValueStore.getKeys().iterator();
		byte[] currentKey = new byte[32];
		int hash;
		byte[] currentValue = null;
		while (keys.hasNext()) {
			// Unwrap the array from the byte buffer
			currentKey = keys.next().array();
			
			// Hash the key
			hash = MD5HashFunction.hash(currentKey);
			KeyValueStore.get(currentKey);
			if(Server.VERBOSE) System.out.println("\nHash: " + hash + " Key: " + bytesToHexString(currentKey) + " Value: " + bytesToHexString(KeyValueStore.get(currentKey)));
			
			
		}
	}
	
	/*
	 * I don't quite know what this was for any more..
	 */
	public static int valueLengthBytesToInt(byte[] b) {
		return (int) b[0] + (((int) b[1]) * 256);
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
	public static String byteCodeToString(byte b) {
		String string = "UNKNOWN";
		switch(b) {
			case Protocols.APP_CMD_PUT: 		string = "PUT"; break;
			case Protocols.APP_CMD_GET: 		string = "GET"; break;
			case Protocols.APP_CMD_REMOVE: 		string = "REMOVE"; break;
			case Protocols.APP_CMD_SHUTDOWN: 	string = "SHUTDOWN"; break;
			case Protocols.CMD_CREATE_DHT: 		string = "CREATE-DHT"; break;
			case Protocols.CMD_START_JOIN_REQUESTS: string = "START-JOIN-REQUESTS"; break;
			case Protocols.CMD_JOIN_REQUEST: 	string = "JOIN-REQUEST"; break;
			case Protocols.CMD_ECHOED: 			string = "ECHOED"; break;
			case Protocols.CMD_IS_ALIVE: 		string = "IS-ALIVE"; break;
			case Protocols.CMD_IS_DEAD: 		string = "IS-DEAD"; break;
			case Protocols.CMD_JOIN_RESPONSE:	string = "JOIN-RESPONSE"; break;
			case Protocols.CMD_JOIN_CONFIRM: 	string = "JOIN-CONFIRM"; break;
		}
		return string;
	}
}
