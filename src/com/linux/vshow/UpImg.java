package com.linux.vshow;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpImg extends Thread {

	private File f;
	private String urlstr;

	public UpImg(File f, String urlstr) {
		this.f = f;
		this.urlstr = urlstr;
	}

	public void run() {
		HttpURLConnection conn = null;
		DataInputStream in = null;
		OutputStream out = null;
		try {
			URL url = new URL(urlstr);
			String BOUNDARY = "---------7d4a6d158c9";
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(3000);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("Charsert", "UTF-8");
			conn.setRequestProperty("Content-Type",
					"multipart/form-data; boundary=" + BOUNDARY);
			conn.setChunkedStreamingMode(1024);
			out = new DataOutputStream(conn.getOutputStream());
			byte[] end_data = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
			StringBuilder sb = new StringBuilder();
			sb.append("--");
			sb.append(BOUNDARY);
			sb.append("\r\n");
			sb.append("Content-Disposition: form-data;name=\"up\";filename=\""
					+ f.getName() + "\"\r\n");
			sb.append("Content-Type: image/jpeg\r\n\r\n");
			byte[] data = sb.toString().getBytes();
			out.write(data);
			in = new DataInputStream(new FileInputStream(f));
			int bytes = 0;
			byte[] bufferOut = new byte[1024];
			while ((bytes = in.read(bufferOut)) != -1) {
				out.write(bufferOut, 0, bytes);
			}
			out.write(end_data);
			out.flush();
			conn.getInputStream();
		} catch (Exception e) {

		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			if (conn != null) {
				try {
					conn.disconnect();
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
	}
}
