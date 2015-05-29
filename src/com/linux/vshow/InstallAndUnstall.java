package com.linux.vshow;

import java.io.FileOutputStream;
import java.io.InputStream;

import android.content.Context;
import android.widget.Toast;

public class InstallAndUnstall extends Thread {

	String textApk = "com.vshow.textfont";
	String fontApk = "com.vshow.scrollfont";
	String testTImeapk = "com.example.test_time";
	Context ct = null;

	public InstallAndUnstall(Context ct){
		this.ct = ct;
	}
	@Override
	public void run() {
		Constant.change = 5;
		Tool.uninstall(textApk);
		Tool.uninstall(fontApk);
		Tool.uninstall(testTImeapk);
		try {
			InputStream is = ct.getAssets().open("textfont.apk");
			FileOutputStream fos = new FileOutputStream(Constant.tempDir
					+ "textfont.apk");

			byte[] buffer = new byte[1024];
			int readLen = 0;
			while ((readLen = is.read(buffer, 0, 1024)) >= 0) {
				fos.write(buffer, 0, readLen);
			}
			is = ct. getAssets().open("scrollfont.apk");
			fos = new FileOutputStream(Constant.tempDir + "scrollfont.apk");
			byte[] buffer2 = new byte[1024];
			int readLen2 = 0;
			while ((readLen2 = is.read(buffer2, 0, 1024)) >= 0) {
				fos.write(buffer2, 0, readLen2);
			}

			Tool.install(Constant.tempDir + "textfont.apk");
			Tool.install(Constant.tempDir + "scrollfont.apk");
			Toast.makeText(ct, "清理完成", 1).show();
		} catch (Exception e) {

		}
		
	}
}
