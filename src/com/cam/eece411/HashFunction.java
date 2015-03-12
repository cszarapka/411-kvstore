package com.cam.eece411;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * A representation of an MD5 hash function that can be used
 * to get the location in the DHT where a key should be placed.
 * @author cam
 *
 */
public class HashFunction {
	MessageDigest md;
	
	/**
	 * Constructs a a HashFunction object which can be used
	 * to generate the MD5 hash of a key
	 */
	public HashFunction() {
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns the hash of a 32 byte key
	 * @param key	the 32 byte key
	 * @return		an integer between 0 and 255
	 */
	public int hash(byte[] key) {
		return md.digest(key)[0];
	}
}
