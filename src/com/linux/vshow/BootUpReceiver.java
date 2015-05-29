package com.linux.vshow;

import java.io.File;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;

public class BootUpReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		if (arg1.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			String[] ps = Tool.loadConfig(new String[] { "savedir" },
					Constant.saveDIR);
			if (ps != null) {
				if (ps.length == 1) {
					try {
						int ppa = Integer.parseInt(ps[0].trim());
						if (ppa != 0) {
							if (ppa == 1) {
								Constant.sdcardDir = Environment
										.getExternalStorageDirectory()
										.getAbsolutePath();
							} else if (ppa == 2) {
								Thread.sleep(1000);
								// Constant.sdcardDir = "/mnt/sata";
								// initConfigFile();
								File file = new File(Constant.config);
								if (!file.exists()) {
									Constant.sdcardDir = "/mnt/extsd";
									initConfigFile();
									file = new File(Constant.config);
									if (!file.exists()) {
										Constant.sdcardDir = Environment
												.getExternalStorageDirectory()
												.getAbsolutePath();
									}
								}
							} else {
								initConfigFile();
								File file = new File(Constant.config);
								if (!file.exists()) {
									Constant.sdcardDir = Environment
											.getExternalStorageDirectory()
											.getAbsolutePath();
								}
							}

						} else {
							initConfigFile();
							File file = new File(Constant.config);
							if (!file.exists()) {
								Constant.sdcardDir = Environment
										.getExternalStorageDirectory()
										.getAbsolutePath();
							}
						}
					} catch (Exception e) {
						// TODO: handle exception
					}
				}

			} else {
				try {
					Thread.sleep(1000);
					// Constant.sdcardDir = "/mnt/sata";
					// initConfigFile();
					File file = new File(Constant.config);
					// if (!file.exists()) {
					Constant.sdcardDir = "/mnt/extsd";
					initConfigFile();
					file = new File(Constant.config);
					if (!file.exists()) {
						Constant.sdcardDir = Environment
								.getExternalStorageDirectory().getAbsolutePath();
					}
					// }
				} catch (Exception e) {
				}

			}
			Constant.config = Constant.sdcardDir + "/config.ini"; // 播放设置等配置文件
			String[] params = Tool.loadConfig(new String[] { "start" },
					Constant.config);
			try {
				String startup = params[0].trim();
				if (startup.equals("1")) {
					Constant.startup = false;
				} else {
					Constant.startup = true;
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			if (Constant.startup) {
				Intent i = new Intent(arg0, Vsplayer.class);
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				arg0.startActivity(i);
			}
		} else if (arg1.getAction().equals("myAction")) {
			Intent i = new Intent(arg0, Vsplayer.class);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			arg0.startActivity(i);
		}
	}
	private void initConfigFile() {
		Constant.config = Constant.sdcardDir + "/config.ini";
		Constant.config2 = Constant.sdcardDir + "/config2.ini";
		Constant.config3 = Constant.sdcardDir + "/config3.ini";

		if (!new File(Constant.config).exists()) {
			try {
				new File(Constant.config).createNewFile();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		if (!new File(Constant.config2).exists()) {
			try {
				new File(Constant.config2).createNewFile();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		if (!new File(Constant.config3).exists()) {
			try {
				new File(Constant.config3).createNewFile();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}
}
