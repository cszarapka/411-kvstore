package com.cam.test.eece411.Messages;

import com.cam.test.eece411.TestingClient;


/**
 * A message sent to a node of the KVStore, requesting
 * the node retrieve a value associated with a specified
 * key
 * @author cam
 *
 */


public class CreateTableRequest extends SendMessage {

	public CreateTableRequest() {
		super(TestingClient.CREATE_DHT);
		this.data = new byte[uniqueID.length + 1];
		assembleData();
	}

	@Override
	protected void assembleData() {
		super.assembleData();
	}

	public String toString() {
		return super.toString() + "\n";
	}
}
