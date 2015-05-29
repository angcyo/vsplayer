package com.linux.vshow;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EncodingUtils;

public class IsNetwork extends Thread {
	String myString = "";
	@Override
	public void run() {
		
		try {
			URL url = new URL("HTTP://www.baidu.com/index.html");
			URLConnection urlCon = url.openConnection();
			urlCon.setConnectTimeout(1500);
			InputStream is = urlCon.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			ByteArrayBuffer baf = new ByteArrayBuffer(50);
			int current = 0;
			while ((current = bis.read()) != -1) {
				baf.append((byte) current);
			}
			myString = EncodingUtils.getString(baf.toByteArray(), "UTF-8");
			bis.close();
			is.close();
		} catch (Exception e) {
			Constant.openurl = false;
			Constant.li.writeLog("0000 ÍøÂçÒì³£");
		}
		if (myString.indexOf("www.baidu.com") > -1) {
			Constant.openurl = true;
			Constant.li.writeLog("0000 ÍøÂçÕı³£");
		} else {
			Constant.openurl = false;
			Constant.li.writeLog("0000 ÍøÂçÒì³£");
		}
	}
}
