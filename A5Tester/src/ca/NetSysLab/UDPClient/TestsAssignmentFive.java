package ca.NetSysLab.UDPClient;

import java.util.ArrayList;
import java.util.Random;

public class TestsAssignmentFive {
	
	// private static classes
	private static class ClientStat {
		
		public long minTimeErr;
		public long maxTimeErr;
		public long avgTimeErr;
		public long stdevTimeErr;
		
		public long minTimeSucc;
		public long maxTimeSucc;
		public long avgTimeSucc;
		public long stdevTimeSucc;
		
		public long numOfMsgs;
		public long numOfErrMsgs;
		public long numOfSuccMsgs;
		public long numOfTimedOutMsgs;
							
		public String errorStatStringPut;
		public String errorStatStringGet;
		public String errorStatStringRemove;
		
		public String successStatStringPut;
		public String successStatStringGet;
		public String successStatStringRemove;
		
		public double errorPercentagePut;
		public double errorPercentageGet;
		public double errorPercentageRemove;
		
		public double successPercentagePut;
		public double successPercentageGet;
		public double successPercentageRemove;
		
		public double timedOutPercentagePut;
		public double timedOutPercentageGet;
		public double timedOutPercentageRemove;
	}
	
	public static class KeyStatus {
		public static final int SUCCESS = 0;
		public static final int TIMED_OUT = 1;
		public static final int ERROR = 2; 
		public static final int REMOVED = 3;
		
		public int key;
		public int value;
		public int status;
	}
	
	// static variables
	private static KeyStatus[] keyStatuses; // used by the tests that send fixed number of requests
	private static Object[] clientsKeyStatuses; // used by the tests that send requests for a fixed duration
	
	private static ClientStat[] clientStats;
	private static long startTime;
	
	// static methods		
	static Object[] initializeKeyValuePairs(int numberOfClients) {
		Object[] ck = new Object[numberOfClients];
		for (int i = 0; i < numberOfClients; i++) {
			ck[i] = new ArrayList<KeyStatus>();
		}
		return ck;
	}
	
	static KeyStatus[] generateUniqueKeyValuePairs(int numberOfCleints, int numberOfMsgsPerClient) {
		int totalNumberOfKeys = numberOfCleints * numberOfMsgsPerClient;
		KeyStatus[] ks = new KeyStatus[totalNumberOfKeys];
		for (int i = 0; i < totalNumberOfKeys; i++) {
			ks[i] = new KeyStatus();
			ks[i].key = i;
			ks[i].value = i;
			ks[i].status = KeyStatus.TIMED_OUT;
		}
		return ks;
	}
	
	static void createSocketConnections(ArrayList<ServerNode> serverNodes) {
		for (int i = 0; i < serverNodes.size(); i++) { 
	    	ServerNode sn = serverNodes.get(i);	    	
	    	sn.setConnector(new Connector(sn.getHostName(), sn.getPortNumber()));
	    }
	}
	
	static void createSocketConnections(ArrayList<ServerNode> serverNodes, int clientPortNumberSeed) {
		for (int i = 0; i < serverNodes.size(); i++) { 
	    	ServerNode sn = serverNodes.get(i);	    	
	    	sn.setConnector(new Connector(sn.getHostName(), sn.getPortNumber(), clientPortNumberSeed + i));
	    }
	}
	
	static void closeSocketConnections(ArrayList<ServerNode> serverNodes) {
		for (int i = 0; i < serverNodes.size(); i++) { 
	    	serverNodes.get(i).getConnector().close();    		    	
	    }
	}
		
	static ArrayList<ServerNode> getCloneServerNodes(ArrayList<ServerNode> serverNodes) {
		ArrayList<ServerNode> cloneServerNodes = new ArrayList<ServerNode>();
		for (int i = 0; i < serverNodes.size(); i++) {
			ServerNode sn = serverNodes.get(i);
			cloneServerNodes.add(new ServerNode(sn.getHostName(), sn.getPortNumber()));			
		}
		return cloneServerNodes;
	}
	
	static int getRandomInteger(int min, int max) {
		Random rand = new Random();
		return rand.nextInt((max - min) + 1) + min;
	}
	
	static long getMinLong(long[] data) {
		if(data.length < 1) {
			return 0;
		}
		long min = data[0];
		for (int i = 0; i < data.length; i++) {
			if (data[i] <= min) {
				min = data[i];
			}
		} 
		return min;
	}
	
	static long getMaxLong(long[] data) {
		if(data.length < 1) {
			return 0;
		}
		long max = data[0];
		for (int i = 0; i < data.length; i++) {
			if (data[i] >= max) {
				max = data[i];
			}
		} 
		return max;
	}
	
	static long getAverageLong(long[] data) {
		if(data.length < 1) {
			return 0;
		}
		double sum = 0;
		for (int i = 0; i < data.length; i++) {
			sum += (double)data[i];
		}
		return (long)Math.ceil(sum / (double)data.length);
	}
	
	static long getStandardDeviationLong(long[] data) {
		if(data.length < 1) {
			return 0;
		}
		double sum = 0;
		for (int i = 0; i < data.length; i++) {
			sum += (double)data[i];
		}
		double mean = sum / (double)data.length;
		sum = 0;
		for (int i = 0; i < data.length; i++) {
			sum += Math.pow(((double)data[i] - mean), 2);
		}
		return (long)Math.ceil(Math.sqrt((sum / (double)data.length)));
	}
	
	static double getErrorPercentageDouble(int error, int total) { 
		return (error == 0 && total == 0) ? 0 : (error * 100) / (double)(total);
	}
	
	static double getSuccessPercentageDouble(int success, int total) {
		return (success == 0 && total == 0) ? 0 : (success * 100) / (double)(total);
	}
	
	static double getTimedOutPercentageDouble(int timedOut, int total) {
		return (timedOut == 0 && total == 0) ? 0 : (timedOut * 100) / (double)(total);
	}
	
	static long[] toPrimitiveArrayLong(Object[] data) {
		long[] primitiveArray = new long[data.length];
		for (int i = 0; i < data.length; i++) {
			primitiveArray[i] = (Long)data[i]; // autoboxing
		}
		return primitiveArray;
	}
	
	static void pause(long seconds) {
		try {
	        Thread.sleep(seconds * 1000);
	    } catch (InterruptedException e) {
	    	e.printStackTrace();
	    }
	}
	
	static void printMessage(String str) {
		System.out.println("\n[ "+ str + " ]");
	}
	
	static String getClientTimeStatString(int clientID, String commandName, long min, long max, long avg, long stdev) {
		return clientID + "," + commandName + 
		       avg + "," + min + "," + max + "," + stdev; 
	}
	
	static String getClientTimeStatStringPrettyPrint(int clientID, String commandName, long min, long max, long avg, long stdev) {
		return "Client #" + clientID + " Command: " + commandName + 
		       " [avg] " + avg + "ms [min] " + min + "ms [max] " + max + "ms [stdev] " + stdev + "ms"; 
	}
	
	static void printTimeStat(int clientID, String commandName, long min, long max, long avg, long stdev) {
		System.out.println("Client #" + clientID + " Command: " + commandName + 
				           " [avg] " + avg + "ms [min] " + min + "ms [max] " + max + "ms [stdev] " + stdev + "ms"); 
	}
	
    //===========================================================================================//
	
	// Test cases		 
	/**
	 * Test: Response-time - random nodes. A client sends fixed number of requests.
	 * @param serverNodes - list of server nodes
	 * @param quantity - number of key-value pairs
	 */
	static void testPutGetRemovePerRndNodes(ArrayList<ServerNode> serverNodes, int quantity, int clientPortNumber, int clientID) {
		printMessage("Test: Response-time - random nodes. A client sends fixed number of requests");
		
		keyStatuses = generateUniqueKeyValuePairs(1, quantity);
		
		clientStats = new ClientStat[1];
		
		for (int i = 0; i < clientStats.length; i++) {			
			clientStats[i] = new ClientStat();
		}
		
		//createSocketConnections(serverNodes, clientPortNumber);
		testPutGetRemovePerRndNodesCore(serverNodes, quantity, clientPortNumber, clientID);
		//closeSocketConnections(serverNodes);
		
		System.out.println("");
		for (int i = 0; i < clientStats.length; i++) {
			System.out.println(clientStats[i].successStatStringPut);
			System.out.println(clientStats[i].errorStatStringPut);
			System.out.println("Put error: " + clientStats[i].errorPercentagePut + "%");
			System.out.println("Put success: " + clientStats[i].successPercentagePut + "%");
			System.out.println("Put timedOut: " + clientStats[i].timedOutPercentagePut + "%");
			System.out.println(clientStats[i].successStatStringGet);
			System.out.println(clientStats[i].errorStatStringGet);
			System.out.println("Get error: " + clientStats[i].errorPercentageGet + "%");
			System.out.println("Get success: " + clientStats[i].successPercentageGet + "%");
			System.out.println("Get timedOut: " + clientStats[i].timedOutPercentageGet + "%");
			System.out.println(clientStats[i].successStatStringRemove);
			System.out.println(clientStats[i].errorStatStringRemove);
			System.out.println("Remove error: " + clientStats[i].errorPercentageRemove + "%");
			System.out.println("Remove success: " + clientStats[i].successPercentageRemove + "%");
			System.out.println("Remove timedOut: " + clientStats[i].timedOutPercentageRemove + "%");
		}
			
		//keyStatuses = null;
		//clientStats = null;
	}
	
