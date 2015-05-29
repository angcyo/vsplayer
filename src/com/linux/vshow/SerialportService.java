package com.linux.vshow;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

public class SerialportService extends Service {
	Console console;
	private static String pass = "";
	private ScheduledExecutorService scheduledExecutorService = null;
	byte[] bytes;

	public class bind extends Binder {
		public SerialportService ms() {
			return SerialportService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return new bind();
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		scheduledExecutorService.shutdownNow();
		super.onDestroy();
	}

	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		if (scheduledExecutorService != null) {
			scheduledExecutorService.shutdownNow();
		}
		if (console == null) {
			console = new Console("/dev/ttyS2", 9600);
		}
		bytes = new byte[3];
		bytes[0] = (byte) 0xE0;
		bytes[1] = (byte) 0x05;
		bytes[2] = (byte) 0xE5;
		// E0 05 E5
		try {
			console.send(bytes, 0, 3);
		} catch (Exception e) {
		}
		console.receive();
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		scheduledExecutorService.scheduleAtFixedRate(new read(), 1, 1,
				TimeUnit.SECONDS);
	}

	public static void message(String msgString) {
		pass = msgString;
	}

	public class read extends Thread {
		public void run() {
			if (!pass.equals("")) {
				pass = pass.substring(1, 8);
				pass = pass.trim();
				String str2 = "";
				if (pass != null && !"".equals(pass)) {
					for (int i = 0; i < pass.length(); i++) {
						if (pass.charAt(i) >= 48 && pass.charAt(i) <= 57) {
							str2 += pass.charAt(i);
						}
					}
				}
				Intent intent1 = new Intent(
						Intent.ACTION_VIEW,
						Uri.parse("http://www.huikuibao.com/v.php?code=" + str2));
				intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent1);
				pass = "";
			}
		}
	}
}
