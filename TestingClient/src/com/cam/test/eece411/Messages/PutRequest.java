package com.cam.test.eece411.Messages;

import com.cam.test.eece411.Helper;
import com.cam.test.eece411.TestingClient;

/**
 * A message sent to a node of the KVStore, requesting
 * the node add (or update) a key/value pair
 * @author cam
 *
 */
public class PutRequest extends SendMessage {
	public byte[] 	key;
	public int 		valueLength;
	public byte[] 	value;

	public PutRequest(byte[] key, byte[] value) {
		super(TestingClient.PUT);
		this.key = key;
		this.valueLength = value.length;
		this.value = value;
		this.data = new byte[uniqueID.length + 1 + key.length + 2 + valueLength];
		assembleData();
	}
	
	public PutRequest(byte[] key, byte[] value, int valueLength) {
		super(TestingClient.PUT);
		this.key = key;
		this.valueLength = valueLength;
		this.value = value;
		this.data = new byte[uniqueID.length + 1 + key.length + 2 + valueLength];
		assembleData();
	}

	@Override
	protected void assembleData() {
		super.assembleData();
		// Add the key
		for (int i = 0; i < key.length; i++) {
			data[index++] = key[i];
		}
		// Add the value length
		data[index++] = Helper.intToByteArray(valueLength)[0];
		data[index++] = Helper.intToByteArray(valueLength)[1];

		// Add the value
		for (int i = 0; i < valueLength; i++) {
			data[index++] = value[i];
		}		
	}
	
	@Override
	public String toString() {
		return super.toString() + "Key: " + Helper.bytesToHexString(key) + "\n" +
				"Value-Length: " + valueLength + "\n";
				//"Value: " + Helper.bytesToHexString(value) + "\n";
	}
}