	private static void testPutGetRemovePerRndNodesCore(ArrayList<ServerNode> serverNodes, int quantity, int clientPortNumber, int clientID) {
		byte[] recv;
	    
	    long start = 0;
	    long stop = 0;
	    
	    long sendTime = 0;
	    long recvTime = 0;
	    
	    ArrayList<Long> errorTimes = new ArrayList<Long>();
	    ArrayList<Long> successTimes = new ArrayList<Long>();
	    
	    long[] errTimes;
	    long[] scsTimes;
	    	    
	    String commandName = "";
	    	    	    
	    int uid = 1;
	    
	    // put requests to random nodes	  
	    commandName = "PUT";
	    
	    int errors = 0;
	    int successes = 0;
	    int timedOut = 0;
	    
	    int key = 0;
	    int val = 0;
	    int status = 0;
	    
	    int clinetKeyIndex = clientID * quantity;
	        	       
	    start = System.currentTimeMillis();
		for (int i = 0; i < quantity; i++) {
			// pick a random node 
			int randomInterger = getRandomInteger(0, serverNodes.size() - 1);
			ServerNode sn = serverNodes.get(randomInterger);						
			//Connector c = sn.getConnector();
			Connector c = new Connector(sn.getHostName(), sn.getPortNumber());
			
			uid = keyStatuses[clinetKeyIndex].key;
			key = keyStatuses[clinetKeyIndex].key;
			val = keyStatuses[clinetKeyIndex].value;
						
			sendTime = System.currentTimeMillis();
			recv = c.sendAndRecieve(Builder.packValued(Integer.toString(uid), (byte)1, Integer.toString(key), Integer.toString(val), false), Connector.MAX_RETRIES);
			recvTime = System.currentTimeMillis();
			if (Checks.checkTimedOut(recv)) {
				timedOut++;
				keyStatuses[clinetKeyIndex].status = KeyStatus.TIMED_OUT;
			}
			else if (!Checks.chkValuelessB(recv, Integer.toString(uid), (byte)0)) {
		    	errors++;
		        errorTimes.add(recvTime - sendTime);
		        keyStatuses[clinetKeyIndex].status = KeyStatus.ERROR;
	        } else {
	    	    successes++;
	    	    successTimes.add(recvTime - sendTime);
	    	    keyStatuses[clinetKeyIndex].status = KeyStatus.SUCCESS;
	        }
		    //System.out.print(".");
			
			//uid++;
		    //key++;
		    //val++;
			clinetKeyIndex++;
		    
		    c.close();
		}
		stop = System.currentTimeMillis();
		//System.out.println("\nClinet #" + clientID + " Put errors: " + errors + " / "+quantity+". Took: " + (stop - start) + " msec");
	    //if (errors != quantity) System.out.println("Client #" + clientID + " " + Long.toString(((stop - start))/((long)(quantity-errors))) + " msec/req.");	    
	    
	    //System.out.println("Clinet #" + clientID + " Command: " + commandName + " Error (%) " + getErrorPercentageDouble(successes, errors));
	    clientStats[clientID].errorPercentagePut = getErrorPercentageDouble(errors, successes + errors + timedOut);
	    clientStats[clientID].successPercentagePut = getSuccessPercentageDouble(successes, successes + errors + timedOut);
	    clientStats[clientID].timedOutPercentagePut = getTimedOutPercentageDouble(timedOut, successes + errors + timedOut);
	    	    
	    scsTimes = toPrimitiveArrayLong(successTimes.toArray());
	    //printTimeStat(clientID, commandName + " (success)", getMinLong(scsTimes), getMaxLong(scsTimes), 
	    //		getAverageLong(scsTimes), getStandardDeviationLong(scsTimes));
	    clientStats[clientID].successStatStringPut = getClientTimeStatString(clientID, commandName + ",success,", getMinLong(scsTimes), getMaxLong(scsTimes), 
	    		getAverageLong(scsTimes), getStandardDeviationLong(scsTimes));
	    
	    errTimes = toPrimitiveArrayLong(errorTimes.toArray());
	    //printTimeStat(clientID, commandName  + " (error)", getMinLong(errTimes), getMaxLong(errTimes), 
	    //		getAverageLong(errTimes), getStandardDeviationLong(errTimes));
	    clientStats[clientID].errorStatStringPut = getClientTimeStatString(clientID, commandName  + ",error,", getMinLong(errTimes), getMaxLong(errTimes), 
	    		getAverageLong(errTimes), getStandardDeviationLong(errTimes));
	    
	    pause(60);	    
	    
	    // get requests to the same node
	    commandName = "GET";
	    	    
	    errorTimes.clear();
	    successTimes.clear();
	    	    
	    errors = 0;	    
	    successes = 0;
	    timedOut = 0;
	    
	    key = 0;
	    val = 0;
	    status = 0;
	    
	    clinetKeyIndex = clientID * quantity;
	    
	    start = System.currentTimeMillis();
	    for (int i = 0; i < quantity; i++) {	    	
	    	// pick a random node 
			int randomInterger = getRandomInteger(0, serverNodes.size() - 1);
			ServerNode sn = serverNodes.get(randomInterger);						
			//Connector c = sn.getConnector();
			Connector c = new Connector(sn.getHostName(), sn.getPortNumber());
			
			uid = keyStatuses[clinetKeyIndex].key;
			key = keyStatuses[clinetKeyIndex].key;
			val = keyStatuses[clinetKeyIndex].value;
			status = keyStatuses[clinetKeyIndex].status;
			
	    	sendTime = System.currentTimeMillis();
	    	recv = c.sendAndRecieve(Builder.packValueless(Integer.toString(uid), (byte)2, Integer.toString(key)), Connector.MAX_RETRIES);
	    	//Checks.chkValued(recv, Integer.toString(uid), (byte)0, Integer.toString(val));
	    	recvTime = System.currentTimeMillis();
	    	if (Checks.checkTimedOut(recv)) {
				timedOut++;
			} else {
	    		if (status == KeyStatus.SUCCESS) {
	    			if (!Checks.chkValuedB(recv, Integer.toString(uid), (byte)0, Integer.toString(val))) {
	    	    		errors++;
	    	    		errorTimes.add(recvTime - sendTime);
	    	    	} else {
	    		    	successes++;
	    		    	successTimes.add(recvTime - sendTime);
	    		    }
	    		} else if (status == KeyStatus.TIMED_OUT) {
	    			if (Checks.chkValuedB(recv, Integer.toString(uid), (byte)0, Integer.toString(val)) ||
	    				Checks.chkValuelessB(recv, Integer.toString(uid), (byte)1)) {
	    				successes++;
	    				successTimes.add(recvTime - sendTime);
	    			} else {
	    				errors++;
	    	    		errorTimes.add(recvTime - sendTime);
	    			}
	    		} else if (status == KeyStatus.ERROR) {
	    			if (!Checks.chkValuedB(recv, Integer.toString(uid), (byte)0, Integer.toString(val)) &&
	    				Checks.chkValuelessB(recv, Integer.toString(uid), (byte)1)) {	    	    		
	    	    		successes++;
	    		    	successTimes.add(recvTime - sendTime);
	    	    	} else {
	    	    		errors++;
	    	    		errorTimes.add(recvTime - sendTime);
	    		    }
	    		} else {
	    			System.err.println("Invalid key status. ");
		    		System.err.println("Aborting test case ... ");	  
		    		c.close();
		    		break;
	    		}
	    	}
	    	
			/*else if (!Checks.chkValuedB(recv, Integer.toString(uid), (byte)0, Integer.toString(val))) {
	    		errors++;
	    		errorTimes.add(recvTime - sendTime);
	    	} else {
		    	successes++;
		    	successTimes.add(recvTime - sendTime);
		    }*/
	    	//System.out.print(".");
	    	
	    	//uid++;
	    	//key++;
	    	//val++;	   
	    	clinetKeyIndex++;
	    	
	    	c.close();
	    }
	    stop = System.currentTimeMillis();
	    //System.out.println("\nClient #" + clientID + " Get errors: " + errors + " / "+quantity+". Took: " + (stop - start) + " msec");
	    //if (errors != quantity) System.out.println("Client #" + clientID + " " + Long.toString(((stop - start))/((long)(quantity-errors))) + " msec/req.");
	    
	    //System.out.println("Clinet #" + clientID + " Command: " + commandName + " Error (%) " + getErrorPercentageDouble(successes, errors));
	    clientStats[clientID].errorPercentageGet = getErrorPercentageDouble(errors, successes + errors + timedOut);
	    clientStats[clientID].successPercentageGet = getSuccessPercentageDouble(successes, successes + errors + timedOut);
	    clientStats[clientID].timedOutPercentageGet = getTimedOutPercentageDouble(timedOut, successes + errors + timedOut);
	    
	    scsTimes = toPrimitiveArrayLong(successTimes.toArray());
	    //printTimeStat(clientID, commandName + " (success)", getMinLong(scsTimes), getMaxLong(scsTimes), 
	    //		getAverageLong(scsTimes), getStandardDeviationLong(scsTimes));
	    clientStats[clientID].successStatStringGet = getClientTimeStatString(clientID, commandName + ",success,", getMinLong(scsTimes), getMaxLong(scsTimes), 
	    		getAverageLong(scsTimes), getStandardDeviationLong(scsTimes));
	    
	    errTimes = toPrimitiveArrayLong(errorTimes.toArray());
	    //printTimeStat(clientID, commandName  + " (error)", getMinLong(errTimes), getMaxLong(errTimes), 
	    //		getAverageLong(errTimes), getStandardDeviationLong(errTimes));
	    clientStats[clientID].errorStatStringGet = getClientTimeStatString(clientID, commandName  + ",error,", getMinLong(errTimes), getMaxLong(errTimes), 
	    		getAverageLong(errTimes), getStandardDeviationLong(errTimes)); 
	    
	    pause(60);
	     
	    // remove requests to the same node
	    commandName = "REMOVE";
	    
	    errorTimes.clear();
	    successTimes.clear();
	   
	    errors = 0;	  
	    successes = 0;
	    timedOut = 0;
	    
	    key = 0;
	    val = 0;
	    status = 0;
	    
	    clinetKeyIndex = clientID * quantity;
	    
	    start = System.currentTimeMillis();
	    for (int i = 0; i < quantity; i++) {
	    	// pick a random node 
			int randomInterger = getRandomInteger(0, serverNodes.size() - 1);
			ServerNode sn = serverNodes.get(randomInterger);						
			//Connector c = sn.getConnector();			
			Connector c = new Connector(sn.getHostName(), sn.getPortNumber());
			
			uid = keyStatuses[clinetKeyIndex].key;
			key = keyStatuses[clinetKeyIndex].key;
			//val = keyStatuses[clinetKeyIndex].value;
			status = keyStatuses[clinetKeyIndex].status;
				    	
	    	sendTime = System.currentTimeMillis();
	    	recv = c.sendAndRecieve(Builder.packValueless(Integer.toString(uid), (byte)3, Integer.toString(key)), Connector.MAX_RETRIES);
	    	recvTime = System.currentTimeMillis();
	    	if (Checks.checkTimedOut(recv)) {
				timedOut++;
			} else {
				if (status == KeyStatus.SUCCESS) {
					if (!Checks.chkValuelessB(recv, Integer.toString(uid), (byte)0)) {
			    		errors++;
			    		errorTimes.add(recvTime - sendTime);
			    	} else {
				    	successes++;
				    	successTimes.add(recvTime - sendTime);
				    }
				} else if (status == KeyStatus.TIMED_OUT) {
					if (Checks.chkValuelessB(recv, Integer.toString(uid), (byte)0) ||
						Checks.chkValuelessB(recv, Integer.toString(uid), (byte)1)) {			    		
			    		successes++;
				    	successTimes.add(recvTime - sendTime);
			    	} else {
			    		errors++;
			    		errorTimes.add(recvTime - sendTime);
				    }
				} else if (status == KeyStatus.ERROR) {
					if (!Checks.chkValuelessB(recv, Integer.toString(uid), (byte)0) &&
						Checks.chkValuelessB(recv, Integer.toString(uid), (byte)1)) {			    		
			    		successes++;
				    	successTimes.add(recvTime - sendTime);
			    	} else {
			    		errors++;
			    		errorTimes.add(recvTime - sendTime);
				    }
				} else {
	    			System.err.println("Invalid key status. ");
		    		System.err.println("Aborting test case ... ");	  
		    		c.close();
		    		break;
	    		}
			}	
	    	
			/*else if (!Checks.chkValuelessB(recv, Integer.toString(uid), (byte)0)) {
	    		errors++;
	    		errorTimes.add(recvTime - sendTime);
	    	} else {
		    	successes++;
		    	successTimes.add(recvTime - sendTime);
		    }*/
	    	//System.out.print(".");
	    	
	    	//uid++;
	    	//key++;
	    	//val++;
	    	clinetKeyIndex++;
	    	
	    	c.close();
	    }
	    stop = System.currentTimeMillis();
	    //System.out.println("\nClient #" + clientID + " " + " Remove errors: " + errors + " / "+quantity+". Took: " + (stop - start) + " msec");
	    //if (errors != quantity) System.out.println("Client #" + clientID + " " + Long.toString(((stop - start))/((long)(quantity-errors))) + " msec/req.");
	    
	    //System.out.println("Clinet #" + clientID + " Command: " + commandName + " Error (%) " + getErrorPercentageDouble(successes, errors));
	    clientStats[clientID].errorPercentageRemove = getErrorPercentageDouble(errors, successes + errors + timedOut);
	    clientStats[clientID].successPercentageRemove = getSuccessPercentageDouble(successes, successes + errors + timedOut);
	    clientStats[clientID].timedOutPercentageRemove = getTimedOutPercentageDouble(timedOut, successes + errors + timedOut);
	    
	    scsTimes = toPrimitiveArrayLong(successTimes.toArray());
	    //printTimeStat(clientID, commandName + " (success)", getMinLong(scsTimes), getMaxLong(scsTimes), 
	    //		getAverageLong(scsTimes), getStandardDeviationLong(scsTimes));
	    clientStats[clientID].successStatStringRemove = getClientTimeStatString(clientID, commandName + ",success,", getMinLong(scsTimes), getMaxLong(scsTimes), 
	    		getAverageLong(scsTimes), getStandardDeviationLong(scsTimes));
	    
	    errTimes = toPrimitiveArrayLong(errorTimes.toArray());
	    //printTimeStat(clientID, commandName  + " (error)", getMinLong(errTimes), getMaxLong(errTimes), 
	    //		getAverageLong(errTimes), getStandardDeviationLong(errTimes));
	    clientStats[clientID].errorStatStringRemove = getClientTimeStatString(clientID, commandName  + ",error,", getMinLong(errTimes), getMaxLong(errTimes), 
	    		getAverageLong(errTimes), getStandardDeviationLong(errTimes));
	}
	
