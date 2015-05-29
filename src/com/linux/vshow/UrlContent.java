package com.linux.vshow;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UrlContent extends Thread {

	String urls = "http://" + Constant.SRVIP + ":8512/vs/gettbtime.vs";
	long diff = 0;

	@Override
	public void run() {
		getUrlContent(urls);
	}

	public void getUrlContent(String urls) {
		URL url;
		Constant.nowTimes = "";
		String str = "";
		HttpURLConnection huc = null;
		BufferedReader reader = null;
		try {
			url = new URL(urls);
			huc = (HttpURLConnection) url.openConnection();
			huc.setConnectTimeout(2000);
			InputStream is = huc.getInputStream();
			reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
			String temp;
			while ((temp = reader.readLine()) != null) {
				str += temp;
			}
			Constant.nowTimes = str.trim();

		} catch (Exception e) {

		} finally {
			try {
				reader.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}

			try {
				huc.disconnect();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		if (Constant.nowTimes.equals("")) {
			return;

		}
		Constant.change = 9;

	}

}
