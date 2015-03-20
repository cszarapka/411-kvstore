package com.cam.test.eece411.Messages;

import com.cam.test.eece411.TestingClient;

/**
 * A message sent to a node of the KVStore, requesting
 * the node to simulate a crash
 * @author cam
 *
 */
public class ShutdownRequest extends SendMessage {
	public ShutdownRequest() {
		super(TestingClient.SHUTDOWN);
		this.data = new byte[uniqueID.length + 1];
		assembleData();
	}
	
	@Override
	public String toString() {
		return super.toString();
	}
}