	/**
	 * Test: A client sends requests to random nodes for a fixed duration.
	 * @param sn
	 * @param seconds
	 * @param clientPortNumber
	 * @param clientID
	 */
	private static void testPutGetRemoveMultipleNodeFixedDurationCore(ArrayList<ServerNode> serverNodes, long seconds, int clientPortNumber, int clientID, String command) {
		byte[] recv;
		
		int successes = 0;
		int errors = 0;
		int timedOut = 0;
		
		long sendTime = 0;
	    long recvTime = 0;
	    
	    ArrayList<Long> errorTimes = new ArrayList<Long>();
	    ArrayList<Long> successTimes = new ArrayList<Long>();
	    
	    long[] errTimes;
	    long[] scsTimes;
			    	 	    	    	    
	    int uid = 1;     
	    
	    // assuming a minimum 50ms per request latency, at most 20 requests can be sent every second 	    
	    int quantity = (int)seconds * 20; // maximum number of requests to be sent
	    	    
	    int key = clientID * quantity;
	    int val = clientID * quantity;
	    int status = KeyStatus.TIMED_OUT;
	    
	    if (key >= Integer.MAX_VALUE || val >= Integer.MAX_VALUE) {
	    	System.err.println("Reached the limit. Aborting ...");
	    	return;
	    }
	    
	    ArrayList<KeyStatus> ck = (ArrayList<KeyStatus>)clientsKeyStatuses[clientID];
	    
	    if ((command.equalsIgnoreCase("GET") || command.equalsIgnoreCase("REMOVE")) && ck.size() < 1) {
	    	System.err.println("Key list is empty. Aborting ...");
	    	return;
	    }
	    
	    int clinetKeyIndex = 0;
	    		    
	    while (System.currentTimeMillis() <= (startTime + (seconds * 1000))) {
	    	    		
	    	// pick a random node 
			int randomInterger = getRandomInteger(0, serverNodes.size() - 1);
			ServerNode sn = serverNodes.get(randomInterger);						
			//Connector c = sn.getConnector();
			Connector c = new Connector(sn.getHostName(), sn.getPortNumber());
				    	
	    	if (command.equalsIgnoreCase("PUT")) {	   

		    	if (ck.size() >= quantity) {
		    		System.err.println("Reached the limit. Aborting ...");
		    		break;
		    	}	    	

				KeyStatus ks = new KeyStatus();
				ks.key = key;
				ks.value = val;
				uid = key;
	    		
	    		sendTime = System.currentTimeMillis();
	    		recv = c.sendAndRecieve(Builder.packValued(Integer.toString(uid), (byte)1, Integer.toString(key), Integer.toString(val), false));			
	    		recvTime = System.currentTimeMillis();
	    		if (Checks.checkTimedOut(recv)) {
					timedOut++;
					ks.status = KeyStatus.TIMED_OUT;
	    		} else if (!Checks.chkValuelessB(recv, Integer.toString(uid), (byte)0)) {
	    			errors++;
	    			errorTimes.add(recvTime - sendTime);
	    			ks.status = KeyStatus.ERROR;
	    		} else {
	    			successes++;
	    			successTimes.add(recvTime - sendTime);
	    			ks.status = KeyStatus.SUCCESS;
	    		}
	    		
	    		ck.add(ks);
	    		
	    		key++;
	 		    val++;	 		    
	 		    
	    	} else if (command.equalsIgnoreCase("GET")) {
	    		
	    		if (clinetKeyIndex > (ck.size() - 1)) {
	    			c.close();
	    			break;
	    		}
	    		
	    		key = ck.get(clinetKeyIndex).key;
	    		val = ck.get(clinetKeyIndex).value;
	    		status = ck.get(clinetKeyIndex).status;
	    		uid = key;
	    		
	    		sendTime = System.currentTimeMillis();
	    		recv = c.sendAndRecieve(Builder.packValueless(Integer.toString(uid), (byte)2, Integer.toString(key)));		    	
	    		recvTime = System.currentTimeMillis();
	    		if (Checks.checkTimedOut(recv)) {
					timedOut++;
	    		} else {
		    		if (status == KeyStatus.SUCCESS) {
		    			if (!Checks.chkValuedB(recv, Integer.toString(uid), (byte)0, Integer.toString(val))) {
		    	    		errors++;
		    	    		errorTimes.add(recvTime - sendTime);
		    	    	} else {
		    		    	successes++;
		    		    	successTimes.add(recvTime - sendTime);
		    		    }
		    		} else if (status == KeyStatus.TIMED_OUT) {
		    			if (Checks.chkValuedB(recv, Integer.toString(uid), (byte)0, Integer.toString(val)) ||
		    				Checks.chkValuelessB(recv, Integer.toString(uid), (byte)1)) {
		    				successes++;
		    				successTimes.add(recvTime - sendTime);
		    			} else {
		    				errors++;
		    	    		errorTimes.add(recvTime - sendTime);
		    			}
		    		} else if (status == KeyStatus.ERROR) {
		    			if (!Checks.chkValuedB(recv, Integer.toString(uid), (byte)0, Integer.toString(val)) &&
		    				Checks.chkValuelessB(recv, Integer.toString(uid), (byte)1)) {	    	    		
		    	    		successes++;
		    		    	successTimes.add(recvTime - sendTime);
		    	    	} else {
		    	    		errors++;
		    	    		errorTimes.add(recvTime - sendTime);
		    		    }
		    		} else {
		    			System.err.println("Invalid key status. ");
			    		System.err.println("Aborting test case ... ");	  
			    		c.close();
			    		break;
		    		}
		    	}
	    		
	    		/*else if (!Checks.chkValuedB(recv, Integer.toString(uid), (byte)0, Integer.toString(val))) {
		    		errors++;
		    		errorTimes.add(recvTime - sendTime);
		    	} else {
	    			successes++;
	    			successTimes.add(recvTime - sendTime);
	    		}*/
	    	} else if (command.equalsIgnoreCase("REMOVE")) {
	    		
	    		if (clinetKeyIndex > (ck.size() - 1)) {
	    			c.close();
	    			break;
	    		}
	    		
	    		key = ck.get(clinetKeyIndex).key;
	    		//val = ck.get(clinetKeyIndex).value;
	    		status = ck.get(clinetKeyIndex).status;
	    		uid = key;
	    		
	    		sendTime = System.currentTimeMillis();
	    		recv = c.sendAndRecieve(Builder.packValueless(Integer.toString(uid), (byte)3, Integer.toString(key)));		    	
	    		recvTime = System.currentTimeMillis();
	    		if (Checks.checkTimedOut(recv)) {
					timedOut++;
	    		} else {
					if (status == KeyStatus.SUCCESS) {
						if (!Checks.chkValuelessB(recv, Integer.toString(uid), (byte)0)) {
				    		errors++;
				    		errorTimes.add(recvTime - sendTime);
				    	} else {
					    	successes++;
					    	successTimes.add(recvTime - sendTime);
					    }
					} else if (status == KeyStatus.TIMED_OUT) {
						if (Checks.chkValuelessB(recv, Integer.toString(uid), (byte)0) ||
							Checks.chkValuelessB(recv, Integer.toString(uid), (byte)1)) {			    		
				    		successes++;
					    	successTimes.add(recvTime - sendTime);
				    	} else {
				    		errors++;
				    		errorTimes.add(recvTime - sendTime);
					    }
					} else if (status == KeyStatus.ERROR) {
						if (!Checks.chkValuelessB(recv, Integer.toString(uid), (byte)0) &&
							Checks.chkValuelessB(recv, Integer.toString(uid), (byte)1)) {			    		
				    		successes++;
					    	successTimes.add(recvTime - sendTime);
				    	} else {
				    		errors++;
				    		errorTimes.add(recvTime - sendTime);
					    }
					} else {
		    			System.err.println("Invalid key status. ");
			    		System.err.println("Aborting test case ... ");	  
			    		c.close();
			    		break;
		    		}
	    		}
	    		/*else if (!Checks.chkValuelessB(recv, Integer.toString(uid), (byte)0)) {
		    		errors++;
		    		errorTimes.add(recvTime - sendTime);
		    	} else {
	    			successes++;
	    			successTimes.add(recvTime - sendTime);
	    		}*/
	    	} else {
	    		System.err.println("Invalid command. ");
	    		System.err.println("Aborting test case ... ");	  
	    		c.close();
	    		break;
	    	}		    
	    	//System.out.print(".");
			//uid++;
		    //key++;
		    //val++;	
			clinetKeyIndex++;
		    
		    c.close();
		}
	    	    
	    pause(60);
	    clientStats[clientID].numOfSuccMsgs = successes;
	    clientStats[clientID].numOfErrMsgs = errors;	
	    clientStats[clientID].numOfTimedOutMsgs = timedOut;
	    clientStats[clientID].numOfMsgs = successes + errors + timedOut;
	    
	    errTimes = toPrimitiveArrayLong(errorTimes.toArray());	   
	    clientStats[clientID].minTimeErr = getMinLong(errTimes); 
	    clientStats[clientID].maxTimeErr = getMaxLong(errTimes);
	    clientStats[clientID].avgTimeErr = getAverageLong(errTimes);
	    clientStats[clientID].stdevTimeErr = getStandardDeviationLong(errTimes);
	    		
	    scsTimes = toPrimitiveArrayLong(successTimes.toArray());	    
	    clientStats[clientID].minTimeSucc = getMinLong(scsTimes);
	    clientStats[clientID].maxTimeSucc = getMaxLong(scsTimes);
	    clientStats[clientID].avgTimeSucc = getAverageLong(scsTimes);
	    clientStats[clientID].stdevTimeSucc = getStandardDeviationLong(scsTimes);
	}
		
