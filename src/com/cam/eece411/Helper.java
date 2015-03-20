package com.cam.eece411;

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
	public static int byteArrayToInt(byte[] b) {
		final ByteBuffer bb = ByteBuffer.wrap(b);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		return bb.getInt();
	}
	
	public static int valueLengthBytesToInt(byte[] b) {
		return (int) b[0] + (((int) b[1]) * 256);
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
	
	public static String bytesToHexString(byte[] bytes){
		String string = "";
		if (bytes != null) {
			for (int i = 0; i < bytes.length; i++) {
				string += String.format("%02X", bytes[i]) + " \\ ";
			}
		}
        return string;
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
}
