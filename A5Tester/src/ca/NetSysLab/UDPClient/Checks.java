package ca.NetSysLab.UDPClient;

import java.util.Arrays;

import javax.xml.bind.DatatypeConverter;

public class Checks {
  public static void chkValueless(byte[] recv, String uID, byte cmd) {
    boolean fail = false;
    System.out.print("Checking uID:\t");
    for (int i = 0; i < 16 && i < uID.length(); i++) {
      if (recv[i] != uID.getBytes()[i]) {
        System.out.print("[FAIL]");
        System.out.print("\n\tExp: " + DatatypeConverter.printHexBinary(uID.getBytes()));
        System.out.print("\n\tGot: " + DatatypeConverter.printHexBinary(recv));
        fail = true;
        break;
      }
    }
    if (!fail) { System.out.print("[OK]"); }
    fail = false;

    System.out.print("\nChecking cmd:\t");
    if (recv[16] != cmd) {
      System.out.print("[FAIL]");
      System.out.print("\n\tExp: " + Byte.toString(cmd));
      System.out.print("\n\tGot: " + Byte.toString(recv[16]));
    } else {
      System.out.print("[OK]");
    }
    System.out.println();
  }
  
  public static boolean chkValuelessB(byte[] recv, String uID, byte cmd) {
    for (int i = 0; i < 16 && i < uID.length(); i++) {
      if (recv[i] != uID.getBytes()[i]) {
        return false;
      }
    }
    if (recv[16] != cmd) {
      return false;
    }
    return true;
  }
  
  public static boolean chkValuelessC(byte[] recv, String uID, byte cmd) {
    boolean fail = false;
    System.out.print("Checking uID:\t");
    for (int i = 0; i < 16 && i < uID.length(); i++) {
      if (recv[i] != uID.getBytes()[i]) {
        System.out.print("[FAIL]");
        System.out.print("\n\tExp: " + DatatypeConverter.printHexBinary(uID.getBytes()));
        System.out.print("\n\tGot: " + DatatypeConverter.printHexBinary(recv));
        fail = true;
        break;
      }
    }
    if (!fail) { System.out.print("[OK]"); }
    fail = false;

    System.out.print("\nChecking cmd:\t");
    if (recv[16] != cmd) {
      System.out.print("[FAIL]");      
      System.out.print("\n\tExp: " + Byte.toString(cmd));
      System.out.print("\n\tGot: " + Byte.toString(recv[16]));
      //fail = false;
    } else {
      System.out.print("[OK]");
    }
    System.out.println();
    return fail;
  }
  
  public static void chkValued(byte[] recv, String uID, byte cmd, String val) {
    boolean fail = false;
    System.out.print("Checking uID:\t");
    for (int i = 0; i < 16 && i < uID.length(); i++) {
      if (recv[i] != uID.getBytes()[i]) {
        System.out.print("[FAIL]");
        System.out.print("\n\tExp: " + DatatypeConverter.printHexBinary(uID.getBytes()));
        System.out.print("\n\tGot: " + DatatypeConverter.printHexBinary(recv));
        fail = true;
        break;
      }
    }
    if (!fail) { System.out.print("[OK]"); }
    fail = false;

    System.out.print("\nChecking cmd:\t");
    if (recv[16] != cmd) {
      System.out.print("[FAIL]");
      System.out.print("\n\tExp: " + Byte.toString(cmd));
      System.out.print("\n\tGot: " + Byte.toString(recv[16]));
    } else {
      System.out.print("[OK]");
    }
    System.out.print("\nChecking v len:\t");
    if (recv[17] != val.length()) {
      System.out.print("[FAIL]");
      System.out.print("\n\tExp: " + val.length());
      System.out.print("\n\tGot: " + Byte.toString(recv[17]));
    } else {
      System.out.print("[OK]");
    }
    System.out.print("\nChecking val:\t");
    for (int i = 0; i < 32 && i < val.length(); i++) {
      if (recv[19+i] != val.getBytes()[i]) {
        System.out.print("[FAIL]");
        System.out.print("\n\tExp: " + DatatypeConverter.printHexBinary(val.getBytes()));
        System.out.print("\n\tGot: " + DatatypeConverter.printHexBinary(recv));
        fail = true;
        break;
      }
    }
    if (!fail) { System.out.print("[OK]"); }
    System.out.println();
  }
  
  public static boolean chkValuedB(byte[] recv, String uID, byte cmd, String val) {
    for (int i = 0; i < 16  && i < uID.length(); i++) {
      if (recv[i] != uID.getBytes()[i]) {
        return false;
      }
    }
    if (recv[16] != cmd) {
      return false;
    }
    
    for (int i = 0; i < val.length(); i++) {
      if (recv[i+19] != val.getBytes()[i]) {
        return false;
      }
    }
    return true;
  }
  
  /**
   * Check is request was timed out.
   * @param recv 
   * @return - true if timed out, false otherwise. 
   */
  public static boolean checkTimedOut(byte[] recv) {	
	return Arrays.equals(recv, Connector.REQ_TIMED_OUT.getBytes());
  }
}
