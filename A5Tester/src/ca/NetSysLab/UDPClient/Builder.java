package ca.NetSysLab.UDPClient;

public class Builder {
  public static byte[] packValueless(String uID, byte cmd, String key) {
    byte[] pack = new byte[16+1+32];
    
    // uID
    for (int i = 0; i < 16 && i < uID.length(); i++) {
      pack[i] = uID.getBytes()[i];
    } 

    // Cmd
    pack[16] = cmd;

    // Key
    for (int i = 0; i < 32 && i < key.length(); i++) {
      pack[16+1+i] = key.getBytes()[i];
    }
    return pack;
  }

  public static byte[] packValued(String uID, byte cmd, String key, String val, boolean bad) {
    byte[] pack = new byte[16+1+32+2+32];

    // uID
    for (int i = 0; i < 16  && i < uID.length(); i++) {
      pack[i] = uID.getBytes()[i];
    } 

    // Cmd
    pack[16] = cmd;

    // Key
    for (int i = 0; i < 32 && i < key.length(); i++) {
      pack[16+1+i] = key.getBytes()[i];
    }

    if (!bad) {
      // Val len
      pack[16+1+32] = (byte)val.length();
      pack[16+1+32+1] = (byte)0;
    } else {
      pack[16+1+32] = (byte)255;
      pack[16+1+32+1] = (byte)255;
    }

    // Val
    for (int i = 0; i < val.length(); i++) {
      pack[16+1+32+2+i] = val.getBytes()[i];
    }

    return pack;
  }
}
