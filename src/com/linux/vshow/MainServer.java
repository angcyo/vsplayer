package com.linux.vshow;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MainServer extends Thread {

	private ServerSocket serverSocket = null;
	private boolean close = false;

	public void run() {
		close = false;
		try {
			serverSocket = new ServerSocket(8515);
		} catch (Exception e) {
		}
		while (true) {
			if (close) {
				break;
			}
			receive();
		}
	}

	private void receive() {
		byte[] buffer = new byte[1];
		Socket client = null;
		try {
			client = serverSocket.accept();
//			client.setSoTimeout((Constant.lian - 2) * 1000);
//			client.setSoLinger(true, 0);
			client.getInputStream().read(buffer);
		} catch (IOException e) {
		
		}
		try {
			if (new String(buffer).trim().equals("1")) {
				Constant.change = 27;
			} else if (new String(buffer).trim().equals("2")) {
				Constant.change = 28;
			}
		} catch (Exception e) {
		}

		try {
			if (client != null) {
				client.close();
			}
		} catch (IOException e) {

		}

	}

	public void StopServer() {
		close = true;
		try {
			serverSocket.close();
		} catch (IOException e) {
		}
	}
}