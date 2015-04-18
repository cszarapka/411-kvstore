package ca.NetSysLab.UDPClient;

public class Tests {
//------------TESTS------------------
  public static void testStore(String a, int p) {
    String uID1 = Long.toString(System.currentTimeMillis());
    String uID2 = Long.toString(System.currentTimeMillis()+10001);
    String key = "cat1";
    String val = "meow1";
    byte[] recv;

    Connector c = new Connector(a, p);
    
    // put
    recv = c.sendAndRecieve(Builder.packValued(uID1, (byte)1, key, val, false));
    Checks.chkValueless(recv, uID1, (byte)0);

    // get the same thing
    recv = c.sendAndRecieve(Builder.packValueless(uID2, (byte)2, key));
    Checks.chkValued(recv, uID2, (byte)0, val);
  }


  public static void testRemove(String a, int p) {
    String uID1 = Long.toString(System.currentTimeMillis());
    String uID2 = Long.toString(System.currentTimeMillis()+10001);
    String uID3= Long.toString(System.currentTimeMillis()+20002);
    String key = "cat2";
    String val = "meow2";
    byte[] recv;
    
    Connector c = new Connector(a, p);
    
    // put
    recv = c.sendAndRecieve(Builder.packValued(uID1, (byte)1, key, val, false));
    Checks.chkValueless(recv, uID1, (byte)0);

    // remove
    recv = c.sendAndRecieve(Builder.packValueless(uID2, (byte)3, key));
    Checks.chkValueless(recv, uID2, (byte)0);

    // try to get the same thing -- should not exist
    recv = c.sendAndRecieve(Builder.packValueless(uID3, (byte)2, key));
    Checks.chkValueless(recv, uID3, (byte)1);
  }


  public static void testReplace(String a, int p) {
    String uID1 = Long.toString(System.currentTimeMillis());
    String uID2 = Long.toString(System.currentTimeMillis()+10001);
    String uID3= Long.toString(System.currentTimeMillis()+20002);
    String key = "cat3";
    String val = "meow3";
    String val2 = "scratch3";
    byte[] recv;

    Connector c = new Connector(a, p);
    
    // put
    recv = c.sendAndRecieve(Builder.packValued(uID1, (byte)1, key, val, false));
    Checks.chkValueless(recv, uID1, (byte)0);

    // put2
    recv = c.sendAndRecieve(Builder.packValued(uID2, (byte)1, key, val2, false));
    Checks.chkValueless(recv, uID2, (byte)0);

    // try to get the same thing -- should be val2
    recv = c.sendAndRecieve(Builder.packValueless(uID3, (byte)2, key));
    Checks.chkValued(recv, uID3, (byte)0, val2);
  }


  public static void testLossRemove(String a, int p) {
    String uID1 = Long.toString(System.currentTimeMillis());
    String uID2 = Long.toString(System.currentTimeMillis()+10001);
    String uID3= Long.toString(System.currentTimeMillis()+20002);
    String key = "cat4";
    String val = "meow4";
    byte[] recv;
    
    Connector c = new Connector(a, p);

    // put
    recv = c.sendAndRecieve(Builder.packValued(uID1, (byte)1, key, val, false));
    Checks.chkValueless(recv, uID1, (byte)0);

    // remove
    recv = c.sendAndRecieve(Builder.packValueless(uID2, (byte)3, key));
    Checks.chkValueless(recv, uID2, (byte)0);
    
    // remove 2, same uID
    recv = c.sendAndRecieve(Builder.packValueless(uID2, (byte)3, key));
    Checks.chkValueless(recv, uID2, (byte)0);

    // try to get the same thing -- should not exist
    recv = c.sendAndRecieve(Builder.packValueless(uID3, (byte)2, key));
    Checks.chkValueless(recv, uID3, (byte)1);
  }


