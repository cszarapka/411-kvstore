package com.cam.eece411;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A representation of a key-value store for use in our DHT. It offers
 * the user commands to put, get and remove key-value pairs.
 * Additionally, the key-value store can be constructed directly from
 * message contents, such as those in a join-response.
 * @author cam
 *
 */
public class KVStore {
	private ConcurrentHashMap<byte[], byte[]> store;

	/**
	 * Construct an empty KVStore
	 */
	public KVStore() {
		store = new ConcurrentHashMap<byte[], byte[]>();
	}

	/**
	 * Construct a KVStore filled with keys and values
	 * @param keys			the keys to put in the KVStore
	 * @param values		the values corresponding to the keys
	 * @param valueLengths	an array of lengths for each value
	 */
	public KVStore(byte[] keys, byte[] values, int[] valueLengths) {
		store = new ConcurrentHashMap<byte[], byte[]>();

		// Get the number of keys, each one is 32 bytes
		int numKeys = keys.length/32;
		int keysIndex = 0;
		int valuesIndex = 0;
		for (int i = 0; i < numKeys; i++) {
			store.put(Arrays.copyOfRange(keys, keysIndex, keysIndex+32),
					Arrays.copyOfRange(values, valuesIndex, valuesIndex+valueLengths[i]));
			keysIndex += 32;
			valuesIndex += valueLengths[i];
		}
	}

	/**
	 * Puts a value into the KVStore
	 * @param key	the key of the value
	 * @param value	the value to put
	 * @return		the resulting response code of this action, possible values:
	 * 					Protocols.CODE_SUCCESS
	 * 					Protocols.CODE_OUT_OF_SPACE
	 */
	public byte put(byte[] key, byte[] value) {
		byte responseCode = Protocols.CODE_SUCCESS;
		store.put(key, value);
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
	public byte[] get(byte[] key) {
		return store.get(key);
	}

	/**
	 * Removes a value from the KVStore
	 * @param key	the key of the value to remove
	 * @return		the resulting response code of this action, possible values:
	 * 					Protocols.CODE_SUCCESS
	 * 					Protocols.CODE_KEY_DNE
	 */
	public byte remove(byte[] key) {
		byte responseCode = Protocols.CODE_SUCCESS;

		if (store.remove(key) == null) {
			responseCode = Protocols.CODE_KEY_DNE;
		}
		// TODO: add other possible codes
		return responseCode;
	}
}
