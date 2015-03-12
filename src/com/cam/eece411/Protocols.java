package com.cam.eece411;

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
	
	// Commands that will be sent from a node in the DHT
	
	
	// Response codes sent to the user of the DHT-KVStore
	public static final byte CODE_SUCCESS			= 0;
	public static final byte CODE_KEY_DNE			= 1;
	public static final byte CODE_OUT_OF_SPACE		= 2;
	public static final byte CODE_SYSTEM_OVERLOAD	= 3;
	public static final byte CODE_INTERNAL_FAILURE	= 4;
	public static final byte CODE_UNKNOWN_COMMAND	= 5;
	
	// Port numbers
	public static final int LISTENING_PORT	= 5000;
	public static final int SENDING_PORT	= 5001;
	
	// Max message size
	public static final int MAX_MSG_SIZE	= 15100;
	
	// Relative to nodes
	public static final int MAX_NODE_NUMBER	= 255;
}