	/**
	 * Test: Response-time - same node. A client sends fixed number of requests.
	 * @param sn
	 * @param quantity
	 */
	static void testPutGetRemovePerfSameNode(ServerNode sn, int quantity, int clientPortNumber, int clientID) {
		printMessage("Test: Response-time - same node. A client sends fixed number of requests");
		clientStats = new ClientStat[1];
		
		for (int i = 0; i < clientStats.length; i++) {			
			clientStats[i] = new ClientStat();
		}
		
		testPutGetRemovePerfSameNodeCore(sn, quantity, clientPortNumber, clientID);
		
		System.out.println("");
		for (int i = 0; i < clientStats.length; i++) {
			System.out.println(clientStats[i].successStatStringPut);
			System.out.println(clientStats[i].errorStatStringPut);
			System.out.println("Put error: " + clientStats[i].errorPercentagePut + "%");
			System.out.println("Put success: " + clientStats[i].successPercentagePut + "%");
			System.out.println(clientStats[i].successStatStringGet);
			System.out.println(clientStats[i].errorStatStringGet);
			System.out.println("Get error: " + clientStats[i].errorPercentageGet + "%");
			System.out.println("Get success: " + clientStats[i].successPercentageGet + "%");
			System.out.println(clientStats[i].successStatStringRemove);
			System.out.println(clientStats[i].errorStatStringRemove);
			System.out.println("Remove error: " + clientStats[i].errorPercentageRemove + "%");
			System.out.println("Remove success: " + clientStats[i].successPercentageRemove + "%");
		}
	}
	
	private static void testPutGetRemovePerfSameNodeCore(ServerNode sn, int quantity, int clientPortNumber, int clientID) {
        byte[] recv; 
    
	    long start = 0;
	    long stop = 0;
	    	    
	    long sendTime = 0;
	    long recvTime = 0;
	   
	    ArrayList<Long> errorTimes = new ArrayList<Long>();
	    ArrayList<Long> successTimes = new ArrayList<Long>();
	    
	    long[] errTimes;
	    long[] scsTimes;
	    	    
	    String commandName = "";
	    	    	    
	    int uid = 1;
	    
	    // put requests to the same nodes	  
	    commandName = "PUT";
	    
	    int errors = 0;	    
	    int successes = 0;
	    
	    int key = 2;
	    int val = 3;
	    
	    Connector c = new Connector(sn.getHostName(), sn.getPortNumber(), clientPortNumber);
	    	    
	    start = System.currentTimeMillis();
		for (int i = 0; i < quantity; i++) {					
								
			sendTime = System.currentTimeMillis();
			recv = c.sendAndRecieve(Builder.packValued(Integer.toString(uid)+clientPortNumber, (byte)1, Integer.toString(key)+clientPortNumber, Integer.toString(val)+clientPortNumber, false));
			//Checks.chkValueless(recv, Integer.toString(uid), (byte)0);
			recvTime = System.currentTimeMillis();						
		    if (!Checks.chkValuelessB(recv, Integer.toString(uid)+clientPortNumber, (byte)0)) {
		    	errors++;
		    	errorTimes.add(recvTime - sendTime);
		    } else {
		    	successes++;
		    	successTimes.add(recvTime - sendTime);
		    }
		    //System.out.print(".");
			
			uid++;
		    key++;
		    val++;		    
		}
		stop = System.currentTimeMillis();
	    //System.out.println("\nClinet #" + clientID + " Put errors: " + errors + " / "+quantity+". Took: " + (stop - start) + " msec");
	    //if (errors != quantity) System.out.println("Client #" + clientID + " " + Long.toString(((stop - start))/((long)(quantity-errors))) + " msec/req.");	    
	    
	    //System.out.println("Clinet #" + clientID + " Command: " + commandName + " Error (%) " + getErrorPercentageDouble(successes, errors));
		clientStats[clientID].errorPercentagePut = getErrorPercentageDouble(successes, errors);
		clientStats[clientID].successPercentagePut = getSuccessPercentageDouble(successes, errors);
		
	    scsTimes = toPrimitiveArrayLong(successTimes.toArray());
	    //printTimeStat(clientID, commandName + " (success)", getMinLong(scsTimes), getMaxLong(scsTimes), 
	    //		getAverageLong(scsTimes), getStandardDeviationLong(scsTimes));
	    clientStats[clientID].successStatStringPut = getClientTimeStatString(clientID, commandName + " (success)", getMinLong(scsTimes), getMaxLong(scsTimes), 
	    		getAverageLong(scsTimes), getStandardDeviationLong(scsTimes));

	    errTimes = toPrimitiveArrayLong(errorTimes.toArray());
	    //printTimeStat(clientID, commandName  + " (error)", getMinLong(errTimes), getMaxLong(errTimes), 
	    //		getAverageLong(errTimes), getStandardDeviationLong(errTimes));
	    clientStats[clientID].errorStatStringPut = getClientTimeStatString(clientID, commandName  + " (error)", getMinLong(errTimes), getMaxLong(errTimes), 
	    		getAverageLong(errTimes), getStandardDeviationLong(errTimes));
	    
	    pause(60);
	    
	    // get requests to the same node
	    commandName = "GET";
	    	    
	    errorTimes.clear();
	    successTimes.clear();
	    	    
	    errors = 0;	    
	    successes = 0;
	    
	    key = 2;
	    val = 3;
	    
	    start = System.currentTimeMillis();
	    for (int i = 0; i < quantity; i++) {	    	    	
	    		    	
	    	sendTime = System.currentTimeMillis();
	    	recv = c.sendAndRecieve(Builder.packValueless(Integer.toString(uid)+clientPortNumber, (byte)2, Integer.toString(key)+clientPortNumber));
	    	//Checks.chkValued(recv, Integer.toString(uid), (byte)0, Integer.toString(val));
	    	recvTime = System.currentTimeMillis();
	    	if (!Checks.chkValuedB(recv, Integer.toString(uid)+clientPortNumber, (byte)0, Integer.toString(val))) {
	    		errors++;
	    		errorTimes.add(recvTime - sendTime);
	    	} else {
		    	successes++;
		    	successTimes.add(recvTime - sendTime);
		    }
	    	//System.out.print(".");
	    	
	    	uid++;
	    	key++;
	    	val++;	    		    	
	    }
	    stop = System.currentTimeMillis();
	    //System.out.println("\nClent #" + clientID + " Get errors: " + errors + " / "+quantity+". Took: " + (stop - start) + " msec");
	    //if (errors != quantity) System.out.println("Client #" + clientID + " " + Long.toString(((stop - start))/((long)(quantity-errors))) + " msec/req.");
	    
	    //System.out.println("Clinet #" + clientID + " Command: " + commandName + " Error (%) " + getErrorPercentageDouble(successes, errors));
	    clientStats[clientID].errorPercentageGet = getErrorPercentageDouble(successes, errors);
	    clientStats[clientID].successPercentageGet = getSuccessPercentageDouble(successes, errors);
	    
	    scsTimes = toPrimitiveArrayLong(successTimes.toArray());
	    //printTimeStat(clientID, commandName + " (success)", getMinLong(scsTimes), getMaxLong(scsTimes), 
	    //		getAverageLong(scsTimes), getStandardDeviationLong(scsTimes));	    
	    clientStats[clientID].successStatStringGet = getClientTimeStatString(clientID, commandName + " (success)", getMinLong(scsTimes), getMaxLong(scsTimes), 
	    		getAverageLong(scsTimes), getStandardDeviationLong(scsTimes));
	    
	    errTimes = toPrimitiveArrayLong(errorTimes.toArray());
	    //printTimeStat(clientID, commandName  + " (error)", getMinLong(errTimes), getMaxLong(errTimes), 
	    //		getAverageLong(errTimes), getStandardDeviationLong(errTimes));	    
	    clientStats[clientID].errorStatStringGet = getClientTimeStatString(clientID, commandName  + " (error)", getMinLong(errTimes), getMaxLong(errTimes), 
	    		getAverageLong(errTimes), getStandardDeviationLong(errTimes));
	    
	    pause(60);
	     
	    // remove requests to the same node
	    commandName = "REMOVE";
	    
	    errorTimes.clear();
	    successTimes.clear();
	   
	    errors = 0;	  
	    successes = 0;
	    
	    key = 2;
	    val = 3;
	    
	    start = System.currentTimeMillis();
	    for (int i = 0; i < quantity; i++) {	    	
	    		    	
	    	sendTime = System.currentTimeMillis();
	    	recv = c.sendAndRecieve(Builder.packValueless(Integer.toString(uid)+clientPortNumber, (byte)3, Integer.toString(key)+clientPortNumber));
	    	recvTime = System.currentTimeMillis();
	    	if (!Checks.chkValuelessB(recv, Integer.toString(uid)+clientPortNumber, (byte)0)) {
	    		errors++;
	    		errorTimes.add(recvTime - sendTime);
	    	} else {
		    	successes++;
		    	successTimes.add(recvTime - sendTime);
		    }
	    	//System.out.print(".");
	    	
	    	uid++;
	    	key++;
	    	val++;    	
	    }
	    stop = System.currentTimeMillis();
	    //System.out.println("\nClinet #" + clientID + " " + " Remove errors: " + errors + " / "+quantity+". Took: " + (stop - start) + " msec");
	    //if (errors != quantity) System.out.println("\nClinet #" + clientID + " " + Long.toString(((stop - start))/((long)(quantity-errors))) + " msec/req.");
	    
	    //System.out.println("\nClinet #" + clientID + " Command: " + commandName + " Error (%) " + getErrorPercentageDouble(successes, errors));
	    clientStats[clientID].errorPercentageRemove = getErrorPercentageDouble(successes, errors);
	    clientStats[clientID].successPercentageRemove = getSuccessPercentageDouble(successes, errors);
	    
	    scsTimes = toPrimitiveArrayLong(successTimes.toArray());
	    //printTimeStat(clientID, commandName + " (success)", getMinLong(scsTimes), getMaxLong(scsTimes), 
	    //		getAverageLong(scsTimes), getStandardDeviationLong(scsTimes));
	    clientStats[clientID].successStatStringRemove = getClientTimeStatString(clientID, commandName + " (success)", getMinLong(scsTimes), getMaxLong(scsTimes), 
	    		getAverageLong(scsTimes), getStandardDeviationLong(scsTimes));
	    
	    errTimes = toPrimitiveArrayLong(errorTimes.toArray());
	    //printTimeStat(clientID, commandName  + " (error)", getMinLong(errTimes), getMaxLong(errTimes), 
	    //		getAverageLong(errTimes), getStandardDeviationLong(errTimes));
	    clientStats[clientID].errorStatStringRemove = getClientTimeStatString(clientID, commandName  + " (error)", getMinLong(errTimes), getMaxLong(errTimes), 
	    		getAverageLong(errTimes), getStandardDeviationLong(errTimes));
	    
	    c.close();
	}
	
