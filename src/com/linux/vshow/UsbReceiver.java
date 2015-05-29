package com.linux.vshow;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class UsbReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context arg0, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)) {
			Constant.change = 29;
		} else if (intent.getAction().equals(Intent.ACTION_MEDIA_REMOVED)) {
		}

	}

}
