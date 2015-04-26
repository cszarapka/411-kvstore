package com.cam.eece411.Utilities;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The constants used for commands at the app-layer and 
 * the node-layer of our system, and response codes.
 * @author cam
 *
 */
public class Protocols {
	// Commands that will be sent from a user of the DHT-KVStore
	public static final byte APP_CMD_PUT		= 1;
	public static final byte APP_CMD_GET		= 2;
	public static final byte APP_CMD_REMOVE		= 3;
	public static final byte APP_CMD_SHUTDOWN	= 4;
	
	// Lower layer commands
	public static final byte CMD_CREATE_DHT				= 20;
	public static final byte CMD_START_JOIN_REQUESTS 	= 21;
	public static final byte CMD_JOIN_REQUEST			= 22;
	public static final byte CMD_ECHOED					= 23;
	public static final byte CMD_IS_ALIVE				= 24;
	public static final byte CMD_IS_DEAD				= 25;
	public static final byte CMD_JOIN_RESPONSE			= 26;
	public static final byte CMD_JOIN_CONFIRM			= 27;
	public static final byte CMD_REPLICATION_PUT		= 28;
	
	// Response codes sent to the user of the DHT-KVStore
	public static final byte CODE_SUCCESS			= 0;
	public static final byte CODE_KEY_DNE			= 1;
	public static final byte CODE_OUT_OF_SPACE		= 2;
	public static final byte CODE_SYSTEM_OVERLOAD	= 3;
	public static final byte CODE_INTERNAL_FAILURE	= 4;
	public static final byte CODE_UNKNOWN_COMMAND	= 5;
	
	// Port numbers
	public static final int LISTENING_PORT			= 5600;
	public static final int SENDING_PORT			= 5601;
	public static final int JOIN_RESPONSE_PORT 		= 5602;
	public static final int IS_ALIVE_RESPONSE_PORT 	= 5603;
	
	// Timeout values
	public static final int JOIN_TIMEOUT = 5000;
	public static final int IS_ALIVE_TIMEOUT = 10000;
	
	// Max message size
	public static final int MAX_MSG_SIZE	= 15100;
	
	// Relative to nodes
	public static final int MAX_NODE_NUMBER	= 255;
	public static final int MAX_NUMBER_OF_NODES = MAX_NODE_NUMBER + 1;
	
	// States the server is in during its lifetime
	public static final int OUT_OF_TABLE = 0;
	public static final int IN_TABLE = 1;
	public static final int LEFT_TABLE = 2;
	public static final int HANDLING_JOIN = 3;
	
	public static Level LOGGER_LEVEL = java.util.logging.Level.OFF;
}