  public static void testConcurrencyPut(String a, int p) {
    String uID1 = Long.toString(System.currentTimeMillis());
    String uID2 = Long.toString(System.currentTimeMillis()+10001);
    String uID3= Long.toString(System.currentTimeMillis()+20002);
    String key = "cat5";
    String val1 = "meow5";
    String val2 = "scratch5";
    byte[] recv;

    Connector c = new Connector(a, p);

    // put1
    recv = c.sendAndRecieve(Builder.packValued(uID1, (byte)1, key, val1, false));
    Checks.chkValueless(recv, uID1, (byte)0);

    // put2
    recv = c.sendAndRecieve(Builder.packValued(uID2, (byte)1, key, val2, false));
    Checks.chkValueless(recv, uID2, (byte)0);

    // put3 - pretending not to see response on 1
    recv = c.sendAndRecieve(Builder.packValued(uID1, (byte)1, key, val1, false));
    Checks.chkValueless(recv, uID1, (byte)0);

    // get -- should be val2
    recv = c.sendAndRecieve(Builder.packValueless(uID3, (byte)2, key));
    Checks.chkValued(recv, uID3, (byte)0, val2);
  }

    
  public static void testCross(String a, String a2, int p, int p2, long sleepy) {
    int uid = (int)(Math.random()*1000) + (int)System.currentTimeMillis();
    String key = "cat"+Double.toString(Math.random());
    String val = "meow"+Double.toString(Math.random());
    byte[] recv;

    Connector c = new Connector(a, p);
    Connector c2 = new Connector(a2, p2);

    // put server 1
    recv = c.sendAndRecieve(Builder.packValued(Integer.toString(uid), (byte)1, key, val, false));
    Checks.chkValueless(recv, Integer.toString(uid), (byte)0);

    try {
      Thread.sleep(sleepy);
    } catch (InterruptedException e) {}

    uid++;
    // get the same thing, from server 2
    recv = c2.sendAndRecieve(Builder.packValueless(Integer.toString(uid), (byte)2, key));
    Checks.chkValued(recv, Integer.toString(uid), (byte)0, val);
    
    uid++;
    // del the same thing, from server 2
    recv = c2.sendAndRecieve(Builder.packValueless(Integer.toString(uid), (byte)3, key));
    Checks.chkValueless(recv, Integer.toString(uid), (byte)0);

    try {
      Thread.sleep(sleepy);
    } catch (InterruptedException e) {}

    uid++;
    // get the same thing from server 1, should not exist
    recv = c.sendAndRecieve(Builder.packValueless(Integer.toString(uid), (byte)2, key));
    Checks.chkValueless(recv, Integer.toString(uid), (byte)1);
  }
  
  
  public static void testNodeFail(String a, int p, String a2, int p2, String a3, int p3) {
    int uid = 5000;
    int key = 5000;
    int val = 6000;
    byte[] recv;
    int errors = 0;
    int quantity = 100;

    Connector c = new Connector(a, p);
    Connector c2 = new Connector(a2, p2);
    Connector c3 = new Connector(a3, p3);

    long start = System.currentTimeMillis();
    for (int i = 0; i < quantity; i++) {
      // put
      recv = c.sendAndRecieve(Builder.packValued(Integer.toString(uid), (byte)1, Integer.toString(key), Integer.toString(val), false));
      if (!Checks.chkValuelessB(recv, Integer.toString(uid), (byte)0)) {
        errors++;
      }
      System.out.print(".");
      uid++;
      key++;
      val++;
    }
    long stop = System.currentTimeMillis();
    System.out.println("\nPut errors: " + errors + " / "+quantity+". Took: " + (stop - start));
    if (errors != quantity) System.out.println(Long.toString(((stop - start))/((long)(quantity-errors))) + " msec/req.");

    // crash c2 -- command 0x04
    recv = c2.sendAndRecieve(Builder.packValueless(Integer.toString(uid), (byte)4, Integer.toString(key)));
    Checks.chkValueless(recv, Integer.toString(uid), (byte)0);
    uid++;
    errors = 0;
    key = 5000;
    val = 6000;

    start = System.currentTimeMillis();
    for (int i = 0; i < quantity; i++) {
      // get the same thing
      recv = c3.sendAndRecieve(Builder.packValueless(Integer.toString(uid), (byte)2, Integer.toString(key)));
      if (!Checks.chkValuedB(recv, Integer.toString(uid), (byte)0, Integer.toString(val))) {
        errors++;
      }
      System.out.print(".");
      uid++;
      key++;
      val++;
    }
    stop = System.currentTimeMillis();
    System.out.println("\nGet errors: " + errors + " / "+quantity+". Took: " + (stop - start));
    if (errors != quantity) System.out.println(Long.toString(((stop - start))/((long)(quantity-errors))) + " msec/req.");

    errors = 0;
    key = 5000;
    val = 6000;

    // replace all keys
    start = System.currentTimeMillis();
    for (int i = 0; i < quantity; i++) {
      // put
      recv = c3.sendAndRecieve(Builder.packValued(Integer.toString(uid), (byte)1, Integer.toString(key), Integer.toString(val), false));
      if (!Checks.chkValuelessB(recv, Integer.toString(uid), (byte)0)) {
        errors++;
      }
      System.out.print(".");
      uid++;
      key++;
      val++;
    }
    stop = System.currentTimeMillis();
    System.out.println("\nPut errors: " + errors + " / "+quantity+". Took: " + (stop - start));
    if (errors != quantity) System.out.println(Long.toString(((stop - start))/((long)(quantity-errors))) + " msec/req.");

    errors = 0;
    key = 5000;
    val = 6000;
    
    start = System.currentTimeMillis();
    for (int i = 0; i < quantity; i++) {
      // get the same thing
      recv = c2.sendAndRecieve(Builder.packValueless(Integer.toString(uid), (byte)2, Integer.toString(key)));
      if (!Checks.chkValuedB(recv, Integer.toString(uid), (byte)0, Integer.toString(val))) {
        errors++;
      }
      System.out.print(".");
      uid++;
      key++;
      val++;
    }
    stop = System.currentTimeMillis();
    System.out.println("\nGet errors: " + errors + " / "+quantity+". Took: " + (stop - start));
    if (errors != quantity) System.out.println(Long.toString(((stop - start))/((long)(quantity-errors))) + " msec/req.");

  }
}
