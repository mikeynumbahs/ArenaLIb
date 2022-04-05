package org.arena.scraping.htmlunit.phantom;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.arena.util.UniqueList;

import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.ProxyConfig;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebClientOptions;

public class Phantom {
	Random random = new Random();
	
	protected List<String> blockedIps = new UniqueList<>();
	protected List<Proxy> proxies = new ArrayList<>();
	
	public Phantom() {}
	
	public Phantom(String server, int port) {
		this();
		addServer(server, port);
	}
	
	public Phantom(String server, int port, String user, String pass) {
		this();
		addServer(server, port, user, pass);
	}
	
	public Phantom(List<Proxy> servers) {
		this();
		setServers(servers);
	}
	
	public void setServers(List<Proxy> proxies) {
		this.proxies = proxies;
	}
	
	public void addServer(String server, int port) {
		proxies.add(new Proxy(server, port));
	}
		
	public void addServer(String server, int port, String user) {
		proxies.add(new Proxy(server, port, user, ""));
	}
		
	public void addServer(String server, int port, String user, String pass) {
		proxies.add(new Proxy(server, port, user, pass));
	}
	
	public WebClient getClient() {
		return getClient(pickRandomProxy());
	}
	
	public WebClient getClient(int index) {
		final Proxy proxyInfo = proxies.get(index);
		final WebClient client = new WebClient();
		
		final ProxyConfig proxyConfig = new ProxyConfig(proxyInfo.server, proxyInfo.port);
		if (proxyInfo.user != null && proxyInfo.pass != null) {
			final DefaultCredentialsProvider credentialsProvider = 
					(DefaultCredentialsProvider) client.getCredentialsProvider();
			credentialsProvider.addCredentials(proxyInfo.user, proxyInfo.pass);
		}
		
		final WebClientOptions options = client.getOptions();
		options.setProxyConfig(proxyConfig);
		options.setCssEnabled(false);
		options.setUseInsecureSSL(false);
		options.setRedirectEnabled(true);
		options.setDoNotTrackEnabled(true);
		options.setDownloadImages(false);
		options.setGeolocationEnabled(false);
		options.setThrowExceptionOnScriptError(false);
		options.setJavaScriptEnabled(false);
		client.getCookieManager().setCookiesEnabled(false);
		
		return client;
	}
	
	protected Integer pickRandomProxy() {
		int totalServers = proxies.size();
		switch (totalServers) {
			case 0 : return null;
			case 1 : return 0;
			default : return random.nextInt(totalServers-1);
		}
	}
	
	public final Phantom getNewInstance() {
		return new Phantom(proxies);
	}
	
	public final List<String> getBlockedIps() {
		List<String> blocked = new ArrayList<>();
		for (String ip : blockedIps) {
			blocked.add(ip);
		}
		return blocked;
	}
	
	public final void addBlockedIp(String ip) {
		blockedIps.add(ip);
	}
}
