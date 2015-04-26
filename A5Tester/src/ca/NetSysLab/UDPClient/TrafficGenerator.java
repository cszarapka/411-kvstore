package ca.NetSysLab.UDPClient;

public class TrafficGenerator {
	 
	/**
	 * Traffic generator - keeps sending put requests to a server node for a given duration without waiting for a response.
	 * @param sn
	 * @param seconds - time in seconds.
	 */
	static void putRqstOnlySameNode(ServerNode sn, long seconds) {
		long uid = 50000001;
		long key = 50000001;
		long val = 60000001;
		long count = 0;
		long startTime = System.currentTimeMillis();
		Connector c = new Connector(sn.getHostName(), sn.getPortNumber(), false);
		while (System.currentTimeMillis() <= (startTime + (seconds * 1000))) {			
			c.send(Builder.packValued(Long.toString(uid), (byte)1, Long.toString(key), Long.toString(val), false));
			System.out.print(".");			
			uid++;
		    key++;
		    val++;
		    count++;
		    
		    if(uid >= Long.MAX_VALUE || key >= Long.MAX_VALUE || val >= Long.MAX_VALUE || count >= Long.MAX_VALUE) {
		    	System.err.println("Reached the limit. Aborting ...");
		    	c.close();
		    	return;
		    }
		}
		c.close();
		System.out.println("Total " + count + " PUT requests sent.");
	}
}
