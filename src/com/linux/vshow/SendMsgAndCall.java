package com.linux.vshow;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;

public class SendMsgAndCall {

	Context ct = null;
	
	public SendMsgAndCall(Context context) {
		this.ct = context;
	}

	public void Call(String number) {
		Intent intent=new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+number));
		ct.startActivity(intent);
		
	
	}

	public void SendMsg(String number, String msg) {
		SmsManager smsManager = SmsManager.getDefault();
		ArrayList<String> texts = smsManager.divideMessage(msg);
		for (String text : texts) {
			smsManager.sendTextMessage(number, null, text, null, null);
		}
	}
	// <uses-permission android:name="android.permission.CALL_PHONE" />
	// <uses-permission android:name="android.permission.SEND_SMS"/>

}
