package com.cam.test.eece411.Messages;

import com.cam.test.eece411.Helper;
import com.cam.test.eece411.TestingClient;

/**
 * A message sent to a node of the KVStore, requesting
 * the node retrieve a value associated with a specified
 * key
 * @author cam
 *
 */
public class GetRequest extends SendMessage {
	public byte[] key;

	public GetRequest(byte[] key) {
		super(TestingClient.GET);
		this.key = key;
		this.data = new byte[uniqueID.length + 1 + key.length];
		assembleData();
	}

	@Override
	protected void assembleData() {
		super.assembleData();
		// Add the key
		for (int i = 0; i < key.length; i++) {
			data[index++] = key[i];
		}
	}
	
	public String toString() {
		return super.toString() + "Key: " + Helper.bytesToHexString(key) + "\n";
	}
}