	/**
	 * Test: A client sends requests for a fixed duration.
	 * @param sn
	 * @param seconds
	 * @param clientPortNumber
	 * @param clientID
	 */
	private static void testPutGetRemoveSameNodeFixedDurationCore(ServerNode sn, long seconds, int clientPortNumber, int clientID, String command) {
		byte[] recv;
		
		int successes = 0;
		int errors = 0;
		
		long sendTime = 0;
	    long recvTime = 0;
	    
	    ArrayList<Long> errorTimes = new ArrayList<Long>();
	    ArrayList<Long> successTimes = new ArrayList<Long>();
	    
	    long[] errTimes;
	    long[] scsTimes;
			    	 	    	    	    
	    int uid = 1;     
	    
	    int key = 2;
	    int val = 3;
	    
	    Connector c = new Connector(sn.getHostName(), sn.getPortNumber(), clientPortNumber);
	    
	    while (System.currentTimeMillis() <= (startTime + (seconds * 1000))) {			
	    	
	    	if (command.equalsIgnoreCase("PUT")) {
	    		sendTime = System.currentTimeMillis();
	    		recv = c.sendAndRecieve(Builder.packValued(Integer.toString(uid)+clientPortNumber, (byte)1, Integer.toString(key)+clientPortNumber, Integer.toString(val)+clientPortNumber, false));			
	    		recvTime = System.currentTimeMillis();
	    		if (!Checks.chkValuelessB(recv, Integer.toString(uid)+clientPortNumber, (byte)0)) {
	    			errors++;
	    			errorTimes.add(recvTime - sendTime);
	    		} else {
	    			successes++;
	    			successTimes.add(recvTime - sendTime);
	    		}
	    	} else if (command.equalsIgnoreCase("GET")) {
	    		sendTime = System.currentTimeMillis();
	    		recv = c.sendAndRecieve(Builder.packValueless(Integer.toString(uid)+clientPortNumber, (byte)2, Integer.toString(key)+clientPortNumber));		    	
	    		recvTime = System.currentTimeMillis();
	    		if (!Checks.chkValuedB(recv, Integer.toString(uid)+clientPortNumber, (byte)0, Integer.toString(val))) {
		    		errors++;
		    		errorTimes.add(recvTime - sendTime);
		    	} else {
	    			successes++;
	    			successTimes.add(recvTime - sendTime);
	    		}
	    	} else if (command.equalsIgnoreCase("REMOVE")) {
	    		sendTime = System.currentTimeMillis();
	    		recv = c.sendAndRecieve(Builder.packValueless(Integer.toString(uid)+clientPortNumber, (byte)3, Integer.toString(key)+clientPortNumber));		    	
	    		recvTime = System.currentTimeMillis();
	    		if (!Checks.chkValuelessB(recv, Integer.toString(uid)+clientPortNumber, (byte)0)) {
		    		errors++;
		    		errorTimes.add(recvTime - sendTime);
		    	} else {
	    			successes++;
	    			successTimes.add(recvTime - sendTime);
	    		}
	    	} else {
	    		System.err.println("Invalid command. ");
	    		System.err.println("Aborting test case ... ");	    
	    		break;
	    	}		    
	    	//System.out.print(".");
			uid++;
		    key++;
		    val++;		    
		}
	    	    
	    pause(60);
	    clientStats[clientID].numOfSuccMsgs = successes;
	    clientStats[clientID].numOfErrMsgs = errors;
	    clientStats[clientID].numOfMsgs = (successes + errors);
	    
	    errTimes = toPrimitiveArrayLong(errorTimes.toArray());	   
	    clientStats[clientID].minTimeErr = getMinLong(errTimes); 
	    clientStats[clientID].maxTimeErr = getMaxLong(errTimes);
	    clientStats[clientID].avgTimeErr = getAverageLong(errTimes);
	    clientStats[clientID].stdevTimeErr = getStandardDeviationLong(errTimes);
	    		
	    scsTimes = toPrimitiveArrayLong(successTimes.toArray());	    
	    clientStats[clientID].minTimeSucc = getMinLong(scsTimes);
	    clientStats[clientID].maxTimeSucc = getMaxLong(scsTimes);
	    clientStats[clientID].avgTimeSucc = getAverageLong(scsTimes);
	    clientStats[clientID].stdevTimeSucc = getStandardDeviationLong(scsTimes);
	    
	    c.close();
	}	
	
	/**
	 * Test: Throughput - single node, multiple clients. 
	 * All the clients send requests for a fixed duration.
	 * @param sn
	 * @param seconds
	 * @param clientPortNumberSeed
	 * @param numberOfClients
	 */
	static void testSameNodeMultipleClients(final ServerNode sn, final long seconds, final int clientPortNumberSeed, int numberOfClients, final String command) {
		printMessage("Test: Throughput - single node, multiple clients");
		clientStats = new ClientStat[numberOfClients];
		
		for (int i = 0; i < clientStats.length; i++) {			
			clientStats[i] = new ClientStat();
		}
		
		Thread[] threads = new Thread[numberOfClients];
		startTime = System.currentTimeMillis();
		for (int i = 0; i < numberOfClients; i++) {			
			final int clientID = i;
			threads[i] = new Thread() {				
				public void run(){					
					//System.out.println("Client #" + clientID);					
					testPutGetRemoveSameNodeFixedDurationCore(sn, seconds, (clientPortNumberSeed + clientID), clientID, command);
				}
			};
			threads[i].start();
		}
		
		for(Thread t : threads) { // wait for these tests to finish before starting the next batch
			try {
				t.join();
			} catch (InterruptedException e) {				
				e.printStackTrace();
			}
		}
		
		System.out.println("");
		long elapsedTime = System.currentTimeMillis() - startTime;	
		elapsedTime -= 60*1000; // subtract pause duration
		System.out.println("Total elapsed time: " + elapsedTime + "ms");
		
		long totalError = 0;
		long totalSuccess = 0;		
		long total = 0;
		
		long totalMinErr = 0;
		long totalMaxErr = 0;
		long totalAvgErr = 0;
		long totalStdevErr = 0;
		
		long totalMinSucc = 0;
		long totalMaxSucc = 0;
		long totalAvgSucc = 0;
		long totalStdevSucc = 0;
		
		for (int i = 0; i < clientStats.length; i++) {			
			totalError +=  clientStats[i].numOfErrMsgs;
			totalSuccess += clientStats[i].numOfSuccMsgs;
			total += clientStats[i].numOfMsgs;
			//System.out.println("Client #" + i + " Error " + clientStats[i].numOfErrMsgs);
			//System.out.println("Client #" + i + " Success " + clientStats[i].numOfSuccMsgs);
			//System.out.println("Client #" + i + " Total " + clientStats[i].numOfMsgs);
			
			totalMinErr += clientStats[i].minTimeErr;
			totalMaxErr += clientStats[i].maxTimeErr;
			totalAvgErr += clientStats[i].avgTimeErr;
			totalStdevErr += clientStats[i].stdevTimeErr;
			
			totalMinSucc += clientStats[i].minTimeSucc;
			totalMaxSucc += clientStats[i].maxTimeSucc;
			totalAvgSucc += clientStats[i].avgTimeSucc;
			totalStdevSucc += clientStats[i].stdevTimeSucc;
		}
		
		double throughputError = (double)totalError / ((double)elapsedTime / 1000);
		System.out.println("Command: " + command + " Throughput (error): " + throughputError + " reqs/s");		
		double throughputSuccess = (double)totalSuccess / ((double)elapsedTime / 1000);
		System.out.println("Command: " + command + " Throughput (success): " + throughputSuccess + " reqs/s");
		double throughputTotal = (double)total / ((double)elapsedTime / 1000);
		System.out.println("Command: " + command + " Throughput (total): " + throughputTotal + " reqs/s");
		
		System.out.println("Command: " + command + " (error)" + 
		        " [avg] " + ((double)totalAvgErr / clientStats.length) 
		        + "ms [min] " + ((double)totalMinErr / clientStats.length) 
		        + "ms [max] " + ((double)totalMaxErr / clientStats.length) 
		        + "ms [stdev] " + ((double)totalStdevErr / clientStats.length) + "ms"); 
		
		System.out.println("Command: " + command + " (success)" + 
		        " [avg] " + ((double)totalAvgSucc / clientStats.length) 
		        + "ms [min] " + ((double)totalMinSucc / clientStats.length) 
		        + "ms [max] " + ((double)totalMaxSucc / clientStats.length) 
		        + "ms [stdev] " + ((double)totalStdevSucc / clientStats.length) + "ms");		
	}
	
	/**
	 * Test: Response-time - single node, multiple clients, fixed number of requests.
	 * @param sn
	 * @param quantity
	 * @param numOfClients - number of clients.
	 */
	static void testSameNodeMultipleClients(final ServerNode sn, final int quantity, final int clientPortNumberSeed, int numberOfClients) {
		printMessage("Test: Response-time - single node, multiple clients, fixed number of reuests");
		clientStats = new ClientStat[numberOfClients];
		
		for (int i = 0; i < clientStats.length; i++) {			
			clientStats[i] = new ClientStat();
		}
		
		Thread[] threads = new Thread[numberOfClients];
		for (int i = 0; i < numberOfClients; i++) {			
			final int clientID = i;
			threads[i] = new Thread() {				
				public void run(){					
					//System.out.println("Client #" + clientID);
					testPutGetRemovePerfSameNodeCore(sn, quantity, (clientPortNumberSeed + clientID), clientID);
				}
			};
			threads[i].start();
		}
		
		for(Thread t : threads) { // wait for these tests to finish before starting the next batch
			try {
				t.join();
			} catch (InterruptedException e) {				
				e.printStackTrace();
			}
		}
		
		System.out.println("");
		for (int i = 0; i < clientStats.length; i++) {
			System.out.println(clientStats[i].successStatStringPut);
			System.out.println(clientStats[i].errorStatStringPut);
			System.out.println("Put error: " + clientStats[i].errorPercentagePut + "%");
			System.out.println("Put success: " + clientStats[i].successPercentagePut + "%");
			System.out.println(clientStats[i].successStatStringGet);
			System.out.println(clientStats[i].errorStatStringGet);
			System.out.println("Get error: " + clientStats[i].errorPercentageGet + "%");
			System.out.println("Get success: " + clientStats[i].successPercentageGet + "%");
			System.out.println(clientStats[i].successStatStringRemove);
			System.out.println(clientStats[i].errorStatStringRemove);
			System.out.println("Remove error: " + clientStats[i].errorPercentageRemove + "%");
			System.out.println("Remove success: " + clientStats[i].successPercentageRemove + "%");
		}
	}
	
