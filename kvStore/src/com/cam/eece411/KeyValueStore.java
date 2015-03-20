package com.cam.eece411;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

import com.cam.eece411.Utilities.Protocols;

/**
 * A representation of this node's key-value store.
 * Allows for GET, PUT and REMOVE commands
 * @author cam
 *
 */
public class KeyValueStore {
	private static ConcurrentHashMap<ByteBuffer, byte[]> store = new ConcurrentHashMap<ByteBuffer, byte[]>();
	
	// Obligatory constructor
	public KeyValueStore() {}
	
	/**
	 * Puts a value associated with a key into the key-value store
	 * @param key	the key of the value to put
	 * @param value	the value to put
	 * @return		the resulting response code of this action, possible values:
	 * 					Protocols.CODE_SUCCESS
	 * 					Protocols.CODE_OUT_OF_SPACE
	 */
	public static byte put(byte[] key, byte[] value) {
		byte responseCode = Protocols.CODE_SUCCESS;
		store.put(ByteBuffer.wrap(key), Arrays.copyOf(value, value.length));
		// TODO: change the response code if the put fails due to overflow
		// TODO: test what happens when you set the max heap size, and then overflow it
		return responseCode;
	}
	
	/**
	 * Returns the value corresponding to key, or null if there is
	 * no value corresponding to the key
	 * @param key	the key of the value to get
	 * @return		the value belonging to the key, or null if there is none
	 */
	public static byte[] get(byte[] key) {
		return store.get(ByteBuffer.wrap(key));
	}
	
	/**
	 * Removes a value from the KVStore
	 * @param key	the key of the value to remove
	 * @return		the resulting response code of this action, possible values:
	 * 					Protocols.CODE_SUCCESS
	 * 					Protocols.CODE_KEY_DNE
	 */
	public static byte remove(byte[] key) {
		byte responseCode = Protocols.CODE_SUCCESS;

		if (store.remove(ByteBuffer.wrap(key)) == null) {
			responseCode = Protocols.CODE_KEY_DNE;
		}
		// TODO: add other possible codes
		return responseCode;
	}
}
