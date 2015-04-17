package com.cam.eece411.Structures;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import com.cam.eece411.Utilities.Commands;

/**
 * A representation of this node's key-value store.
 * Allows for GET, PUT and REMOVE commands
 * @author cam
 *
 */
public class KVS {
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(KVS.class.getName());
	
	private static ConcurrentHashMap<ByteBuffer, byte[]> store = new ConcurrentHashMap<ByteBuffer, byte[]>();
	
	// Obligatory constructor
	public KVS() {}
	
	/**
	 * Puts a value associated with a key into the key-value store
	 * @param key	the key of the value to put
	 * @param value	the value to put
	 * @return		the resulting response code of this action, possible values:
	 * 					Protocols.CODE_SUCCESS
	 * 					Protocols.CODE_OUT_OF_SPACE
	 */
	public static byte put(byte[] key, byte[] value) {
		byte responseCode = Commands.SUCCESS;
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
		byte responseCode = Commands.SUCCESS;

		if (store.remove(ByteBuffer.wrap(key)) == null) {
			responseCode = Commands.KEY_DNE;
		}
		// TODO: add other possible codes
		return responseCode;
	}
	
	/**
	 * Returns a Set with all the keys in this KeyValueStore
	 * @return	a ByteBuffer Set of all local keys
	 */
	public static Set<ByteBuffer> getKeys() {
		return store.keySet();
	}
}