	/**
	 * Test: Throughput - multiple nodes, multiple clients. 
	 * All the clients send requests for a fixed duration.
	 * @param sn
	 * @param seconds
	 * @param clientPortNumberSeed
	 * @param numberOfClients
	 */
	static void testMultipleNodesMultipleClients(final ArrayList<ServerNode> serverNodes, final long seconds, final int clientPortNumberSeed, int numberOfClients, final String command) {		
		printMessage("Test: Throughput - multiple nodes, multiple clients");
		//createSocketConnections(serverNodes, clientPortNumberSeed);
		
		if (command.equalsIgnoreCase("PUT")) {
			clientsKeyStatuses = initializeKeyValuePairs(numberOfClients);			
		}
		
		clientStats = new ClientStat[numberOfClients];
		
		for (int i = 0; i < clientStats.length; i++) {			
			clientStats[i] = new ClientStat();
		}
		
		Thread[] threads = new Thread[numberOfClients];
		startTime = System.currentTimeMillis();
		for (int i = 0; i < numberOfClients; i++) {			
			final int clientID = i;
			threads[i] = new Thread() {				
				public void run(){					
					//System.out.println("Client #" + clientID);					
					//testPutGetRemoveSameNodeFixedDuration(sn, seconds, (clientPortNumberSeed + clientID), clientID, command);
					
					// for dual Xeon servers
					// This is more expensive but port assignment is handled by native API.
					//ArrayList<ServerNode> cloneNodes = getCloneServerNodes(serverNodes);
					//createSocketConnections(cloneNodes);
					//testPutGetRemoveMultipleNodeFixedDurationCore(cloneNodes, seconds, (clientPortNumberSeed + clientID), clientID, command);
					//closeSocketConnections(cloneNodes);
					//cloneNodes.clear();
					
					// workaround for wimpy EC2 instances  
					testPutGetRemoveMultipleNodeFixedDurationCore(serverNodes, seconds, (clientPortNumberSeed + clientID), clientID, command);
				}
			};
			threads[i].start();
		}
		
		for(Thread t : threads) { // wait for these tests to finish before starting the next batch
			try {
				t.join();
			} catch (InterruptedException e) {				
				e.printStackTrace();
			}
		}
		//closeSocketConnections(serverNodes);
		
		// print number of requests sent by each client 
		/*if (command.equalsIgnoreCase("PUT")) {
			for (int i = 0; i < clientsKeyStatuses.length; i++) {
				ArrayList<KeyStatus> ck = (ArrayList<KeyStatus>)clientsKeyStatuses[i];
				System.out.println("Client #" + i + " #reqs " + ck.size());
				for (int j = 0; j < ck.size(); j++) {
					System.out.println(ck.get(j).key + " ... " + ck.get(j).value + " ... " + ck.get(j).status);
				}
			}		
		}*/
		
		System.out.println("");
		long elapsedTime = System.currentTimeMillis() - startTime;	
		elapsedTime -= 60*1000; // subtract pause duration
 		System.out.println("Total elapsed time: " + elapsedTime + "ms");
		
		long totalError = 0;
		long totalSuccess = 0;
		long totalTimedOut = 0;
		long total = 0;
		
		long totalMinErr = 0;
		long totalMaxErr = 0;
		long totalAvgErr = 0;
		long totalStdevErr = 0;
		
		long totalMinSucc = 0;
		long totalMaxSucc = 0;
		long totalAvgSucc = 0;
		long totalStdevSucc = 0;
		
		for (int i = 0; i < clientStats.length; i++) {			
			totalError +=  clientStats[i].numOfErrMsgs;
			totalSuccess += clientStats[i].numOfSuccMsgs;
			totalTimedOut += clientStats[i].numOfTimedOutMsgs;
			total += clientStats[i].numOfMsgs;
						
			totalMinErr += clientStats[i].minTimeErr;
			totalMaxErr += clientStats[i].maxTimeErr;
			totalAvgErr += clientStats[i].avgTimeErr;
			totalStdevErr += clientStats[i].stdevTimeErr;
			
			totalMinSucc += clientStats[i].minTimeSucc;
			totalMaxSucc += clientStats[i].maxTimeSucc;
			totalAvgSucc += clientStats[i].avgTimeSucc;
			totalStdevSucc += clientStats[i].stdevTimeSucc;			
		}
		
		double throughputError = (double)totalError / ((double)elapsedTime / 1000);
		System.out.println("Command: " + command + " Throughput (error): " + throughputError + " reqs/s");		
		double throughputSuccess = (double)totalSuccess / ((double)elapsedTime / 1000);
		System.out.println("Command: " + command + " Throughput (success): " + throughputSuccess + " reqs/s");		
		double throughputTimedOut = (double)totalTimedOut / ((double)elapsedTime / 1000);
		System.out.println("Command: " + command + " Throughput (timed out): " + throughputTimedOut + " reqs/s");
		double throughputTotal = (double)total / ((double)elapsedTime / 1000);
		System.out.println("Command: " + command + " Throughput (total): " + throughputTotal + " reqs/s");
		
		System.out.println("Command: " + command + " (error)" + 
		        " [avg] " + ((double)totalAvgErr / clientStats.length) 
		        + "ms [min] " + ((double)totalMinErr / clientStats.length) 
		        + "ms [max] " + ((double)totalMaxErr / clientStats.length) 
		        + "ms [stdev] " + ((double)totalStdevErr / clientStats.length) + "ms"); 
		
		System.out.println("Command: " + command + " (success)" + 
		        " [avg] " + ((double)totalAvgSucc / clientStats.length) 
		        + "ms [min] " + ((double)totalMinSucc / clientStats.length) 
		        + "ms [max] " + ((double)totalMaxSucc / clientStats.length) 
		        + "ms [stdev] " + ((double)totalStdevSucc / clientStats.length) + "ms");		
	}
	
	/**
	 * Test: Response-time - multiple nodes, multiple clients, fixed number of requests.
	 * @param serverNodes
	 * @param quantity
	 * @param numOfClients - number of clients.
	 */
	static void testMultipleNodesMultipleClients(final ArrayList<ServerNode> serverNodes, final int quantity, final int clientPortNumberSeed, int numberOfClients) {		
		if (numberOfClients == 1) printMessage("Test: Response-time - random nodes. A client sends fixed number of requests");
		else printMessage("Test: Response-time - multiple nodes, multiple clients, fixed number of requests");
		
		keyStatuses = generateUniqueKeyValuePairs(numberOfClients, quantity);
		
		clientStats = new ClientStat[numberOfClients];
		
		for (int i = 0; i < clientStats.length; i++) {			
			clientStats[i] = new ClientStat();
		}
		
		//createSocketConnections(serverNodes, clientPortNumberSeed);
		Thread[] threads = new Thread[numberOfClients];
		for (int i = 0; i < numberOfClients; i++) {						
			final int clientID = i;
			threads[i] = new Thread() {			
				public void run(){					
					//System.out.println("Client #" + clientID);
					//testPutGetRemovePerRndNodesCore(serverNodes, quantity, (clientPortNumberSeed + clientID), clientID);
					
					// for dual Xeon servers
					// This is more expensive but port assignment is handled by native API.
					//ArrayList<ServerNode> cloneNodes = getCloneServerNodes(serverNodes);
					//createSocketConnections(cloneNodes);
					//testPutGetRemovePerRndNodesCore(cloneNodes, quantity, (clientPortNumberSeed + clientID), clientID);
					//closeSocketConnections(cloneNodes);
					//cloneNodes.clear();
					
					// workaround for wimpy EC2 instances
					testPutGetRemovePerRndNodesCore(serverNodes, quantity, (clientPortNumberSeed + clientID), clientID);
				}
			};
			threads[i].start();
		}
		
		for(Thread t : threads) { // wait for these tests to finish before starting the next batch
			try {
				t.join();
			} catch (InterruptedException e) {				
				e.printStackTrace();
			}
		}
		//closeSocketConnections(serverNodes);
		
		System.out.println("");
		// prints per client stat
		/*for (int i = 0; i < clientStats.length; i++) {
			System.out.println(clientStats[i].successStatStringPut);
			System.out.println(clientStats[i].errorStatStringPut);
			System.out.println("Put error: " + clientStats[i].errorPercentagePut + "%");
			System.out.println("Put success: " + clientStats[i].successPercentagePut + "%");
			System.out.println(clientStats[i].successStatStringGet);
			System.out.println(clientStats[i].errorStatStringGet);
			System.out.println("Get error: " + clientStats[i].errorPercentageGet + "%");
			System.out.println("Get success: " + clientStats[i].successPercentageGet + "%");
			System.out.println(clientStats[i].successStatStringRemove);
			System.out.println(clientStats[i].errorStatStringRemove);
			System.out.println("Remove error: " + clientStats[i].errorPercentageRemove + "%");
			System.out.println("Remove success: " + clientStats[i].successPercentageRemove + "%");
		}*/
		
		// prints average
		String[] commands = {"PUT", "GET", "REMOVE"}; 		
		for (String command : commands) {					
			double errorPercentage = 0;
			double successPercentage = 0;
			double timedOutPercentage = 0;
			
			long totalMinErr = 0;
			long totalMaxErr = 0;
			long totalAvgErr = 0;
			long totalStdevErr = 0;
			
			long totalMinSucc = 0;
			long totalMaxSucc = 0;
			long totalAvgSucc = 0;
			long totalStdevSucc = 0;
			
			String err = "";
			String scs = "";
						
			for (int i = 0; i < clientStats.length; i++) {
				
				if (command.equalsIgnoreCase(commands[0])) {				
					err = clientStats[i].errorStatStringPut;
					scs = clientStats[i].successStatStringPut;
					
					errorPercentage += clientStats[i].errorPercentagePut;
					successPercentage += clientStats[i].successPercentagePut;
					timedOutPercentage += clientStats[i].timedOutPercentagePut;
					
				} else if (command.equalsIgnoreCase(commands[1])) {
					err = clientStats[i].errorStatStringGet;
					scs = clientStats[i].successStatStringGet;
					
					errorPercentage += clientStats[i].errorPercentageGet;
					successPercentage += clientStats[i].successPercentageGet;
					timedOutPercentage += clientStats[i].timedOutPercentageGet;
					
				} else if (command.equalsIgnoreCase(commands[2])) {
					err = clientStats[i].errorStatStringRemove;
					scs = clientStats[i].successStatStringRemove;
					
					errorPercentage += clientStats[i].errorPercentageRemove;
					successPercentage += clientStats[i].successPercentageRemove;
					timedOutPercentage += clientStats[i].timedOutPercentageRemove;
				}
							
				String[] e = err.split(",");
				String[] s = scs.split(",");
							
				totalAvgErr += Long.parseLong(e[3]);
				totalMinErr += Long.parseLong(e[4]);
				totalMaxErr += Long.parseLong(e[5]);
				totalStdevErr += Long.parseLong(e[6]);
				
				totalAvgSucc += Long.parseLong(s[3]);
				totalMinSucc += Long.parseLong(s[4]);
				totalMaxSucc += Long.parseLong(s[5]);
				totalStdevSucc += Long.parseLong(s[6]);		
			}
			
			System.out.println("Command: " + command + " (error)" + 
			        " [avg] " + ((double)totalAvgErr / clientStats.length) 
			        + "ms [min] " + ((double)totalMinErr / clientStats.length) 
			        + "ms [max] " + ((double)totalMaxErr / clientStats.length) 
			        + "ms [stdev] " + ((double)totalStdevErr / clientStats.length) + "ms"); 
			
			System.out.println("Command: " + command + " (success)" + 
			        " [avg] " + ((double)totalAvgSucc / clientStats.length) 
			        + "ms [min] " + ((double)totalMinSucc / clientStats.length) 
			        + "ms [max] " + ((double)totalMaxSucc / clientStats.length) 
			        + "ms [stdev] " + ((double)totalStdevSucc / clientStats.length) + "ms");
			
			System.out.println("Error: " + (errorPercentage / clientStats.length) + "%");
			System.out.println("Success: " + (successPercentage / clientStats.length) + "%");			
			System.out.println("Timed out: " + (timedOutPercentage / clientStats.length) + "%");			
		}		
	}
		
