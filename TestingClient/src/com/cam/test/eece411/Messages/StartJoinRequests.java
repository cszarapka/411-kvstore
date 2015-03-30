package com.cam.test.eece411.Messages;

import com.cam.test.eece411.TestingClient;

public class StartJoinRequests extends SendMessage {

	public StartJoinRequests(byte[] key) {
		super(TestingClient.START_JOIN_REQUESTS);
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
