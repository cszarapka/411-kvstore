package ca.NetSysLab.UDPClient;

public interface Command {
	public static final byte PUT = 1;
	public static final byte GET = 2;
	public static final byte REMOVE = 3;
	public static final byte SHUTDOWN = 4;
}
