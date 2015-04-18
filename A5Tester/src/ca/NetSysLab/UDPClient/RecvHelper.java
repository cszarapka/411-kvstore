package ca.NetSysLab.UDPClient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.util.Arrays;

import javax.xml.bind.DatatypeConverter;

import org.json.JSONException;
import org.json.JSONObject;

public class RecvHelper implements Runnable {
  static boolean DEBUG = true;	
  DatagramSocket clientSocket = null;
  JSONObject requestBuffer;
  
  public RecvHelper (DatagramSocket s){
    this.clientSocket = s;
    this.requestBuffer = new JSONObject();
  }
  
  @Override
  public void run() {
    while (true) {
      if(clientSocket.isClosed()) { // stop listening if socket has been closed
    	  break;
      }
      byte[] receiveData = new byte[16+1+2+32];
      Arrays.fill(receiveData, (byte)0);
      try {
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        this.clientSocket.receive(receivePacket);        
        if (DEBUG) System.out.println("RECV: " + DatatypeConverter.printHexBinary(receiveData));
      } catch (IOException e) { continue; }
      ByteBuffer bb = ByteBuffer.wrap(receiveData);
      try {
        this.requestBuffer.put(Long.toString(bb.getLong()), receiveData);
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
  }

  public byte[] request(long req) {
    long now = System.currentTimeMillis();
    while(System.currentTimeMillis() < (now + 5000)) {
      try {
        byte[] cats = (byte[]) requestBuffer.get(Long.toString(req));
        requestBuffer.remove(Long.toString(req));
        return cats;
      } catch (JSONException e) { continue; }
    }
    //byte[] blowUp = new byte[16+1+2+32];
    //Arrays.fill(blowUp, (byte)255);
    //return blowUp; 
    return Connector.REQ_TIMED_OUT.getBytes();
  }
}
