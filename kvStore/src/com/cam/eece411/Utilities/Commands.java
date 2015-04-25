package com.cam.eece411.Utilities;

/**
 * The constants used for commands at the app-layer and 
 * the node-layer of our system, and response codes.
 * @author cam
 *
 */
public class Commands {

	/*
	 * Application-Level (Key/Value Store) Commands
	 */

	/**
	 * Add/update a value for the given key
	 */
	public static final byte PUT		= 1;

	/**
	 * Ret the value for the given key
	 */
	public static final byte GET		= 2;

	/**
	 * Remove the value for the given key
	 */
	public static final byte REMOVE		= 3;

	/**
	 * Notifies that the message contains a "passed-along"
	 * app-layer command. Following the command will be
	 * details of whom to respond to, the the regular
	 * app-layer command 
	 */
	public static final byte ECHOED		= 20;

	/**
	 * A replicated put command...
	 */
	public static final byte REP_PUT	= 21;
	
	/**
	 * Returned echo response
	 */
	public static final byte ECHO_RETURN = 26;

	/*
	 * DHT Commands
	 */

	/**
	 * Command to tell a node to simulate a crash
	 */
	public static final byte SHUTDOWN		= 4;

	/**
	 * Command to ask a node to join the DHT
	 */
	public static final byte JOIN_REQUEST	= 22;

	/**
	 * Command to use for a response to a Join-Request. Gives the
	 * requesting node an ID and your current DHT "view"
	 */
	public static final byte JOIN_RESPONSE	= 23;

	/**
	 * Specifies a message stating that the node with specified
	 * node ID and address is alive (in DHT)
	 */
	public static final byte IS_ALIVE		= 24;

	/**
	 * Specifies a message stating that the node with specified
	 * node ID and address is dead (out of DHT) and should be 
	 * removed from your local DHT
	 */
	public static final byte IS_DEAD		= 25;

	/*
	 * Response Codes
	 */
	// Response codes sent to the user of the DHT-KVStore
	public static final byte SUCCESS			= 0;
	public static final byte KEY_DNE			= 1;
	public static final byte OUT_OF_SPACE		= 2;
	public static final byte SYSTEM_OVERLOAD	= 3;
	public static final byte INTERNAL_FAILURE	= 4;
	public static final byte UNKNOWN_COMMAND	= 5;

	/*
	 * Methods for determining a set of commands
	 */

	public static boolean isKVSCommand(byte command) {
		if (command == GET || command == PUT || command == REMOVE ||
			command == ECHOED || command == REP_PUT) {
			return true;
		}
		return false;
	}

	public static boolean isJoinMessage(byte command) {
		if (command == JOIN_REQUEST || command == JOIN_RESPONSE) {
			return true;
		}
		return false;
	}

	public static boolean isUpdate(byte command) {
		if (command == Commands.IS_ALIVE || command == Commands.IS_DEAD) {
			return true;
		}
		return false;
	}
}
