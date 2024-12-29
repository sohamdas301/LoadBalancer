package org.example;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class ConsistentHashing {
	private static TreeMap<Long, String> ring;
	private static int numberOfReplicas;
	private static MessageDigest md;

	public ConsistentHashing(int numberOfReplicas) throws NoSuchAlgorithmException {
		this.ring = new TreeMap<>();
		this.numberOfReplicas = numberOfReplicas;
		this.md = MessageDigest.getInstance("MD5");
	}

	public static void addServer(String server) {
		for (int i = 0; i < numberOfReplicas; i++) {
			long hash = generateHash(server + i);
			ring.put(hash, server);
		}
	}

	public static void removeServer(String server) {
		for (int i = 0; i < numberOfReplicas; i++) {
			long hash = generateHash(server + i);
			ring.remove(hash);
		}
	}

	public static String getServer(String key) {
		if (ring.isEmpty()) {
			return null;
		}
		long hash = generateHash(key);
		System.out.println("Hash of key " + key + " is " + hash);
		if (!ring.containsKey(hash)) {
			SortedMap<Long, String> tailMap = ring.tailMap(hash);
			hash = tailMap.isEmpty() ? ring.firstKey() : tailMap.firstKey();
		}
		return ring.get(hash);
	}

	private static long generateHash(String key) {
		md.reset();
		md.update(key.getBytes());
		byte[] digest = md.digest();
		long hash = ((long) (digest[3] & 0xFF) << 24) | ((long) (digest[2] & 0xFF) << 16)
				| ((long) (digest[1] & 0xFF) << 8) | ((long) (digest[0] & 0xFF));
		return hash;
	}

	public static void printHashRing() {
		for (Map.Entry<Long, String> me : ring.entrySet()) {
			System.out.println(me.getKey() + " " + me.getValue());
		}
	}
}
