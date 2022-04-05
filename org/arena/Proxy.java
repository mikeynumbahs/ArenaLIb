package org.arena.scraping.htmlunit.phantom;

public class Proxy {
	public final String server;
	public final int port;
	public final String user;
	public final String pass;
		
	public Proxy(String server, int port) {
		this(server, port, null, null);
	}
	
	public Proxy(String server, int port, String user) {
		this(server, port, user, "");
	}
	
	public Proxy(String server, int port, String user, String pass) {
		this.server = server;
		this.port = port;
		this.user = user;
		this.pass = pass;
	}
		
	public String toString() {
		return server + " ("+port+") ";
	}
}