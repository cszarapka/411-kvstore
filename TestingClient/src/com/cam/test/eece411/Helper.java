package com.cam.test.eece411;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;

/**
 * This helper class defines static methods that make life easier, like byte conversions.
 * @author cam
 *
 */
public class Helper {
	/**
	 * Returns an int from the byte array specified, in little endian
	 * @param b	a byte array to convert to an int
	 * @return	the integer corresponding to the byte array
	 */
//	public static int byteArrayToInt(byte[] b) {
//		final ByteBuffer bb = ByteBuffer.wrap(b);
//		bb.order(ByteOrder.LITTLE_ENDIAN);
//		return bb.getInt();
//	}
	
	public static int valueLengthBytesToInt(byte[] b) {
		byte[] a = new byte[4];
		a[2] = b[0];
		a[3] = b[1];
		return byteArrayToInt(a);
	}

	/**
	 * Returns a byte array from the int specified, in little endian
	 * @param i	the int to convert to a byte array
	 * @return	the byte array corresponding to the int
	 */
	public static byte[] intToByteArray(int i) {
		final ByteBuffer bb = ByteBuffer.allocate(Integer.SIZE / Byte.SIZE);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.putInt(i);
		return bb.array();
	}
	
	/**
	 * Returns a randomized byte array of specified length
	 * @param length	the length of the byte array to return
	 * @return			the random byte array
	 */
	public static byte[] generateRandomByteArray(int length) {
		Random rand = new Random();
		byte[] bytes = new byte[length];
		rand.nextBytes(bytes);
		return bytes;
	}
	
	public static int randInt(int min, int max) {

	    // NOTE: Usually this should be a field rather than a method
	    // variable so that it is not re-seeded every call.
	    Random rand = new Random();

	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
	}
	
	public static String bytesToHexString(byte[] bytes){
		String string = "";
		for (int i = 0; i < bytes.length; i++) {
			string += String.format("%02X", bytes[i]);
		}
        return string;
    }  
	
	public static String cmdToString(byte b) {
		String string = "UNKNOWN";
		switch(b) {
			case TestingClient.PUT: 		string = "PUT"; break;
			case TestingClient.GET: 		string = "GET"; break;
			case TestingClient.REMOVE: 		string = "REMOVE"; break;
			case TestingClient.SHUTDOWN: 	string = "SHUTDOWN"; break;
			case TestingClient.CREATE_DHT: 	string = "CREATE-DHT"; break;
			case TestingClient.START_JOIN_REQUESTS: string = "START-JOIN-REQUESTS"; break;
		}
		return string;
	}
	
	public static int byteArrayToInt(byte[] b) {
		final ByteBuffer bb = ByteBuffer.wrap(b);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		return bb.getInt();
	}
	
}