	/**
	 * Test: Replication - single-node failure.
	 * @param serverNodes - list of server nodes.
	 * @param quantity
	 */
	static void testSingleNodeFailure(ArrayList<ServerNode> serverNodes, int quantity) {	
		printMessage("Test: Replication - single-node failure");
		createSocketConnections(serverNodes);
		
		//ServerNode snOne = serverNodes.get(0); 
		ServerNode snTwo = serverNodes.get(1); 
		ServerNode snThree = serverNodes.get(2);
		
	    byte[] recv;
	    
	    long start = 0;
	    long stop = 0;
	    	    
	    int uid = 1;
    
	    // put requests to random nodes	  
	    int errors = 0;	    
	    int successes = 0;
	    
	    int key = 2;
	    int val = 3;
	    
	    start = System.currentTimeMillis();
		for (int i = 0; i < quantity; i++) {
			// pick a random node 
			int randomInterger = getRandomInteger(0, serverNodes.size() - 1);
			ServerNode sn = serverNodes.get(randomInterger);						
			Connector c = sn.getConnector();
						
			recv = c.sendAndRecieve(Builder.packValued(Integer.toString(uid), (byte)1, Integer.toString(key), Integer.toString(val), false));
		    if (!Checks.chkValuelessB(recv, Integer.toString(uid), (byte)0)) {
		    	errors++;
		    } else {
		    	successes++;
		    }
		    //System.out.print(".");
			
			uid++;
		    key++;
		    val++;
		}
		stop = System.currentTimeMillis();
	    System.out.println("\nPut errors: " + errors + " / "+quantity+". Took: " + (stop - start) + " msec");
	    if (errors != quantity) System.out.println(Long.toString(((stop - start))/((long)(quantity-errors))) + " msec/req.");
	    System.out.println("Put error: " + getErrorPercentageDouble(successes, errors) + "%");
	    
	    pause(30);
	    
	    // crash a single nodes	    
	    
	    // create a clone of the server node list
	    ArrayList<ServerNode> cloneServerNodes = new ArrayList<ServerNode>(serverNodes); 
	    //for (ServerNode sn : cloneServerNodes) {
	    //	System.out.println(sn.getHostName());
	    	//cloneServerNodes.add((new ServerNode(sn.getHostName(), sn.getPortNumber()))); 
	    //}   
	    
	    System.out.println("> Shutting down: " + snTwo.getHostName());
	    Connector cTwo = snTwo.getConnector();
	    recv = cTwo.sendAndRecieve(Builder.packValueless(Integer.toString(uid), (byte)4, Integer.toString(key)));
	    if (!Checks.chkValuelessC(recv, Integer.toString(uid), (byte)0)) { 
	    	cloneServerNodes.remove(snTwo);
	    } else {	    	 
	    	System.err.println("Error shutting down node: " + snTwo.getHostName());
    	}
	    
    	if (serverNodes.size() == cloneServerNodes.size()) {
    		System.err.println("Failed to shut down any node");
    		System.err.println("Aborting test case ... ");
    		cloneServerNodes.clear();
    	    closeSocketConnections(serverNodes);
    		return;
    	}
	    uid++;
	    	    
	    pause(30);
	    
	    // get requests to random "alive" nodes
	    errors = 0;
	    successes = 0;
	    
	    key = 2;
	    val = 3;
	    start = System.currentTimeMillis();
	    for (int i = 0; i < quantity; i++) {
	    	// pick a random node from the cloned list
	    	int randomInterger = getRandomInteger(0, cloneServerNodes.size() - 1);
	    	ServerNode sn = cloneServerNodes.get(randomInterger);
	    	Connector c = sn.getConnector();
	    	
	    	recv = c.sendAndRecieve(Builder.packValueless(Integer.toString(uid), (byte)2, Integer.toString(key)));
	    	if (!Checks.chkValuedB(recv, Integer.toString(uid), (byte)0, Integer.toString(val))) {
	    		errors++;
	    	} else {
	    		successes++;
	    	}
	    	//System.out.print(".");
	    	uid++;
	    	key++;
	    	val++;
	    }
	    stop = System.currentTimeMillis();
	    System.out.println("\nGet errors: " + errors + " / "+quantity+". Took: " + (stop - start) + " msec");
	    if (errors != quantity) System.out.println(Long.toString(((stop - start))/((long)(quantity-errors))) + " msec/req.");
	    System.out.println("Get error: " + getErrorPercentageDouble(successes, errors) + "%");
	    
	    pause(30);
	    
        // crash a single nodes	    
   
	    System.out.println("> Shutting down: " + snThree.getHostName());
	    Connector cThree = snThree.getConnector();
	    recv = cThree.sendAndRecieve(Builder.packValueless(Integer.toString(uid), (byte)4, Integer.toString(key)));
	    if (!Checks.chkValuelessC(recv, Integer.toString(uid), (byte)0)) { 
	    	cloneServerNodes.remove(snThree);
	    } else {	    	 
	    	System.err.println("Error shutting down node: " + snThree.getHostName());
    	}
	    
    	if (serverNodes.size() == cloneServerNodes.size()) {
    		System.err.println("Failed to shut down any node");
    		System.err.println("Aborting test case ... ");
    		cloneServerNodes.clear();
    	    closeSocketConnections(serverNodes);
    		return;
    	}
	    uid++;
	    	        
	    pause(30);
	    
	    // get requests to random "alive" nodes
	    errors = 0;
	    successes = 0;
	    
	    key = 2;
	    val = 3;
	    start = System.currentTimeMillis();
	    for (int i = 0; i < quantity; i++) {
	    	// pick a random node from the cloned list
	    	int randomInterger = getRandomInteger(0, cloneServerNodes.size() - 1);
	    	ServerNode sn = cloneServerNodes.get(randomInterger);
	    	Connector c = sn.getConnector();
	    	
	    	recv = c.sendAndRecieve(Builder.packValueless(Integer.toString(uid), (byte)2, Integer.toString(key)));
	    	if (!Checks.chkValuedB(recv, Integer.toString(uid), (byte)0, Integer.toString(val))) {
	    		errors++;
	    	} else {
	    		successes++;
	    	}
	    	//System.out.print(".");
	    	uid++;
	    	key++;
	    	val++;
	    }
	    stop = System.currentTimeMillis();
	    System.out.println("\nGet errors: " + errors + " / "+quantity+". Took: " + (stop - start) + " msec");
	    if (errors != quantity) System.out.println(Long.toString(((stop - start))/((long)(quantity-errors))) + " msec/req.");
	    System.out.println("Get error: " + getErrorPercentageDouble(successes, errors) + "%");
	    
	    pause(30);
	    
	    // remove requests to random "alive" nodes
	    errors = 0;
	    successes = 0;
	    
	    key = 2;
	    val = 3;
	    start = System.currentTimeMillis();
	    for (int i = 0; i < quantity; i++) {
	    	// pick a random node from the cloned list
	    	int randomInterger = getRandomInteger(0, cloneServerNodes.size() - 1);
	    	ServerNode sn = cloneServerNodes.get(randomInterger);
	    	Connector c = sn.getConnector();
	    		    	
	    	recv = c.sendAndRecieve(Builder.packValueless(Integer.toString(uid), (byte)3, Integer.toString(key)));
	    	if (!Checks.chkValuelessB(recv, Integer.toString(uid), (byte)0)) {
	    		errors++;
	    	} else {
	    		successes++;
	    	}
	    	//System.out.print(".");
	    	uid++;
	    	key++;
	    	val++;
	    }
	    stop = System.currentTimeMillis();
	    System.out.println("\nRemove errors: " + errors + " / "+quantity+". Took: " + (stop - start) + " msec");
	    if (errors != quantity) System.out.println(Long.toString(((stop - start))/((long)(quantity-errors))) + " msec/req.");
	    System.out.println("Remove error: " + getErrorPercentageDouble(successes, errors) + "%");
	    
	    cloneServerNodes.clear();
	    closeSocketConnections(serverNodes);
	}
	
	/**
	 * Test: Replication - catastrophic (multi-node) failure.
	 * See the Initiator class for how to execute this test.
	 * @param serverNodes
	 * @param quantity
	 * @param nPercent - % of nodes in the "serverNodes" list to shutdown
	 */
	static void testCatastrophicFailureGracefulDegradation(ArrayList<ServerNode> serverNodes, int nPercent) {
		printMessage("Test: Replication - catastrophic (multi-node) failure");
		createSocketConnections(serverNodes);
		
	    byte[] recv;
	    
	    //long start = 0;
	    //long stop = 0;
	    	    
	    int uid = 1;
	    	     
	    //int errors = 0;	   
	    //int successes = 0;
	    
	    int key = 2;
	    //int val = 3;
	    
	    int serverNodesOriginalSize = serverNodes.size();
	        
	    // crash some nodes	    
	    // update serverNodes list	    
	    int numOfNodesToShutdown =  (int)Math.ceil(((double)serverNodes.size() * nPercent) / 100);	    
	    
	    // randomly crash n% of the nodes
	    for (int i = 0; i < numOfNodesToShutdown; i++) {
	    	// pick a random node from the cloned list
	    	int randomInterger = getRandomInteger(0, serverNodes.size() - 1);
	    	ServerNode sn = serverNodes.get(randomInterger);
	    	System.out.println("> Shutting down: " + sn.getHostName());
	    	Connector c = sn.getConnector();
	    	
	    	recv = c.sendAndRecieve(Builder.packValueless(Integer.toString(uid), (byte)4, Integer.toString(key)));
	    	if (!Checks.chkValuelessC(recv, Integer.toString(uid), (byte)0)) {
	    		serverNodes.remove(sn); // remove from the list if shutdown is successful
	    	} else {
	    		System.err.println("Error shutting down node: " + sn.getHostName());
	    	}
	    	
	    	if (serverNodesOriginalSize == serverNodes.size()) {
	    		System.err.println("Failed to shut down any node");
	    		System.err.println("Aborting test case ... ");
	    		closeSocketConnections(serverNodes);
	    		return;
	    	}
	    	uid++; 		    	
	    }
	    
	    closeSocketConnections(serverNodes);
	    pause(60*3);	    
	}
	
