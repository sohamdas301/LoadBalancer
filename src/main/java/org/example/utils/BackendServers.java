package org.example.utils;

import java.util.ArrayList;
import java.util.List;

public class BackendServers {
	
	private static List<String> servers = new ArrayList<>();
	
	private static int count = 0;
	
	static {
		servers.add("172.31.25.135");
		servers.add("172.31.31.242");
	}
	
	public static String getHost() {
		// Handle the case where their are no active servers
		String host = servers.get(count%servers.size());
		count++;
		return host;
	}
	
}
