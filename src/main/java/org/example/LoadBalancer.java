package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;

/**
 * Hello world!
 *
 */
public class LoadBalancer {
	public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
		final ServerSocket serverSocket = new ServerSocket(8081);
		System.out.println("LoadBalancer Started at port : " + serverSocket.getLocalPort());
		ConsistentHashing ch = new ConsistentHashing(2);
		ch.addServer("172.31.25.135");
		ch.addServer("172.31.31.242");
		ch.printHashRing();

		System.out.println("LoadBalancing Started");

		while (true) {
			final Socket socket = serverSocket.accept();
			System.out.println("TCP connection established with client : " + socket.toString());
			System.out.println("Inet Address" + socket.getInetAddress().toString());
			handleSocket(socket);
		}

	}

	private static void handleSocket(Socket socket) {
		ClientSocketHandler clientSocketHandler = new ClientSocketHandler(socket);
		Thread clientSocketHandlerThread = new Thread(clientSocketHandler);
		clientSocketHandlerThread.start();
	}
}
