package com.linux.vshow;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Connect extends Thread {

	private VideoPlayer vp;
	private boolean updata = false;

	public Connect(VideoPlayer vp, boolean updata) {
		this.vp = vp;
		this.updata = updata;
	}

	public void SerialPort(File device) throws SecurityException, IOException {
		if (!device.canRead() || !device.canWrite()) {
			try {
				Process su;
				su = Runtime.getRuntime().exec("su");
				String cmd = "chmod 777 " + device.getAbsolutePath() + "\n"
						+ "exit\n";
				su.getOutputStream().write(cmd.getBytes());
				if ((su.waitFor() != 0) || !device.canRead()
						|| !device.canWrite()) {
					throw new SecurityException();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new SecurityException();
			}
		}
	}

	public void run() {
		if (!Constant.cmdopen) {
			try {
				SerialPort(new File("/sys/devices/platform/disp/hdmi_ctrl"));
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
			} catch (IOException e) {
				// TODO Auto-generated catch block
			}
			Constant.cmdopen = true;

		}

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
		String biao = "1";
		if (updata) {
			biao = "4";
		}
		saa = Constant.mac + "_" + biao + "_" + dons;
		if (saa.length() >= 24) {
			saa = saa.substring(0, 24);
		}
		if (updata) {
			saa = saa + "|" + Constant.playname + "|" + Constant.playtime;
		}
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String cur = sf.format(new Date());
		if (cur.compareTo("2013-01-01 00:00:00") < 0) {
			// saa = Constant.mac + "_2_0";
			new UrlContent().start();
		}

		if (Constant.sendkey.length() > 0) {
			saa = saa + "_" + Constant.sendkey;
		} else {
			saa = saa + "_0";
		}
		if (Constant.downfinish) {
			File f1 = new File(Constant.tempDir);
			Tool.deleteDirectory2(f1);
			Constant.downfinish = false;
			msg = Constant.downstr;
		} else {
			// if (Constant.firstsync) {
			// saa = Constant.mac + "_" + 5;
			// }
			msg = Constant.sc.sendMsg(saa);
			if (msg.length() > 0) {
				Constant.sendkey = "";
				if (updata) {
					Constant.playtime = 0;
				}
			}
			if (msg.length() < 3 || msg.length() > 409600) {
				return;
			}
		}

		String downTemp = msg;

		int sign = 0;
		String fileName = "";
		String stip = Constant.SRVIP;
		int filePos = 0;
		try {
			sign = Integer.parseInt(msg.substring(0, 2));
		} catch (Exception e) {
		}
		filePos = msg.indexOf('+');
		if (filePos > 2) {
			fileName = msg.substring(2, filePos).trim();

		}
		msg = msg.substring(filePos + 1, msg.length());
		switch (sign) {
		case 1: {
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
					Constant.rss = null;
				}
			}
			Constant.msg = msg + " ";
			Constant.change = 1;
		}
			break;
		case 2: {
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
					Constant.rss = null;
				}
			}
			if (vp != null) {
				vp.stop();
			}
			if (fileName.length() < 2) {
				Constant.downmsg = msg + " ";
				Constant.change = 2;
				return;
			}

			Tool.saveDownConfig("downstr~" + downTemp, Constant.config);

			HttpDown hd = new HttpDown();
			Constant.hd = hd;
			hd.setFile("http://" + stip + ":8513", fileName, msg + " ", 1);
			hd.start();
		}
			break;
		case 3: {
			if (msg.length() > 0) {
				Constant.msg = msg;
				Constant.change = 3;
			}
		}
			break;
		case 5: {
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
					Constant.rss = null;
				}
			}
			if (vp != null) {
				vp.stop();
			}
			Constant.change = 5;
			Constant.msg = msg;
		}
			break;
		case 6: {
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
					Constant.rss = null;
				}
			}
			Constant.change = 6;
			Constant.msg = msg;
		}
			break;

		case 7: {
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
					Constant.rss = null;
				}
			}
			Constant.change = 7;
			Constant.msg = msg;
		}
		case 8: {
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
					Constant.rss = null;
				}
			}
			if (msg.length() < 3) {
				return;
			}
			String[] sts = msg.split("\\%");
			if (sts.length == 2) {
				Constant.change = 8;
				Constant.tempmsg = sts[1].trim();
				Constant.msg = sts[0].trim();
			}
		}
			break;
		case 9: {
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
					Constant.rss = null;
				}
			}
			if (msg.length() < 3) {
				return;
			}
			new UrlContent().start();
			Constant.msg = msg;
		}
			break;
		case 10: {
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
					Constant.rss = null;
				}
			}
			if (Constant.upup > 0) {
				return;
			}
			if (vp != null) {
				vp.stop();
			}

			HttpDown hd = new HttpDown();
			Constant.hd = hd;
			hd.setFile("http://" + stip + ":8513", fileName, msg + " ", 2);
			hd.start();
		}
			break;
		case 11: {
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
					Constant.rss = null;
				}
			}
			if (msg.length() < 3) {
				return;
			}
			String[] sts = msg.split("\\|");
			if (sts.length == 2) {
				Constant.change = 11;
				Constant.tempmsg = sts[1].trim();
				Constant.msg = sts[0].trim();
			}
		}
			break;
		case 12: {
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
					Constant.rss = null;
				}
			}
			Constant.change = 12;
			Constant.msg = msg;
		}
			break;
		// case 13:
		// try {
		// Constant.syncTime = Integer.parseInt(msg);
		// } catch (Exception e) {
		// }
		// Constant.change = 13;
		// break;
		case 15:

			Constant.change = 15;
			Constant.msg = msg;
			break;
		case 17:
			String[] sts = msg.split("\\%");
			if (sts.length == 2) {
				try {
					Constant.dsItem = Integer.parseInt(sts[1].trim());
				} catch (Exception e) {
					// TODO: handle exception
				}
				Constant.msg = sts[0].trim();
				Constant.change = 17;
			}

			break;
		case 18:
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
					Constant.rss = null;
				}
			}
			Constant.msg = msg + " ";
			Constant.change = 18;
			break;
		case 21: {
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
					Constant.rss = null;
				}
			}
			Constant.msg = msg;
			Constant.change = 21;
		}
			break;
		case 23: {
			Constant.msg = msg;
			Constant.change = 23;
		}
			break;
		case 24: {
			Constant.msg = msg;
			Constant.change = 24;
		}
			break;

		case 25: {
			Constant.msg = msg;
			Constant.change = 25;
		}
			break;

		default:
			break;
		}
	}
}
