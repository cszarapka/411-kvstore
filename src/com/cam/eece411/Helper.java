package com.cam.eece411;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

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
}