	/**
	 * Test: Replication - catastrophic (multi-node) failure.
	 * @param serverNodes
	 * @param quantity
	 * @param nPercent - % of nodes in the "serverNodes" list to shutdown
	 */
	static void testCatastrophicFailure(ArrayList<ServerNode> serverNodes,  int quantity, int nPercent) {
		printMessage("Test: Replication - catastrophic (multi-node) failure");
		createSocketConnections(serverNodes);
		
	    byte[] recv;
	    
	    long start = 0;
	    long stop = 0;
	    	    
	    int uid = 1;
	    
	    // put requests to random nodes	  
	    int errors = 0;	   
	    int successes = 0;
	    
	    int key = 2;
	    int val = 3;
	    
	    start = System.currentTimeMillis();
		for (int i = 0; i < quantity; i++) {
			// pick a random node 
			int randomInterger = getRandomInteger(0, serverNodes.size() - 1);
			ServerNode sn = serverNodes.get(randomInterger);
			Connector c = sn.getConnector();
			
			recv = c.sendAndRecieve(Builder.packValued(Integer.toString(uid), (byte)1, Integer.toString(key), Integer.toString(val), false));
		    if (!Checks.chkValuelessB(recv, Integer.toString(uid), (byte)0)) {
		    	errors++;
		    } else {
		    	successes++;
		    }
		    //System.out.print(".");
			
			uid++;
		    key++;
		    val++;
		}
		stop = System.currentTimeMillis();
	    System.out.println("\nPut errors: " + errors + " / "+quantity+". Took: " + (stop - start) + " msec");
	    if (errors != quantity) System.out.println(Long.toString(((stop - start))/((long)(quantity-errors))) + " msec/req.");
	    System.out.println("Put error: " + getErrorPercentageDouble(successes, errors) + "%");
	    System.out.println("Put success: " + getSuccessPercentageDouble(successes, errors) + "%");
	    
	    pause(60);
	    
	    // crash some nodes	    
	    
	    // create a clone of the server node list
	    ArrayList<ServerNode> cloneServerNodes = new ArrayList<ServerNode>(serverNodes); 
	    //for (ServerNode sn : serverNodes) {
	    	//System.out.println(sn.getHostName());
	    	//cloneServerNodes.add((new ServerNode(sn.getHostName(), sn.getPortNumber()))); 
	    //}
	    
	    int numOfNodesToShutdown =  (int)Math.ceil(((double)serverNodes.size() * nPercent) / 100);	    
	    
	    // randomly crash n% of the nodes
	    for (int i = 0; i < numOfNodesToShutdown; i++) {
	    	// pick a random node from the cloned list
	    	int randomInterger = getRandomInteger(0, cloneServerNodes.size() - 1);
	    	ServerNode sn = cloneServerNodes.get(randomInterger);
	    	System.out.println("> Shutting down: " + sn.getHostName());
	    	Connector c = sn.getConnector();
	    	
	    	recv = c.sendAndRecieve(Builder.packValueless(Integer.toString(uid), (byte)4, Integer.toString(key)));
	    	if (Checks.chkValuelessC(recv, Integer.toString(uid), (byte)0)) {
	    		cloneServerNodes.remove(sn); // remove from the list if shutdown is successful
	    	} else {
	    		System.err.println("Error shutting down node: " + sn.getHostName());
	    	}
	    	
	    	if (serverNodes.size() == cloneServerNodes.size()) {
	    		System.err.println("Failed to shut down any node");
	    		System.err.println("Aborting test case ... ");
	    		return;
	    	}
	    	uid++; 		    	
	    }
	    
	    pause(60*3);
	    
	    // get requests to random "alive" nodes
	    errors = 0;	
	    successes = 0;
	    
	    key = 2;
	    val = 3;
	    start = System.currentTimeMillis();
	    for (int i = 0; i < quantity; i++) {
	    	// pick a random node from the cloned list
	    	int randomInterger = getRandomInteger(0, cloneServerNodes.size() - 1);
	    	ServerNode sn = cloneServerNodes.get(randomInterger);
	    	Connector c = sn.getConnector();
	    	
	    	recv = c.sendAndRecieve(Builder.packValueless(Integer.toString(uid), (byte)2, Integer.toString(key)));
	    	if (!Checks.chkValuedB(recv, Integer.toString(uid), (byte)0, Integer.toString(val))) {
	    		errors++;
	    	} else {
	    		successes++;
	    	}
	    	//System.out.print(".");
	    	uid++;
	    	key++;
	    	val++;
	    }
	    stop = System.currentTimeMillis();
	    System.out.println("\nGet errors: " + errors + " / "+quantity+". Took: " + (stop - start) + " msec");
	    if (errors != quantity) System.out.println(Long.toString(((stop - start))/((long)(quantity-errors))) + " msec/req.");
	    System.out.println("Get error: " + getErrorPercentageDouble(successes, errors) + "%");
	    System.out.println("Get success: " + getSuccessPercentageDouble(successes, errors) + "%");
	    
	    pause(60);	    
	    
	    // remove requests to random "alive" nodes
	    errors = 0;	    
	    successes = 0;
	    
	    key = 2;
	    val = 3;
	    start = System.currentTimeMillis();
	    for (int i = 0; i < quantity; i++) {
	    	// pick a random node from the cloned list
	    	int randomInterger = getRandomInteger(0, cloneServerNodes.size() - 1);
	    	ServerNode sn = cloneServerNodes.get(randomInterger);
	    	Connector c = sn.getConnector();
	    	
	    	recv = c.sendAndRecieve(Builder.packValueless(Integer.toString(uid), (byte)3, Integer.toString(key)));
	    	if (!Checks.chkValuelessB(recv, Integer.toString(uid), (byte)0)) {
	    		errors++;
	    	} else {
	    		successes++;
	    	}
	    	//System.out.print(".");
	    	uid++;
	    	key++;
	    	val++;
	    }
	    stop = System.currentTimeMillis();
	    System.out.println("\nRemove errors: " + errors + " / "+quantity+". Took: " + (stop - start) + " msec");
	    if (errors != quantity) System.out.println(Long.toString(((stop - start))/((long)(quantity-errors))) + " msec/req.");
	    System.out.println("Remove error: " + getErrorPercentageDouble(successes, errors) + "%");
	    System.out.println("Remove success: " + getSuccessPercentageDouble(successes, errors) + "%");
	    
	    cloneServerNodes.clear();
	    closeSocketConnections(serverNodes);
	}
	
	/**
	 * Test: Replication - verify (multi-node) graceful degradation.
	 * @param serverNodes
	 * @param quantity
	 * @param nPercent - % of nodes in the "serverNodes" list to shutdown.
	 */
	@Deprecated
	static void testGracefulDegradation(ArrayList<ServerNode> serverNodes,  int quantity, int nPercent) {
		printMessage("Test: Replication - verify (multi-node) graceful degradation");
		createSocketConnections(serverNodes);
		
	    byte[] recv;
	    
	    long start = 0;
	    long stop = 0;
	    	    
	    int uid = 1;
	    
	    // put requests to random nodes	  
	    int errors = 0;	    
	    int successes = 0;
	    
	    int key = 2;
	    int val = 3;
	    
	    start = System.currentTimeMillis();
		for (int i = 0; i < quantity; i++) {
			// pick a random node 
			int randomInterger = getRandomInteger(0, serverNodes.size() - 1);
			ServerNode sn = serverNodes.get(randomInterger);
			Connector c = sn.getConnector();
			
			recv = c.sendAndRecieve(Builder.packValued(Integer.toString(uid), (byte)1, Integer.toString(key), Integer.toString(val), false));
		    if (!Checks.chkValuelessB(recv, Integer.toString(uid), (byte)0)) {
		    	errors++;
		    } else {
		    	successes++;
		    }
		    //System.out.print(".");
			
			uid++;
		    key++;
		    val++;
		}
		stop = System.currentTimeMillis();
	    System.out.println("\nPut errors: " + errors + " / "+quantity+". Took: " + (stop - start) + " msec");
	    if (errors != quantity) System.out.println(Long.toString(((stop - start))/((long)(quantity-errors))) + " msec/req.");
	    System.out.println("Put error: " + getErrorPercentageDouble(successes, errors) + "%");
	    System.out.println("Put success: " + getSuccessPercentageDouble(successes, errors) + "%");
	    
	    pause(60);
	    
	    // get requests to random "alive" nodes
	    errors = 0;	   
	    successes = 0;
	    
	    key = 2;
	    val = 3;
	    start = System.currentTimeMillis();
	    for (int i = 0; i < quantity; i++) {
	    	// pick a random node from the cloned list
	    	int randomInterger = getRandomInteger(0, serverNodes.size() - 1);
	    	ServerNode sn = serverNodes.get(randomInterger);
	    	Connector c = sn.getConnector();
	    	
	    	recv = c.sendAndRecieve(Builder.packValueless(Integer.toString(uid), (byte)2, Integer.toString(key)));
	    	if (!Checks.chkValuedB(recv, Integer.toString(uid), (byte)0, Integer.toString(val))) {
	    		errors++;
	    	} else {
	    		successes++;
	    	}
	    	//System.out.print(".");
	    	uid++;
	    	key++;
	    	val++;
	    }
	    stop = System.currentTimeMillis();
	    System.out.println("\nGet errors: " + errors + " / "+quantity+". Took: " + (stop - start) + " msec");
	    if (errors != quantity) System.out.println(Long.toString(((stop - start))/((long)(quantity-errors))) + " msec/req.");
	    System.out.println("Get error: " + getErrorPercentageDouble(successes, errors) + "%");
	    System.out.println("Get success: " + getSuccessPercentageDouble(successes, errors) + "%");
	    
	    pause(60);
	    
	    // crash some nodes	    
	    
	    // create a clone of the server node list
	    ArrayList<ServerNode> cloneServerNodes = new ArrayList<ServerNode>(serverNodes); 
	    //for (ServerNode sn : serverNodes) {
	    	//System.out.println(sn.getHostName());
	    	//cloneServerNodes.add((new ServerNode(sn.getHostName(), sn.getPortNumber()))); 
	    //}
	    
	    int numOfNodesToShutdown =  (int)Math.ceil(((double)serverNodes.size() * nPercent) / 100);	    
	    
	    // randomly crash n% of the nodes
	    for (int i = 0; i < numOfNodesToShutdown; i++) {
	    	// pick a random node from the cloned list
	    	int randomInterger = getRandomInteger(0, cloneServerNodes.size() - 1);
	    	ServerNode sn = cloneServerNodes.get(randomInterger);
	    	System.out.println("> Shutting down: " + sn.getHostName());
	    	Connector c = sn.getConnector();
	    	
	    	recv = c.sendAndRecieve(Builder.packValueless(Integer.toString(uid), (byte)4, Integer.toString(key)));
	    	if (!Checks.chkValuelessC(recv, Integer.toString(uid), (byte)0)) {
	    		cloneServerNodes.remove(sn); // remove from the list if shutdown is successful
	    	} else {
	    		System.err.println("Error shutting down node: " + sn.getHostName());
	    	}
	    	
	    	if (serverNodes.size() == cloneServerNodes.size()) {
	    		System.err.println("Failed to shut down any node");
	    		System.err.println("Aborting test case ... ");
	    		return;
	    	}
	    	uid++; 		    	
	    }
	    
	    pause(60*3);
	    
	    // get requests to random "alive" nodes
	    errors = 0;	    
	    successes = 0;
	    
	    key = 2;
	    val = 3;
	    start = System.currentTimeMillis();
	    for (int i = 0; i < quantity; i++) {
	    	// pick a random node from the cloned list
	    	int randomInterger = getRandomInteger(0, cloneServerNodes.size() - 1);
	    	ServerNode sn = cloneServerNodes.get(randomInterger);
	    	Connector c = sn.getConnector();
	    	
	    	recv = c.sendAndRecieve(Builder.packValueless(Integer.toString(uid), (byte)2, Integer.toString(key)));
	    	if (!Checks.chkValuedB(recv, Integer.toString(uid), (byte)0, Integer.toString(val))) {
	    		errors++;
	    	} else {
	    		successes++;
	    	}
	    	//System.out.print(".");
	    	uid++;
	    	key++;
	    	val++;
	    }
	    stop = System.currentTimeMillis();
	    System.out.println("\nGet errors: " + errors + " / "+quantity+". Took: " + (stop - start) + " msec");
	    if (errors != quantity) System.out.println(Long.toString(((stop - start))/((long)(quantity-errors))) + " msec/req.");
	    System.out.println("Get error: " + getErrorPercentageDouble(successes, errors) + "%");
	    System.out.println("Get success: " + getSuccessPercentageDouble(successes, errors) + "%");
	    	    
	    pause(60);
	    
	    // remove requests to random "alive" nodes
	    errors = 0;	    
	    successes = 0;
	    
	    key = 2;
	    val = 3;
	    start = System.currentTimeMillis();
	    for (int i = 0; i < quantity; i++) {
	    	// pick a random node from the cloned list
	    	int randomInterger = getRandomInteger(0, cloneServerNodes.size() - 1);
	    	ServerNode sn = cloneServerNodes.get(randomInterger);
	    	Connector c = sn.getConnector();
	    	
	    	recv = c.sendAndRecieve(Builder.packValueless(Integer.toString(uid), (byte)3, Integer.toString(key)));
	    	if (!Checks.chkValuelessB(recv, Integer.toString(uid), (byte)0)) {
	    		errors++;
	    	} else {
	    		successes++;
	    	}
	    	//System.out.print(".");
	    	uid++;
	    	key++;
	    	val++;
	    }
	    stop = System.currentTimeMillis();
	    System.out.println("\nRemove errors: " + errors + " / "+quantity+". Took: " + (stop - start) + " msec");
	    if (errors != quantity) System.out.println(Long.toString(((stop - start))/((long)(quantity-errors))) + " msec/req.");
	    System.out.println("Remove error: " + getErrorPercentageDouble(successes, errors) + "%");
	    System.out.println("Remove success: " + getSuccessPercentageDouble(successes, errors) + "%");
	    
	    cloneServerNodes.clear();
	    closeSocketConnections(serverNodes);
	}
}
