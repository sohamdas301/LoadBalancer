package org.example;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.example.utils.BackendServers;

public class ClientSocketHandler implements Runnable {
	private final Socket clientSocket;

	public ClientSocketHandler(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	@Override
	public void run() {
		try {
			InputStream clientToLoadBalancerInputStream = clientSocket.getInputStream();
			OutputStream loadBalancerToClientOutputStream = clientSocket.getOutputStream();

//			String serverHost = BackendServers.getHost();

			String serverHost = ConsistentHashing.getServer(clientSocket.getLocalAddress().toString());
//			String serverHost = ConsistentHashing.getServer("navonvlknoainvsdbkdbvbuvbjkvbifbsd");
			System.out.println("Server " + serverHost + " will handle this request");

			Socket backendSocket = new Socket(serverHost, 8080);

			InputStream backendServerToLoadBalancerInputStream = backendSocket.getInputStream();
			OutputStream loadBalancerToBackendServerOutputStream = backendSocket.getOutputStream();

			Thread clientDataHandler = new Thread() {
				@Override
				public void run() {
					try {
						int data = clientToLoadBalancerInputStream.read();
						while (data != -1) {
							loadBalancerToBackendServerOutputStream.write(data);
							data = clientToLoadBalancerInputStream.read();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};

			clientDataHandler.start();

			Thread backendDataHandler = new Thread() {
				@Override
				public void run() {
					try {
						int data = backendServerToLoadBalancerInputStream.read();
						while (data != -1) {
							loadBalancerToClientOutputStream.write(data);
							data = backendServerToLoadBalancerInputStream.read();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};

			backendDataHandler.start();

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}
}
