package ca.NetSysLab.UDPClient;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Initiator {
  static String a  = "null";
  static String a2 = "null";
  static String a3 = "null";
  static int p    = 0;
  static int p2   = 0;
  static int p3   = 0;
  
  static boolean DEBUG = false;
  
  // static methods  
  static ArrayList<ServerNode> buildServerNodeList(String fileName) {
	  ArrayList<ServerNode> serverNodes = new ArrayList<ServerNode>();
	  
	  FileInputStream fin = null;
	  BufferedReader br = null;
	  try {
		  fin = new FileInputStream(fileName);
		  br = new BufferedReader(new InputStreamReader(fin));
		  String line = "";
		  while ((line = br.readLine()) != null) {
			String[] tokens = line.split(":");

	    	/*try {
	    		 if (InetAddress.getByName(tokens[0]).isReachable(5000)) 
	    		 {
	    			 serverNodes.add(new ServerNode(tokens[0], Integer.parseInt(tokens[1])));
	    		 } 
	    	} catch (IOException e) {
	    		 e.printStackTrace();
	    	}*/
	    	
			try {
				InetAddress.getByName(tokens[0]);
				serverNodes.add(new ServerNode(tokens[0], Integer.parseInt(tokens[1])));
			} catch (UnknownHostException e) {
				//e.printStackTrace();
				System.err.println("\nFailed to reach host: " + tokens[0]);
				System.err.println("Excluding from the server node list ... ");
			}			
		  }
	  } catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fin.close();
				br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	  return serverNodes;
  }
  
  void executeTestsAssignmentFour() {

	    System.out.print("Enter address1:port1 ");
	    String[] sa = System.console().readLine().split(":");
	    a = sa[0];
	    p = Integer.parseInt(sa[1]);

	    System.out.print("Enter address2:port2 ");
	    String[] sa2 = System.console().readLine().split(":");
	    a2 = sa2[0];
	    p2 = Integer.parseInt(sa2[1]);

	    System.out.print("Enter address3:port3 ");
	    String[] sa3 = System.console().readLine().split(":");
	    a3 = sa3[0];
	    p3 = Integer.parseInt(sa3[1]);


	    System.out.println("Testing STORE A1 -------");
	    Tests.testStore(a, p);  
	    System.out.println();
	    
	    System.out.println("Testing STORE A2 -------");
	    Tests.testStore(a2, p2);  
	    System.out.println();
	    
	    System.out.println("Testing STORE A3 -------");
	    Tests.testStore(a3, p3);  
	    System.out.println();

	    System.out.println("Testing REMOVE -------");
	    Tests.testRemove(a, p);
	    System.out.println();

	    System.out.println("Testing REPLACE -------");
	    Tests.testReplace(a, p);
	    System.out.println();

	    System.out.println("Testing LOSS REMOVE -------");
	    Tests.testLossRemove(a, p);
	    System.out.println();

	    System.out.println("Testing CONCURRENCY PUT -------");
	    Tests.testConcurrencyPut(a, p);
	    System.out.println();
	    
	    System.out.println("Testing CROSS SERVER A1-A2 -------");
	    Tests.testCross(a, a2, p, p2, 0);
	    System.out.println();
	    
	    System.out.println("Testing CROSS SERVER A1-A3 -------");
	    Tests.testCross(a, a3, p, p3, 0);
	    System.out.println();
	    
	    System.out.println("Testing CROSS SERVER A2-A3 -------");
	    Tests.testCross(a2, a3, p2, p3, 0);
	    System.out.println();
	   
	    System.out.println("Testing NODE FAILURE -------");
	    Tests.testNodeFail(a, p, a2, p2, a3, p3);
	    System.out.println();

	    System.out.println("\nDONE");
	    System.exit(0);
  }
    
  static void executeTestsAssignmentFive() throws IOException {
	  BufferedReader br;
	  
	  //String smallListFileName = "";
	  String bigListFileName = "";
	  	  	  
	  //System.out.print("Enter small node list file path: ");
	  //br = new BufferedReader(new InputStreamReader(System.in));
	  //smallListFileName = br.readLine(); 
	  
	  System.out.print("Enter big node list file path: ");
	  br = new BufferedReader(new InputStreamReader(System.in));
	  bigListFileName = br.readLine(); 
	  	  
	  //System.out.println("Enter\n A - to run test suite\n B - to run traffic generator\n C - to run load tests on the machine traffic generator is sending requests to");
	  //br = new BufferedReader(new InputStreamReader(System.in));
	  //String option = br.readLine();;
	  
	  String option = "A";
	  	  	  
	  //ArrayList<ServerNode> smallLsitServerNodes = buildServerNodeList(smallListFileName);
	  ArrayList<ServerNode> bigLsitServerNodes = buildServerNodeList(bigListFileName);
	  
	  System.out.println("Done building node list ... ");
	  
	  int numberOfRequestsSmall = 10;
	  int numberOfRequests = 20;
	  int numberOfClientsSmall = 20;
	  int numberOfClientsLarge = 50;
	  long durationSeconds = 180;
	  	  	  
	  if (option.equalsIgnoreCase("A")) {	
		  		  
		  System.out.print("Enter Number of requests small,Number of requests large,Number of clients small,Number of clients large,Request sending duration (seconds): ");
		  br = new BufferedReader(new InputStreamReader(System.in));
		  String[] s = br.readLine().split(",");
		  numberOfRequestsSmall = Integer.parseInt(s[0]);
		  numberOfRequests = Integer.parseInt(s[1]);
		  numberOfClientsSmall = Integer.parseInt(s[2]);
		  numberOfClientsLarge = Integer.parseInt(s[3]);
	      durationSeconds = Long.parseLong(s[4]);
	      
	      System.out.println("\n ... Running response time tests - medium load ...");
	      
	      TestsAssignmentFive.testMultipleNodesMultipleClients(bigLsitServerNodes, numberOfRequestsSmall, 2222, 1); // multiple nodes / single client
	      TestsAssignmentFive.testMultipleNodesMultipleClients(bigLsitServerNodes, numberOfRequests, 2222, numberOfClientsSmall); // multiple nodes / multiple clients	            
		  		  		  
	      System.out.println("\n ... Running throughput tests - medium load ...");
	      
	      TestsAssignmentFive.testMultipleNodesMultipleClients(bigLsitServerNodes, durationSeconds, 2222, numberOfClientsSmall, "PUT"); // multiple nodes / multiple clients
		  TestsAssignmentFive.testMultipleNodesMultipleClients(bigLsitServerNodes, durationSeconds, 2222, numberOfClientsSmall, "GET"); // multiple nodes / multiple clients
		  TestsAssignmentFive.testMultipleNodesMultipleClients(bigLsitServerNodes, durationSeconds, 2222, numberOfClientsSmall, "REMOVE"); // multiple nodes / multiple clients
	      
		  System.out.println("\n ... Running throughput tests - high load ...");
		  
	      TestsAssignmentFive.testMultipleNodesMultipleClients(bigLsitServerNodes, durationSeconds, 2222, numberOfClientsLarge, "PUT"); // multiple nodes / multiple clients
		  TestsAssignmentFive.testMultipleNodesMultipleClients(bigLsitServerNodes, durationSeconds, 2222, numberOfClientsLarge, "GET"); // multiple nodes / multiple clients
		  TestsAssignmentFive.testMultipleNodesMultipleClients(bigLsitServerNodes, durationSeconds, 2222, numberOfClientsLarge, "REMOVE"); // multiple nodes / multiple clients
		  
		  System.out.println("\n ... Running throughput test - before catastrophic node failure ...");
		  TestsAssignmentFive.testMultipleNodesMultipleClients(bigLsitServerNodes, durationSeconds, 2222, numberOfClientsSmall, "PUT"); // multiple nodes / multiple clients
		  TestsAssignmentFive.testMultipleNodesMultipleClients(bigLsitServerNodes, durationSeconds, 2222, numberOfClientsSmall, "GET"); // multiple nodes / multiple clients
		  		  
		  // this one simply shuts down some of the nodes
		  TestsAssignmentFive.testCatastrophicFailureGracefulDegradation(bigLsitServerNodes, 20); // multiple nodes / single client
		  
		  System.out.println("\n ... Running throughput test - after catastrophic node failure ...");		  
		  TestsAssignmentFive.testMultipleNodesMultipleClients(bigLsitServerNodes, durationSeconds, 2222, numberOfClientsSmall, "GET"); // multiple nodes / multiple clients
		  TestsAssignmentFive.testMultipleNodesMultipleClients(bigLsitServerNodes, durationSeconds, 2222, numberOfClientsSmall, "REMOVE"); // multiple nodes / multiple clients
		  
	  } else if (option.equalsIgnoreCase("B")) {
		  TrafficGenerator.putRqstOnlySameNode(bigLsitServerNodes.get(0), 60); // single node / single client 		  
	  } else if (option.equalsIgnoreCase("C")) {
		  TestsAssignmentFive.testSameNodeMultipleClients(bigLsitServerNodes.get(0), numberOfRequests, 2222, numberOfClientsSmall); // single node/ multiple clients 
	  } else {
		  System.err.println("Invalid option.");
		  return;
	  }
  }

  public static void main(String args[]) throws IOException {	 
	  executeTestsAssignmentFive();  
  }  
}
