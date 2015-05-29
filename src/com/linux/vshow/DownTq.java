package com.linux.vshow;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownTq extends Thread {
	private String urlStr;
	private String filename;

	public DownTq(String urlStr, String filename) {
		this.urlStr = urlStr;
		this.filename = filename;
	}

	public void run() {
		FileOutputStream fout = null;
		HttpURLConnection conn = null;
		InputStream bs = null;
		try {
			URL url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout((Constant.lian * 3) * 1000);
			conn.setReadTimeout((Constant.lian * 3) * 1000);
			bs = conn.getInputStream();
			fout = new FileOutputStream(Constant.tempDir + "tq.html");
			byte[] b = new byte[4096];
			int nRead;
			while ((nRead = bs.read(b)) != -1) {
				fout.write(b, 0, nRead);
			}
			fout.close();
			bs.close();
			conn.disconnect();
			if (new File(filename).exists()) {  
				new File(filename).delete();
			}
			new File(Constant.tempDir + "tq.html").renameTo(new File(filename));
			Constant.change=22;
		} catch (Exception e) {
			try {
				fout.close();
			} catch (Exception e2) {
			}
			try {
				bs.close();
			} catch (Exception e2) {
			}
			try {
				conn.disconnect();
			} catch (Exception e2) {
			}

		}
	}

}
