package com.linux.vshow;

public class Connect2 extends Thread {

	public void run() {
		String msg = "";
		float dons = 0;
		String saa = "";
		if (Constant.alltotal != 0) {
			if (Constant.downtotal >= Constant.alltotal) {
				dons = 1;
			} else {
				dons = (((float) Constant.downtotal) / Constant.alltotal);
			}
		}
		dons = dons * 100;
		saa = Constant.mac + "_3_" + dons;
		if (saa.length() >= 24) {
			saa = saa.substring(0, 24);
		}
		if (Constant.sendkey.length() > 0) {
			saa = saa + "_" + Constant.sendkey;
		} else {
			saa = saa + "_0";
		}
		msg = Constant.sc.sendMsg(saa);

		if (msg.length() > 0) {
			Constant.sendkey = "";
		}
		if (msg.length() < 3 || msg.length() > 4096) {
			return;
		}
		int sign = 0;
		try {
			sign = Integer.parseInt(msg.substring(0, 2));
		} catch (Exception e) {
		}
		int filePos = msg.indexOf('+');
		msg = msg.substring(filePos + 1, msg.length());
		if (sign == 7) {
			Constant.change = 7;
			Constant.msg = msg;
		} else if (sign == 12) {
			if (Constant.hd != null) {
				Constant.hd.out = true;
				Constant.hd = null;
				Constant.downing = false;

				if (Constant.rss != null) {
					try {
						Constant.rss.close();
					} catch (Exception e) {
						// TODO: handle exception
					}
					Constant.rss=null;
				}
			}
			Constant.change = 12;
			Constant.msg = msg;
		}
	}
}
