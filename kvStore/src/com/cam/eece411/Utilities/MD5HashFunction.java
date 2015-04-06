package com.cam.eece411.Utilities;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * A representation of an MD5 hash function that can be used
 * to get the location in the DHT where a key should be placed.
 * @author cam
 *
 */
public class MD5HashFunction {
	// Obligatory constructor
	public MD5HashFunction() {}

	/**
	 * Returns the hash of a 32 byte key
	 * @param key	the 32 byte key
	 * @return		an integer between 0 and 255
	 * @throws 		NoSuchAlgorithmException 
	 */
	public static int hash(byte[] key) {
		try {
			//add 128 because byte to int returns -128 to 127
			return MessageDigest.getInstance("MD5").digest(key)[0] + 128;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
}
