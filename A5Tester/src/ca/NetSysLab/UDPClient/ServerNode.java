package ca.NetSysLab.UDPClient;

public class ServerNode {
	 private String hostName;
	 private int portNumber;
	 private Connector connector;
	 
	 public ServerNode(String hostName, int portNumber) {
		 this.hostName = hostName;
		 this.portNumber = portNumber;
	 }
	 
	 public String getHostName() {
		return this.hostName; 
	 }
	 
	 public int getPortNumber() {
		 return this.portNumber;
	 }
	 
	 public void setConnector(Connector connector) {
		 this.connector = connector;
	 }
	 
	 public Connector getConnector() {
		 return this.connector;
	 }
}
