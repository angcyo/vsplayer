package com.linux.vshow;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class UpBlockSize extends Thread {
	private String urlstr = "";

	public UpBlockSize(String urlstr) {
		this.urlstr = urlstr;
	}

	@Override
	public void run() {
		try {
			URL url = new URL(urlstr);
			URLConnection connection = url.openConnection();
			connection.connect();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));

		} catch (Exception e) {
		}
	}
}
