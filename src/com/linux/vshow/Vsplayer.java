package com.linux.vshow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Gpio;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsoluteLayout;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.Toast;

import com.softwinner.tv.CVBS;
import com.zckj.setting.Menu;

public class Vsplayer extends Activity implements OnTouchListener,
		OnKeyListener {

	private AbsoluteLayout abslayout = null;
	private VideoPlayer vp = null;
	private CVBS cvbs = null;
	private ShowCamera showCamera = null;
	private Mp3Player mp = null;
	private ImageSee ims = null;
	private EditText et = null;
	private WebView tqweb = null;
	private WebView wv = null;
	private WebView timeweb = null;
	private WebView hlweb = null;
	private WebView tweb = null;
	private WebView excelWeb = null;

	private Matrix matrix = null;
	private Matrix savedMatrix = null;
	private PointF start = null;
	private PointF mid = null;

	private PopupWindow mPoupuWindow = null;
	private ImageView iView = null;

	private Console console = null;
	private EditText tvColse1 = null;
	private EditText tvColse1_2 = null;
	private EditText tvOpen1 = null;
	private EditText tvopen1_2 = null;

	private EditText tvColse2 = null;
	private EditText tvColse2_2 = null;
	private EditText tvOpen2 = null;
	private EditText tvOpen2_2 = null;

	private EditText tvColse3 = null;
	private EditText tvColse3_2 = null;
	private EditText tvOpen3 = null;
	private EditText tvOpen3_2 = null;

	private Bitmap newBm = null;

	private DisplayMetrics dm = null;

	private MainServer ms = null;

	private ScrollFont sf = null;

	// private TextView elenumberTv = null;
	// private TextView elenumberInfo = null;

	private Bitmap toConformBitmap(Bitmap background, Bitmap foreground) {
		Bitmap newbmp = Bitmap.createBitmap(Constant.width, Constant.height,
				Config.ARGB_8888);
		Canvas cv = new Canvas(newbmp);
		cv.drawBitmap(background, 0, 0, null);
		cv.drawBitmap(foreground, Constant.vx, Constant.vy, null);
		cv.save(Canvas.ALL_SAVE_FLAG);
		cv.restore();
		return newbmp;
	}

	public void upImg(String str) {
		String videoinfo = "0";
		if (vp != null) {
			videoinfo = vp.pause();
		}
		String imgfile = Constant.vx + "_" + Constant.vy + "_"
				+ Constant.vwidth + "_" + Constant.vheight + "_" + videoinfo
				+ "#" + str;
		View view = getWindow().getDecorView();
		if (view != null) {
			Constant.li.writeLog("0000 " + R.string.log1);
			view.setDrawingCacheEnabled(true);
			view.buildDrawingCache();
			Bitmap b1 = view.getDrawingCache();
			Bitmap b = null;
			FileOutputStream fos = null;
			if (b1 != null) {
				b = Bitmap.createBitmap(b1, 0, 0, Constant.width,
						Constant.height);
				view.destroyDrawingCache();
				if (b != null) {
					try {
						fos = new FileOutputStream(Constant.tempDir + imgfile);
						if (fos != null) {
							b.compress(Bitmap.CompressFormat.PNG, 100, fos);
							fos.flush();
							fos.close();
							new UpImg(new File(Constant.tempDir + imgfile),
									"http://" + Constant.SRVIP
											+ ":8512/vs/upimg.vs").start();
						}
					} catch (Exception e) {
						Constant.li.writeLog("0000 " + R.string.log2);
					}
				}
			}
		}
		if (vp != null) {
			try {
				vp.start();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	public void lockScreen() {

		if (console == null) {
			console = new Console("/dev/ttyS2", 9600);
		}
		byte[] bytes = new byte[3];
		bytes[0] = (byte) 0xE0;
		bytes[1] = (byte) 0x05;
		bytes[2] = (byte) 0xE5;
		try {
			console.send(bytes, 0, 3);
		} catch (Exception e) {
		}

		bytes = new byte[3];
		bytes[0] = (byte) 0xE2;
		bytes[1] = (byte) 0x05;
		bytes[2] = (byte) 0xE7;
		try {
			Thread.sleep(1000);
			console.send(bytes, 0, 3);
		} catch (Exception e) {
		}
		// Tool.disable_watchdog();
		// Constant.upup++;
		// stopPlay();
		// try {
		// if (Constant.WAKELOCK != null) {
		// try {
		// Constant.WAKELOCK.release();
		// } catch (Exception e) {
		// // TODO: handle exception
		// }
		// Constant.WAKELOCK = null;
		// } else {
		// PowerManager pm = (PowerManager)
		// getSystemService(Context.POWER_SERVICE);
		// pm.goToSleep(SystemClock.uptimeMillis());
		// }
		// PowerManager pm = (PowerManager)
		// getSystemService(Context.POWER_SERVICE);
		// Constant.WAKELOCK = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
		// "");
		// Constant.WAKELOCK.acquire();
		File f = new File("/sys/devices/platform/disp/hdmi_ctrl");
		if (f.exists()) {
			try {
				writeFile(1);
				Constant.upup++;
				stopPlay();
				Constant.guanji = true;
				Constant.li.writeLog("0000 " + R.string.log3);
			} catch (Exception e) {
				Constant.li.writeLog("0000 " + R.string.log4);
			}
		}
		// } catch (Exception e) {
		// Constant.li.writeLog("0000 " + R.string.log4);
		// }
	}

	public void lockScreen2() {
		onDestroy2();
		Intent ite = new Intent(this, BootUpReceiver.class);
		ite.setAction("myAction");
		PendingIntent SENDER = PendingIntent.getBroadcast(this, 0, ite,
				PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager ALARM = (AlarmManager) getSystemService(ALARM_SERVICE);
		ALARM.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 3000,
				SENDER);
		finish();
	}

	private void wakeup() {

		Constant.li.writeLog("0000 " + R.string.log5);
		// if (Constant.WAKELOCK != null) {
		// try {
		// Constant.WAKELOCK.release();
		// } catch (Exception e) {
		// }
		// Constant.WAKELOCK = null;
		// }
		//
		// PowerManager pm = (PowerManager)
		// getSystemService(Context.POWER_SERVICE);
		// Constant.WAKELOCK = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
		// | PowerManager.ACQUIRE_CAUSES_WAKEUP, "");
		// Constant.WAKELOCK.acquire();

		File f = new File("/sys/devices/platform/disp/hdmi_ctrl");
		if (f.exists()) {
			try {
				writeFile(0);
				Constant.guanji = false;
			} catch (Exception e) {
				Constant.li.writeLog("0000 " + R.string.log4);
			}
		}
		if (console == null) {
			console = new Console("/dev/ttyS2", 9600);
		}
		byte[] bytes = new byte[3];
		bytes[0] = (byte) 0xE0;
		bytes[1] = (byte) 0x05;
		bytes[2] = (byte) 0xE5;
		try {
			console.send(bytes, 0, 3);
		} catch (Exception e) {
		}

		bytes = new byte[3];
		bytes[0] = (byte) 0xE1;
		bytes[1] = (byte) 0x05;
		bytes[2] = (byte) 0xE6;
		try {
			Thread.sleep(1000);
			console.send(bytes, 0, 3);
		} catch (Exception e) {
		}
		init();
	}

	public boolean isConnect() {
		try {
			ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {
					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		} catch (Exception e) {
		}
		return false;
	}

	private boolean gpiod = false;

	private void timeDo() {

		if (Constant.dsItem > 0) {
			if (Constant.total % Constant.dsItem == 0 && Constant.total != 0) {
				Constant.change = 13;
			}
		}

		if (Constant.dpTimeCount) {
			Constant.dpRunTime++;
			if (Constant.dpRunTime == Constant.dpTime) {
				Constant.dpTimeCount = false;
				Constant.dpRunTime = 0;
				Constant.dpIsStop = false;
				init();
			}
		}
		// if (Constant.total % 2 == 0) {
		// Tool.feed_watchdog();
		//
		// }

		// 串口
		// if (Constant.eleopen) {
		// if (!Constant.msgString.equals("")) {
		// // 30 78 44 30 上升
		// // 30 78 45 30 下降
		// // 30 78 45 32 停留
		// if (Constant.msgString.trim().equals("0xD0")) {
		// Message message = new Message();
		// message.what = 1;
		// handler.sendMessage(message);
		// } else if (Constant.msgString.trim().equals("0xE0")) {
		// Message message = new Message();
		// message.what = 2;
		// handler.sendMessage(message);
		// } else if (Constant.msgString.trim().equals("0xE2")) {
		// Message message = new Message();
		// message.what = 3;
		// handler.sendMessage(message);
		// }
		//
		// Constant.msgString = "";
		// }
		// }

		if (Constant.guanji) {
			Constant.total++;
			if (Constant.total % Constant.lian == 0) {
				new Connect2().start();
			}
			if (Constant.change == 7) {
				Constant.change = 0;
				wakeup();
				return;
			} else if (Constant.change == 12) {
				Constant.change = 0;
				Tool.saveConfig("sendkey!" + Constant.msg, Constant.config);
				onDestroy2();
				Intent intent = new Intent(Intent.ACTION_REBOOT);
				intent.putExtra(Intent.EXTRA_KEY_CONFIRM, false);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				return;
			}
			return;
		}

		if (Constant.total > 0) {
			if (Constant.curday != getWeekOfDate()) {
				init();
				return;
			}
		}

		if (Constant.outsideCount1 && Constant.outsideCount2
				&& Constant.outsideCount3 && Constant.outsideCount4) {

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			et = new EditText(this);
			if (Constant.tuichu.length() > 0) {
				builder.setTitle(R.string.quit).setView(et);
			} else {
				builder.setTitle(R.string.quit);
			}
			builder.setPositiveButton(R.string.confirm,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog1, int btn) {
							Constant.leftdown = false;
							Constant.lefttotal = 0;
							if (et.getText().toString().trim()
									.equals(Constant.tuichu)) {
								// Tool.disable_watchdog();
								Constant.li.writeLog("0000 " + R.string.log6);
								onDestroy2();
								finish();
								return;
							}
						}
					});
			builder.setNegativeButton(R.string.cancel,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog1, int btn) {
							Constant.outsideCount1 = false;
							Constant.outsideCount2 = false;
							Constant.outsideCount3 = false;
							Constant.outsideCount4 = false;
						}
					});
			builder.create().show();
			Constant.outsideCount1 = false;
			Constant.outsideCount2 = false;
			Constant.outsideCount3 = false;
			Constant.outsideCount4 = false;
		}
		if (Constant.total % Constant.lian == 0) {
			if ((Constant.total != 0)
					&& (Constant.total % (Constant.lian * 6) == 0)) {
				new Connect(vp, true).start();
			} else {
				new Connect(vp, false).start();
			}

			if (Constant.dayStr == 8 || Constant.dayStr == Constant.curday) {
				SimpleDateFormat formatter = new SimpleDateFormat("HH-mm");
				String nowTimeStr = "";
				int nowTime = 0;
				String hour = "";
				String minute = "";
				try {
					nowTimeStr = formatter.format(new Date());
				} catch (Exception e) {
				}
				String nowStr[] = nowTimeStr.split("\\-");
				if (nowStr.length == 2) {
					int nowHour = Integer.parseInt(nowStr[0].trim()) * 60;
					int nowMinute = Integer.parseInt(nowStr[1].trim());
					nowTime = nowHour + nowMinute;
				}
				if (!Constant.CloseTime.equals("")) {

					if ((Constant.CloseTimes - nowTime) == 2) {

						if (console == null) {
							console = new Console("/dev/ttyS2", 9600);
						}
						byte[] bytes = new byte[3];
						bytes[0] = (byte) 0xE0;
						bytes[1] = (byte) 0x05;
						bytes[2] = (byte) 0xE5;
						try {
							console.send(bytes, 0, 3);
						} catch (Exception e) {
						}

						bytes = new byte[3];
						bytes[0] = (byte) 0xE2;
						bytes[1] = (byte) 0x05;
						bytes[2] = (byte) 0xE7;
						try {
							Thread.sleep(1000);
							console.send(bytes, 0, 3);
						} catch (Exception e) {
						}

					}
				}
			}
		}

		if (!Constant.tbtime.equals("")) {
			SimpleDateFormat formatter = new SimpleDateFormat("HH-mm-ss");
			String nowTimeStr = "";
			int nowTime = 0;
			String hour = "";
			String minute = "";
			int tbtimes = 0;
			try {
				nowTimeStr = formatter.format(new Date());
			} catch (Exception e) {
			}
			String nowStr[] = nowTimeStr.split("\\-");
			if (nowStr.length == 3) {
				int nowHour = Integer.parseInt(nowStr[0].trim()) * 60;
				int nowMinute = Integer.parseInt(nowStr[1].trim());
				nowTime = nowHour + nowMinute
						+ Integer.parseInt(nowStr[2].trim());
			}
			// 08:30:00
			String tbtime[] = Constant.tbtime.split("\\:");
			if (tbtime.length == 3) {
				int tbtimeHour = Integer.parseInt(tbtime[0].trim()) * 60;
				int nowMinute = Integer.parseInt(nowStr[1].trim());
				tbtimes = tbtimeHour + nowMinute
						+ Integer.parseInt(tbtime[2].trim());
			}
			if (tbtimes == nowTime) {
				new UrlContent().start();
			}
		}

		if (Constant.tqagain > 0) {
			if (Constant.total % Constant.tqagain == 0) {
				if (Constant.tqurl == 0) {
					DownTq dowf = new DownTq("http://" + Constant.SRVIP
							+ ":8512/vs/showtq.vs?key=" + Constant.tqkey
							+ "&dx=" + Constant.tqsize + "&ys="
							+ Constant.tqcolor + "&imgsize="
							+ Constant.tqimgsize, Constant.TQDIR + "/tq.html");
					dowf.start();
				} else {
					DownTq dowf = new DownTq("http://" + Constant.SRVIP
							+ ":8512/vs/showtq2.vs?key=" + Constant.tqkey
							+ "&dx=" + Constant.tqsize + "&ys="
							+ Constant.tqcolor + "&imgsize="
							+ Constant.tqimgsize, Constant.TQDIR + "/tq.html");
					dowf.start();
				}
			}
		}

		if (Constant.hltime > 0) {
			if (Constant.total % Constant.hltime == 0 && Constant.total != 0) {
				DownHl dohl = new DownHl(Constant.hlkey, Constant.HLDIR
						+ "/hl.html");
				dohl.start();
				Constant.li.writeLog("0000 " + R.string.log7);
			}

		}

		if (Constant.camera == 2) {
			if (Constant.total % (Constant.lian * 6) == 0) {
				if (Constant.rcamera != null) {
					Constant.rcamera.stopRecorder();
				}
				SimpleDateFormat dsday = new SimpleDateFormat("yyyy_MM_dd");
				String ffss = "";
				if (Constant.cameraType == 0) {
					ffss = Constant.cDir + File.separator
							+ dsday.format(new Date());
				} else if (Constant.cameraType == 1) {
					ffss = Environment.getExternalStorageDirectory()
							.getAbsolutePath()
							+ File.separator
							+ "camera"
							+ File.separator + dsday.format(new Date());
				} else if (Constant.cameraType == 2) {
					ffss = "/mnt/extsd/camera" + File.separator
							+ dsday.format(new Date());
				} else if (Constant.cameraType == 3) {
					ffss = "/mnt/sata/camera" + File.separator
							+ dsday.format(new Date());
				}
				File fsd = new File(ffss);
				if (!fsd.exists()) {
					fsd.mkdir();
				}
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy_MM_dd_HH_mm_ss");
				Constant.rcamera.startRecorder(ffss + File.separator
						+ sdf.format(new Date()) + ".3gp");
				Constant.li.writeLog("0000 " + R.string.log8);
			}
		}

		Constant.total++;

		if (Constant.camera == 1) {
			int gpio_status = Gpio.readGpio('h', 9);
			// int gpio_status = Gpio.readGpio('i', 3);
			if (gpio_status == 1) {
				if (!gpiod) {
					gpiod = true;
					SimpleDateFormat dsday = new SimpleDateFormat("yyyy_MM_dd");
					String ffss = Constant.cDir + File.separator
							+ dsday.format(new Date());
					File fsd = new File(ffss);
					if (!fsd.exists()) {
						fsd.mkdir();
					}
					SimpleDateFormat sdf = new SimpleDateFormat(
							"yyyy_MM_dd_HH_mm_ss");
					try {
						Constant.sPic.takePicture(ffss + File.separator
								+ sdf.format(new Date()) + ".jpg");
						Constant.li.writeLog("0000 " + R.string.log9 + ffss
								+ File.separator + sdf.format(new Date())
								+ ".jpg");
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			} else {
				gpiod = false;
			}
		}
		if (Constant.light == 1) {
			int gpio_status = Gpio.readGpio('h', 9);
			// int gpio_status = Gpio.readGpio('i', 3);
			if (gpio_status == 1) {
				if (Constant.dodod) {
					Constant.currs = 0;
					Constant.dodod = false;
					AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
					am.setStreamMute(AudioManager.STREAM_MUSIC, false);
					Gpio.writeGpio('h', 12, 1);
					// Gpio.writeGpio('h', 26, 1);
				}
			} else {
				if (!Constant.dodod) {
					Constant.currs++;
					if (Constant.currs >= Constant.lighttime) {
						Constant.dodod = true;
						AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
						am.setStreamMute(AudioManager.STREAM_MUSIC, true);
						Gpio.writeGpio('h', 12, 0);
						// Gpio.writeGpio('h', 26, 0);
					}
				}
			}

		}

		if ((Constant.VIDEOAGAIN > 0) && (!Constant.chu) && (!Constant.itemgo)) {
			if (Constant.total % Constant.VIDEOAGAIN == 0) {
				if (vp != null || ims != null) {
					try {
						int cu = 0;
						if (Constant.playList.size() > 0) {
							PlayItem pl = Constant.playList
									.get(Constant.curplay);
							cu = pl.curtime;
						}
						OutputStreamWriter fw = new OutputStreamWriter(
								new FileOutputStream(Constant.config3));
						if (vp != null) {
							fw.write(Constant.modelcontent + "_"
									+ Constant.curplay + "_" + cu + "_"
									+ Constant.avlcur + "_" + vp.getcur());
							Constant.li.writeLog("0000 " + R.string.log10 + ":"
									+ vp.getcur());
						} else {
							fw.write(Constant.modelcontent + "_"
									+ Constant.curplay + "_" + cu + "_"
									+ Constant.avlcur + "_" + "0_0");
						}
						fw.close();
						Tool.enable_sync(Constant.config3);

					} catch (Exception e) {

					}

				}
			}
		}
		SimpleDateFormat sf2 = new SimpleDateFormat("HH:mm:ss");
		String curat = sf2.format(new Date());
		SimpleDateFormat sf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dString = sf3.format(new Date());
		if ((!Constant.nexttime.isEmpty())
				&& (Constant.nexttime.compareTo(curat) <= 0)
				&& !Constant.dpStart) {
			Constant.showInfo = true;
			Constant.cblbStr = this.getString(R.string.log11);
			init();

			return;
		}
		if ((!Constant.nextfontTime.isEmpty())
				&& (compareFontTimeDay(dString, Constant.nextfontTime))) {
			Constant.nextfontTime = "";
			init();
			return;
		}

		if (Constant.change != 0) {
			if (Constant.change == 1) {
				Constant.change = 0;
				String font[] = Constant.msg.split("\\#");
				if (font.length == 2) {
					Tool.saveConfig(font[0], Constant.config);
					Tool.saveConfig(font[1], Constant.config);
				} else {
					String msg[] = Constant.msg.split("\\%");
					if (msg.length == 2) {
						try {
							if (msg[1].split("\\!")[0].equals("shut")) {
								try {
									Tool.saveConfig(msg[1], Constant.config);
								} catch (Exception e) {
									// TODO: handle exception
								}
							} else {
								Tool.saveConfig(msg[1], Constant.config);
							}
						} catch (Exception e) {
							// TODO: handle exception
						}

						try {
							Tool.saveConfig(msg[0], Constant.config);
						} catch (Exception e) {
							// TODO: handle exception
						}
					} else {
						Tool.saveConfig(Constant.msg, Constant.config);
					}
				}
				init();
				initSetOff();
				return;

			} else if (Constant.change == 2) {
				Constant.change = 0;
				Tool.saveConfig(Constant.downmsg, Constant.config);
				init();
				return;
			} else if (Constant.change == 3) {
				Constant.change = 0;
				upImg(Constant.msg);
			} else if (Constant.change == 5) {
				Constant.li.writeLog("0000 " + R.string.log12);
				Constant.curmodel = "";
				Constant.change = 0;
				if (!Constant.deldo) {
					Constant.deldo = true;
					// Tool.disable_watchdog();
					stopPlay();

					if (new File(Constant.cDir + ".zip").exists()) {
						new File(Constant.cDir + ".zip").delete();
					}
					Tool.saveConfig("playlist! " + "%timelist! " + "%cblist! "
							+ "%dplist! ", Constant.config);
					Tool.saveConfig("downtotal!0%alltotal!0", Constant.config3);
					File f1 = new File(Constant.tDir);
					File f2 = new File(Constant.fDir);
					File f3 = new File(Constant.offDir);
					File f4 = new File(Constant.cDir);
					Tool.deleteDirectory(f1);
					f1.mkdir();
					Tool.deleteDirectory(f2);
					f2.mkdir();
					Tool.deleteDirectory(f3);
					f3.mkdir();
					Tool.deleteDirectory(f4);
					f4.mkdir();
					// Tool.enable_watchdog();
					Constant.deldo = false;
					Tool.saveConfig(Constant.msg, Constant.config);
					init();
					return;
				}
			} else if (Constant.change == 6) {
				Constant.change = 0;
				Tool.saveConfig("sendkey!" + Constant.msg, Constant.config);
				Constant.sendkey = Constant.msg;
				lockScreen();
				return;
			} else if (Constant.change == 8) {
				Constant.change = 0;
				int sss = 0;
				try {
					sss = Integer.parseInt(Constant.tempmsg);
					AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
					am.setStreamVolume(AudioManager.STREAM_MUSIC, sss, 0);
					Tool.saveConfig(Constant.msg, Constant.config);
					String[] ssss = Constant.msg.split("\\!");
					if (ssss.length == 2) {
						Constant.sendkey = ssss[1].trim();
					}
					Constant.li.writeLog("0000 " + R.string.log13 + sss);
				} catch (Exception e) {
					Constant.li.writeLog("0000 " + R.string.log14);
				}
			} else if (Constant.change == 9) {
				Constant.change = 0;
				Constant.li.writeLog("0000 "
						+ getResources().getString(R.string.log15));
				try {
					Constant.nowTimes = Constant.nowTimes.replaceAll("-", "_");
					Constant.nowTimes = Constant.nowTimes.replaceAll(":", "_");
					String[] strs = Constant.nowTimes.split("\\_");

					if (strs.length == 6) {
						Calendar _Calendar = Calendar.getInstance();
						_Calendar.set(Integer.parseInt(strs[0]),
								Integer.parseInt(strs[1]) - 1,
								Integer.parseInt(strs[2]),
								Integer.parseInt(strs[3]),
								Integer.parseInt(strs[4]),
								Integer.parseInt(strs[5]));
						long when = _Calendar.getTimeInMillis();
						SystemClock.setCurrentTimeMillis(when);
						Tool.saveConfig(Constant.msg, Constant.config);
						lockScreen2();
					}
				} catch (Exception e) {
					Constant.li.writeLog("0000 "
							+ getResources().getString(R.string.log16));
				}
				return;
			} else if (Constant.change == 10) {
				Constant.change = 0;
				Tool.saveConfig("sendkey!" + Constant.msg, Constant.config);
				// Tool.disable_watchdog();
				onDestroy2();
				Intent ite = new Intent(this, BootUpReceiver.class);
				ite.setAction("myAction");
				PendingIntent SENDER = PendingIntent.getBroadcast(this, 0, ite,
						PendingIntent.FLAG_CANCEL_CURRENT);
				AlarmManager ALARM = (AlarmManager) getSystemService(ALARM_SERVICE);
				ALARM.set(AlarmManager.RTC_WAKEUP,
						System.currentTimeMillis() + 90000, SENDER);
				Tool.saveConfig("update!1", Constant.config2);
				Tool.install(Constant.updateDir + Constant.downmsg.trim());
				Constant.li.writeLog("0000 " + R.string.log17);
				finish();
				return;
			} else if (Constant.change == 11) {
				Constant.change = 0;
				if (Constant.tempmsg.length() > 2) {
					Tool.saveConfig(Constant.tempmsg, Constant.config2);
					Tool.saveConfig(Constant.msg, Constant.config);
					init();
				}
				return;
			} else if (Constant.change == 12) {
				Constant.li.writeLog("0000 " + R.string.log18);
				Constant.change = 0;
				Tool.saveConfig("sendkey!" + Constant.msg, Constant.config);
				onDestroy2();
				Intent intent = new Intent(Intent.ACTION_REBOOT);
				intent.putExtra(Intent.EXTRA_KEY_CONFIRM, false);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				return;
			} else if (Constant.change == 13) {
				Toast.makeText(Vsplayer.this, 13 + "", 1).show();
				Constant.change = 0;
				Constant.li.writeLog("0000 " + R.string.log19);
				Constant.change = 0;
				int Videocount = 0;
				String str = "";

				int nowVideoTime = 0;
				int nowVideoPlays = 0;

				SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
				Date curDate = new Date(System.currentTimeMillis());
				str = formatter.format(curDate);

				Constant.syncTime = syncTime(str);
				if (vp != null) {
					try {
						Videocount = Constant.syncTime % Constant.allVideoTime;
						Constant.nowTime = Videocount;
					} catch (Exception e) {
						// TODO: handle exception
					}
					int playtime = 0;
					for (int i = 0; i < vp.array.size(); i++) {
						if (i < vp.VideoTime.size()) {
							playtime = vp.VideoTime.get(i);
						}
						if (Videocount <= playtime) {
							nowVideoTime = Videocount;
							nowVideoPlays = i;
							break;
						} else {
							Videocount -= playtime;
						}
					}
					// vp.initToto(nowVideoTime);
					if (nowVideoPlays == vp.cur) {
						int videoTime = vp.mediaPlayer.getCurrentPosition() / 1000;
						if (videoTime - nowVideoTime > Constant.dsTime) {
							Toast.makeText(Vsplayer.this,
									" seekTo" + nowVideoTime, 1).show();
							if (Constant.firstVideo) {
								Constant.videototo = nowVideoTime;
							} else {
								vp.mediaPlayer.seekTo(nowVideoTime * 1000);
							}
						}
						Toast.makeText(Vsplayer.this, "same", 1).show();
					} else {
						try {
							Toast.makeText(
									Vsplayer.this,
									"new " + nowVideoPlays + " seekTo"
											+ nowVideoTime, 1).show();
							if (Constant.firstVideo) {
								Constant.videototo = nowVideoTime;
								Constant.videocur = nowVideoPlays;
								vp.cur = nowVideoPlays;
							} else {
								vp.cur = nowVideoPlays;
								vp.mediaPlayer.reset();
								vp.mediaPlayer.setDataSource(vp.array
										.get(nowVideoPlays));
								vp.mediaPlayer.prepare();
								vp.mediaPlayer.start();
								int posiont = nowVideoTime * 1000;
								vp.mediaPlayer.seekTo(posiont);
							}
						} catch (Exception e) {

						}
					}
				}
				// if (Constant.tpvList.size() > 0) {
				// for (int i = 0; i < Constant.tpvList.size(); i++) {
				// ((TurnPageView) Constant.tpvList.get(i)).initGotime();
				// }
				// }
				if (Constant.imglist.size() > 0) {

					for (int i = 0; i < Constant.imglist.size(); i++) {
						Constant.changePlaysImage = true;
						((SeeImageView) Constant.imglist.get(i)).initGotime();
					}

				}

			} else if (Constant.change == 15) {
				Constant.li.writeLog("0000 " + R.string.log20);
				Constant.change = 0;
				Constant.zipMsgStr = Constant.msg;
				Constant.zipFileStr = Environment.getExternalStorageDirectory()
						.getAbsolutePath()
						+ File.separator
						+ Constant.zipMsgStr + ".zip";

				DoZip zip = new DoZip(Constant.LogDir, Constant.zipFileStr);
				zip.start();

			} else if (Constant.change == 16) {
				Constant.change = 0;
				new UpImg(new File(Constant.zipFileStr), "http://"
						+ Constant.SRVIP + ":8512/vs/uplog.vs?key="
						+ Constant.zipMsgStr).start();
				try {
					Thread.sleep(1000);
				} catch (Exception e) {

				}
				File f1 = new File(Constant.zipFileStr);
				Tool.deleteDirectory(f1);
			} else if (Constant.change == 17) {
				// Constant.li.writeLog("0000 " + R.string.log28);
				Constant.change = 13;
				Tool.saveConfig("dsitem!" + Constant.dsItem, Constant.config2);
			}
			if (Constant.change == 18) {
				// sendkey!2014-05-14-17:03:34-1-5993%tbtime!08:30:00
				Constant.change = 0;
				String initTime[] = Constant.msg.split("\\%");
				if (initTime.length == 2) {
					String tbtime[] = initTime[1].trim().split("\\!");
					if (tbtime.length == 2) {
						Constant.tbtime = tbtime[1].trim();
						Tool.saveConfig("tbtime!" + Constant.tbtime,
								Constant.config2);
					}
				}
				return;
			} else if (Constant.change == 20) {
				Constant.change = 0;
				if (vp != null) {
					vp.next();
				}
				if (mp != null) {
					mp.next();
				}
			} else if (Constant.change == 21) {
				Constant.change = 0;
				try {
					String[] strs = Constant.msg.split("\\_");
					if (strs.length == 6) {
						Calendar _Calendar = Calendar.getInstance();
						_Calendar.set(Integer.parseInt(strs[0]),
								Integer.parseInt(strs[1]) - 1,
								Integer.parseInt(strs[2]),
								Integer.parseInt(strs[3]),
								Integer.parseInt(strs[4]),
								Integer.parseInt(strs[5]));
						long when = _Calendar.getTimeInMillis();
						SystemClock.setCurrentTimeMillis(when);
						lockScreen2();
					}
				} catch (Exception e) {
				}
				return;
			} else if (Constant.change == 22) {
				Constant.change = 0;
				if (tqweb != null) {
					tqweb.clearView();
					abslayout.removeView(tqweb);
					tqweb = null;
				}
				Constant.li.writeLog("0000 " + R.string.log21);
				inittq();
			} else if (Constant.change == 23) {
				Constant.li.writeLog("0000 " + R.string.log22);
				Constant.change = 0;
				Constant.upCameraFile = true;
				upCamera();
				Tool.saveConfig("sendkey!" + Constant.msg, Constant.config);
			} else if (Constant.change == 24) {
				Constant.li.writeLog("0000 " + R.string.log23);
				Constant.change = 0;
				StatFs statFs = new StatFs(Constant.sdcardDir);
				long blockSize = statFs.getBlockSize();
				long totalBlocks = statFs.getBlockCount() * blockSize;
				long availableBlocks = statFs.getAvailableBlocks() * blockSize;
				long useBlocks = totalBlocks - availableBlocks;
				AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
				int current = mAudioManager
						.getStreamVolume(AudioManager.STREAM_MUSIC);
				String shutStr = "";
				if (!Constant.shut.equals("")) {
					String str[] = Constant.shut.split("\\^");
					try {
						shutStr = str[0].trim();
					} catch (Exception e) {
					}
				}
				UpBlockSize ubs = new UpBlockSize("http://" + Constant.SRVIP
						+ ":8512/vs/upsize.vs?size=" + useBlocks + "/"
						+ totalBlocks + "/" + Constant.version + "/" + current
						+ "/" + shutStr + "&mark=" + Constant.mac);
				ubs.start();

			} else if (Constant.change == 25) {

				Constant.change = 0;
				String str[] = Constant.msg.split("\\%");
				if (str.length == 2) {
					Tool.saveConfig("sendkey!" + str[0].trim(), Constant.config);
					try {
						Constant.cameraType = Integer.parseInt(str[1].trim());
					} catch (Exception e) {
					}

				}
				Constant.li.writeLog("0000 " + R.string.log24
						+ Constant.cameraType);

			} else if (Constant.change == 26) {
				Constant.change = 0;
				Constant.li.writeLog("0000 " + R.string.log25);
				if (hlweb != null) {
					hlweb.clearView();
					abslayout.removeView(hlweb);
					hlweb = null;
				}
				inithl();
			} else if (Constant.change == 27) {
				Constant.change = 0;
				if (Constant.playList.size() > 0) {
					Constant.curplay = 0;
					againPlay(Constant.curplay);
				}
				Constant.li.writeLog("0000 " + R.string.log26);
			} else if (Constant.change == 28) {
				Constant.change = 0;
				if (Constant.playList.size() > 0) {
					Constant.curplay++;
					if (Constant.curplay >= Constant.playList.size()) {
						Constant.curplay = 0;
					}
					againPlay(Constant.curplay);
				}
				Constant.li.writeLog("0000 " + R.string.log27);
			} else if (Constant.change == 29) {
				Constant.li.writeLog("0000 " + R.string.log28);
				Constant.change = 0;
				init();
			} else if (Constant.change == 40) {
				Constant.change = 0;
				Constant.upCameraFile = false;
				new UpImg(new File(Constant.cDir + ".zip"), "http://"
						+ Constant.SRVIP + ":8512/vs/upcamera.vs?key="
						+ Constant.mac.replaceAll(":", "-")).start();
				File f1 = new File(Constant.cDir);
				Tool.deleteDirectory(f1);
				f1.mkdir();

			} else if (Constant.change == 41) {
				Constant.change = 0;
				Constant.change = 5;
				Tool.uninstall(Constant.textApk);
				Tool.uninstall(Constant.fontApk);
				Tool.uninstall(Constant.serviceapk);
				Message message = new Message();
				message.what = 2;
				Constant.handler.sendMessage(message);

			}
		}

		if (Constant.downing) {
			Tool.saveConfig("downtotal!" + Constant.downtotal + "%alltotal!"
					+ Constant.alltotal, Constant.config3);
		}

		if (Constant.chu || Constant.itemgo) {
			if (Constant.itemgo) {
				return;
			}
			Constant.chutime--;
			if (Constant.chutime <= 0) {
				Constant.chu = false;
				if (Constant.playList.size() > 1) {
					PlayItem pl = Constant.playList.get(Constant.curplay);
					pl.curtime = 0;
					Constant.afvideo = false;
					againPlay(Constant.curplay);
					return;
				}
			}
		} else {
			if (Constant.playList.size() > 1) {

				PlayItem pl = Constant.playList.get(Constant.curplay);
				if (Constant.changePlays) {
					pl.curtime = Constant.nowTime;
					Constant.changePlays = false;
					Constant.curplay = Constant.nowPlays;
				}
				if (pl.curtime >= pl.totaltime) {
					pl.curtime = 0;
					Constant.curplay++;
					if (Constant.curplay >= Constant.playList.size()) {
						Constant.curplay = 0;
						Constant.playtime++;
						if (Constant.dpIsStart) {
							Constant.dpCount++;
							if (Constant.dpPlayCount >= Constant.dpCount) {
								Constant.dpTimeCount = true;
								Constant.dpIsStart = false;
								Constant.dpCount = 0;
								Constant.dpIsStop = true;
								init();
							}
						}
					}
					againPlay(Constant.curplay);
					return;
				} else {
					pl.curtime++;

				}
			} else {
				if (Constant.playList.size() > 0) {
					PlayItem pl = Constant.playList.get(0);
					if (pl.curtime >= pl.totaltime) {
						pl.curtime = 0;
						Constant.playtime++;
						if (Constant.dpIsStart) {
							Constant.dpCount++;
							if (Constant.dpPlayCount <= Constant.dpCount) {
								Constant.dpTimeCount = true;
								Constant.dpIsStart = false;
								Constant.dpCount = 0;
								Constant.dpIsStop = true;
								init();
							}
						}
					} else {
						pl.curtime++;
					}
				}
			}
		}

		if (Constant.avlbian) {
			Constant.avlbian = false;
			agavin();
			return;
		}
		if ((ims != null) && (!Constant.chu) && (!Constant.itemgo)) {
			ims.fre();
		}
		if (Constant.imglist.size() > 0) {
			for (int i = 0; i < Constant.imglist.size(); i++) {
				((SeeImageView) Constant.imglist.get(i)).tonext();
			}
		}
		if (Constant.isPop) {
			Constant.popTime--;
			if (Constant.popTime <= 0) {
				Constant.isPop = false;
				Constant.popTime = 30;
				try {
					mPoupuWindow.dismiss();
					Constant.interval = true;
				} catch (Exception e) {
				}
			}
		}

	}

	protected void onDestroy2() {
		stopPlay();
		if (Constant.timer != null) {
			Constant.timer.cancel();
			Constant.timer = null;
		}
		if (Constant.task != null) {
			Constant.task.cancel();
			Constant.task = null;
		}
		if (Constant.handler != null) {
			Constant.handler = null;
		}
		if (ms != null) {
			ms.StopServer();
			ms = null;
		}
		Constant.sc.closeSocket();

	}

	public void clearWebCache() {
		wv.clearCache(true);
		wv.clearFormData();
	}

	@SuppressLint("@JavascriptInterface")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
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
							initConfigFile();
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
				// Thread.sleep(1000);
				// Constant.sdcardDir = "/mnt/sata";
				// initConfigFile();
				// File file = new File(Constant.config);
				// if (!file.exists()) {
				// Constant.sdcardDir = "/mnt/extsd";
				initConfigFile();
				File file = new File(Constant.config);
				if (!file.exists()) {
					Constant.sdcardDir = Environment
							.getExternalStorageDirectory().getAbsolutePath();
				}
				// }
			} catch (Exception e) {
			}

		}
		Constant.tempDir = Constant.sdcardDir + "/temp/"; // 临时文件目录
		Constant.fileDir = Constant.sdcardDir + "/files/"; // 播放文件目录
		Constant.TQDIR = Constant.sdcardDir + "/tq"; // 播放文件目录
		Constant.HLDIR = Constant.sdcardDir + "/hl";
		Constant.updateDir = Constant.sdcardDir + "/update/"; // 更新文件目录
		Constant.offDir = Constant.sdcardDir + "/off/"; // 断网播放文件目录
		Constant.novideoDir = Constant.sdcardDir + "/"; // 无文件播放目录
		Constant.tDir = Constant.sdcardDir + "/temp"; // 临时目录
		Constant.fDir = Constant.sdcardDir + "/files"; // 文件目录
		Constant.uDir = Constant.sdcardDir + "/update"; // 更新目录
		Constant.cDir = Constant.sdcardDir + "/camera"; // 更新目录
		Constant.sdcardTemp = Constant.sdcardDir + "/temp/"; // 临时文件位置
		Constant.config = Constant.sdcardDir + "/config.ini"; // 播放设置等配置文件
		Constant.config2 = Constant.sdcardDir + "/config2.ini"; // 服务器IP等配置文件
		Constant.config3 = Constant.sdcardDir + "/config3.ini"; // 断电续播配置文件
		Constant.config4 = Constant.offDir + "config.ini"; // 断网播放配置文件
		Constant.advance = Constant.sdcardDir + "/advance.ini";

		while (Constant.isInstall) {
			installApk();
			Constant.installNum++;
			if ((checkApkExist("com.vshow.textfont")
					&& checkApkExist("com.vshow.scrollfont") && checkApkExist("com.example.vsplayerservice"))) {
				Toast.makeText(Vsplayer.this, this.getString(R.string.log29), 1)
						.show();
				Constant.isInstall = false;
			}
			if (Constant.installNum >= 5) {
				Toast.makeText(Vsplayer.this, this.getString(R.string.log30), 1)
						.show();
				break;
			}
		}

		Constant.lefttotal = 0;
		Constant.change = 0;
		Constant.downing = false;
		Constant.curmodel = "";
		Constant.totalfilelen = 0;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.main);
		initShow();
		super.onCreate(savedInstanceState);

		abslayout = (AbsoluteLayout) findViewById(R.id.absoluteLayout);

		wv = new WebView(this);
		wv.getSettings().setDefaultTextEncodingName("utf-8");
		wv.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		wv.setOnKeyListener(this);
		wv.setOnTouchListener(this);
		wv.getSettings().setJavaScriptEnabled(true);
		wv.getSettings().setAllowFileAccess(true);
		wv.getSettings().setPluginsEnabled(true);
		wv.getSettings().setPluginState(PluginState.ON);
		wv.getSettings().setBuiltInZoomControls(false);
		wv.getSettings().setSupportZoom(true);
		
		wv.getSettings()
				.setUserAgentString(
						"Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_4; zh-tw) AppleWebKit/533.16 (KHTML, like Gecko) Version/5.0 Safari/533.16");

		wv.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Constant.curmodel = "";
				Constant.tqagain = 0;
				Constant.tqkey = "";
				Constant.tqdo = false;

				Constant.hltime = 0;
				Constant.hlkey = "";
				Constant.hldo = false;

				url = url.trim();
				if (url.length() > 5) {
					int tef = url.lastIndexOf('/');
					String md;
					if (tef != -1) {
						md = url.substring(tef + 1, url.length());
					} else {
						md = url;
					}
					File tdsdf = null;
					tdsdf = new File(Constant.fileDir + md + File.separator
							+ "vsjm.txt");
					String tv = "";
					String temread = "";
					String[] msts;
					if (tdsdf.exists()) {
						try {
							BufferedReader fad = new BufferedReader(
									new FileReader(tdsdf));
							temread = fad.readLine().trim();
							fad.close();
						} catch (Exception e) {

						}
						msts = temread.split("\\*");
						if (msts.length == 7) {
							tv = msts[2].trim();
							if (tv.length() > 0) {
								try {
									Constant.vx = Integer.parseInt(msts[3]
											.trim());
									Constant.vy = Integer.parseInt(msts[4]
											.trim());
									Constant.vwidth = Integer.parseInt(msts[5]
											.trim());
									Constant.vheight = Integer.parseInt(msts[6]
											.trim());
									if (Constant.vwidth == 0
											|| Constant.vheight == 0) {
										Constant.vwidth = 1;
										Constant.vheight = 1;
									}
								} catch (Exception e) {

								}
							}
						}
					}

					tdsdf = new File(Constant.fileDir + md + File.separator
							+ "tq.txt");
					if (tdsdf.exists()) {
						try {
							BufferedReader fad = new BufferedReader(
									new FileReader(tdsdf));
							temread = fad.readLine().trim();
							fad.close();
						} catch (Exception e) {

						}
						msts = temread.split("\\*");
						if (msts.length == 10) {
							Constant.tqkey = msts[0].trim();
							Constant.tqsize = msts[2].trim();
							Constant.tqcolor = msts[3].trim();
							Constant.tqimgsize = msts[8].trim();
							try {
								Constant.tqurl = Integer.parseInt(msts[9]
										.trim());
							} catch (Exception e) {

							}
							try {
								Constant.tqagain = Integer.parseInt(msts[1]
										.trim());
							} catch (Exception e) {

							}
							try {
								Constant.tqx = Integer.parseInt(msts[4].trim());
							} catch (Exception e) {

							}
							try {
								Constant.tqy = Integer.parseInt(msts[5].trim());
							} catch (Exception e) {

							}
							try {
								Constant.tqwidth = Integer.parseInt(msts[6]
										.trim());
							} catch (Exception e) {

							}
							try {
								Constant.tqheight = Integer.parseInt(msts[7]
										.trim());
							} catch (Exception e) {

							}
							if (Constant.tqwidth == 0 || Constant.tqheight == 0) {
								Constant.tqwidth = 1;
								Constant.tqheight = 1;
							}
							Constant.tqdo = true;
						}
					}

					tdsdf = new File(Constant.fileDir + md + File.separator
							+ "hl.txt");
					if (tdsdf.exists()) {
						try {
							BufferedReader fad = new BufferedReader(
									new FileReader(tdsdf));
							temread = fad.readLine().trim();
							fad.close();
						} catch (Exception e) {

						}
						msts = temread.split("\\*");
						if (msts.length == 6) {
							Constant.hlkey = msts[0].trim();
							try {
								Constant.hltime = Integer.parseInt(msts[9]
										.trim());
							} catch (Exception e) {

							}
							try {
								Constant.hlx = Integer.parseInt(msts[4].trim());
							} catch (Exception e) {

							}
							try {
								Constant.hly = Integer.parseInt(msts[5].trim());
							} catch (Exception e) {

							}
							try {
								Constant.hlwidth = Integer.parseInt(msts[6]
										.trim());
							} catch (Exception e) {

							}
							try {
								Constant.hlheight = Integer.parseInt(msts[7]
										.trim());
							} catch (Exception e) {

							}
							if (Constant.hlwidth == 0 || Constant.hlheight == 0) {
								Constant.hlwidth = 1;
								Constant.hlheight = 1;
							}
							Constant.hldo = true;
						}
					}

					tdsdf = new File(Constant.fileDir + md + File.separator
							+ "wtime.txt");
					if (tdsdf.exists()) {
						try {
							BufferedReader fad = new BufferedReader(
									new FileReader(tdsdf));
							temread = fad.readLine().trim();
							fad.close();
						} catch (Exception e) {

						}
						msts = temread.split("\\*");
						if (msts.length == 5) {
							Constant.tkey = msts[0].trim();

							try {
								Constant.tx = Integer.parseInt(msts[1].trim());
							} catch (Exception e) {

							}
							try {
								Constant.ty = Integer.parseInt(msts[2].trim());
							} catch (Exception e) {

							}
							try {
								Constant.twidth = Integer.parseInt(msts[3]
										.trim());
							} catch (Exception e) {

							}
							try {
								Constant.theight = Integer.parseInt(msts[4]
										.trim());
							} catch (Exception e) {

							}
							if (Constant.twidth == 0 || Constant.theight == 0) {
								Constant.twidth = 1;
								Constant.theight = 1;
							}
							Constant.tdo = true;
						}
					}

					String imgs = "";
					String tfil;
					tfil = Constant.fileDir + md + "/" + "img.txt";
					tdsdf = new File(tfil);
					if (tdsdf.exists()) {
						try {
							BufferedReader fad = new BufferedReader(
									new FileReader(tfil));
							imgs = fad.readLine().trim();
							fad.close();
						} catch (Exception e) {

						}
					}

					String texts = "";
					tfil = Constant.fileDir + md + "/" + "text.txt";
					tdsdf = new File(tfil);
					if (tdsdf.exists()) {
						try {
							BufferedReader fad = new BufferedReader(
									new FileReader(tfil));
							texts = fad.readLine().trim();
							fad.close();
						} catch (Exception e) {

						}
					}

					String times = "";
					tfil = Constant.fileDir + md + "/" + "time.txt";
					tdsdf = new File(tfil);
					if (tdsdf.exists()) {
						try {
							BufferedReader fad = new BufferedReader(
									new FileReader(tfil));
							times = fad.readLine().trim();
							fad.close();
						} catch (Exception e) {

						}
					}

					String avs = "";
					if (Constant.offshow) {
						tfil = Constant.offDir + md + "/" + "av.txt";
					} else {
						tfil = Constant.fileDir + md + "/" + "av.txt";
					}
					tdsdf = new File(tfil);
					if (tdsdf.exists()) {
						try {
							BufferedReader fad = new BufferedReader(
									new FileReader(tfil));
							avs = fad.readLine().trim();
							fad.close();
						} catch (Exception e) {

						}
					}

					String scs = "";
					if (Constant.offshow) {
						tfil = Constant.offDir + md + "/" + "camera.txt";
					} else {
						tfil = Constant.fileDir + md + "/" + "camera.txt";
					}
					tdsdf = new File(tfil);
					if (tdsdf.exists()) {
						try {
							BufferedReader fad = new BufferedReader(
									new FileReader(tfil));
							scs = fad.readLine().trim();
							fad.close();
						} catch (Exception e) {

						}
					}
					String exc = "";
					if (Constant.offshow) {
						tfil = Constant.offDir + md + "/" + "table.txt";
					} else {
						tfil = Constant.fileDir + md + "/" + "table.txt";
					}
					tdsdf = new File(tfil);
					if (tdsdf.exists()) {
						try {
							BufferedReader fad = new BufferedReader(
									new FileReader(tfil));
							exc = fad.readLine().trim();
							fad.close();
						} catch (Exception e) {

						}
					}
					String logsc = "";
					if (Constant.offshow) {
						tfil = Constant.offDir + md + "/" + "logsc.txt";
					} else {
						tfil = Constant.fileDir + md + "/" + "logsc.txt";
					}
					tdsdf = new File(tfil);
					if (tdsdf.exists()) {
						try {
							BufferedReader fad = new BufferedReader(
									new FileReader(tfil));
							logsc = fad.readLine();
							fad.close();
						} catch (Exception e) {

						}
					}
					String key = "";
					if (Constant.offshow) {
						tfil = Constant.offDir + md + "/" + "key.txt";
					} else {
						tfil = Constant.fileDir + md + "/" + "key.txt";
					}
					tdsdf = new File(tfil);
					if (tdsdf.exists()) {
						try {
							BufferedReader fad = new BufferedReader(
									new FileReader(tfil));
							key = fad.readLine().trim();
							fad.close();
						} catch (Exception e) {

						}
					}

					Constant.mfile = md;
					Tool.saveConfig("item!" + Constant.mfile, Constant.config);
					toPlay2(Constant.fileDir + md + File.separator
							+ "index.html", tv, imgs, texts, times, avs, scs,
							exc, logsc, key);
				}
				return true;
			}
		});

		abslayout.addView(wv, new AbsoluteLayout.LayoutParams(Constant.width,
				Constant.height, 0, 0));
		
		initConfigFile();

//		if (!new File(Constant.config).exists()) {
//			try {
//				new File(Constant.config).createNewFile();
//			} catch (Exception e) {
//				// TODO: handle exception
//			}
//		}
//		if (!new File(Constant.config2).exists()) {
//			try {
//				new File(Constant.config2).createNewFile();
//			} catch (Exception e) {
//				// TODO: handle exception
//			}
//		}
//		if (!new File(Constant.config3).exists()) {
//			try {
//				new File(Constant.config3).createNewFile();
//			} catch (Exception e) {
//				// TODO: handle exception
//			}
//		}

		GetMac gm = new GetMac();
		gm.getLocalMacAddress();
		File f1 = new File(Constant.tDir);
		File f2 = new File(Constant.fDir);
		File f3 = new File(Constant.uDir);
		File f4 = new File(Constant.offDir);
		File f5 = new File(Constant.TQDIR);
		File f6 = new File(Constant.cDir);
		File f7 = new File(Constant.HLDIR);
		File f8 = new File(Constant.LogDir);
		if (!f1.isDirectory()) {
			f1.mkdir();
		}
		if (!f2.isDirectory()) {
			f2.mkdir();
		}
		if (!f3.isDirectory()) {
			f3.mkdir();
		}
		if (!f4.isDirectory()) {
			f4.mkdir();
		}
		if (!f5.isDirectory()) {
			f5.mkdir();
			String[] tqf;
			String tqstr;
			try {
				tqf = getAssets().list("tq");
				for (int j = 0; j < tqf.length; j++) {
					try {
						tqstr = tqf[j];
						InputStream is = getAssets().open("tq/" + tqstr);
						FileOutputStream fos = new FileOutputStream(
								Constant.TQDIR + File.separator + tqstr);
						byte[] buffer = new byte[1024];
						int readLen = 0;
						while ((readLen = is.read(buffer, 0, 1024)) >= 0) {
							fos.write(buffer, 0, readLen);
						}
						fos.close();
						is.close();
					} catch (Exception e) {

					}
				}
			} catch (Exception e) {
			}
		}
		if (!f6.isDirectory()) {
			f6.mkdir();
		}
		if (!f7.isDirectory()) {
			f7.mkdir();
		}
		if (!f8.isDirectory()) {
			f8.mkdir();
		}

		try {
			InputStream is = getAssets().open("no.jpg");
			FileOutputStream fos = new FileOutputStream(Constant.sdcardDir
					+ File.separator + "no.jpg");
			byte[] buffer = new byte[1024];
			int readLen = 0;
			while ((readLen = is.read(buffer, 0, 1024)) >= 0) {
				fos.write(buffer, 0, readLen);
			}
			fos.close();
			is.close();
		} catch (Exception e) {

		}
		try {
			InputStream is = getAssets().open("noen.jpg");
			FileOutputStream fos = new FileOutputStream(Constant.sdcardDir
					+ File.separator + "noen.jpg");
			byte[] buffer = new byte[1024];
			int readLen = 0;
			while ((readLen = is.read(buffer, 0, 1024)) >= 0) {
				fos.write(buffer, 0, readLen);
			}
			fos.close();
			is.close();
		} catch (Exception e) {

		}
		try {
			InputStream is = getAssets().open("no.html");
			FileOutputStream fos = new FileOutputStream(Constant.sdcardDir
					+ File.separator + "no.html");
			byte[] buffer = new byte[1024];
			int readLen = 0;
			while ((readLen = is.read(buffer, 0, 1024)) >= 0) {
				fos.write(buffer, 0, readLen);
			}
			fos.close();
			is.close();
		} catch (Exception e) {

		}
		try {
			InputStream is = getAssets().open("noen.html");
			FileOutputStream fos = new FileOutputStream(Constant.sdcardDir
					+ File.separator + "noen.html");
			byte[] buffer = new byte[1024];
			int readLen = 0;
			while ((readLen = is.read(buffer, 0, 1024)) >= 0) {
				fos.write(buffer, 0, readLen);
			}
			fos.close();
			is.close();
		} catch (Exception e) {

		}
		Constant.li.writeLog("0000  " + R.string.log31);

		String[] params2 = Tool.loadConfig(new String[] { "runr", "web",
				"light", "lighttime" }, Constant.config2);
		// int runr = 0;
		Constant.light = 0;
		Constant.lighttime = 60;
		if (params2 != null) {
			if (!params2[2].equals("")) {
				try {
					Constant.light = Integer.parseInt(params2[2].trim());
				} catch (Exception e) {
				}
			}
			if (!params2[3].equals("")) {
				try {
					Constant.lighttime = Integer.parseInt(params2[3].trim());
				} catch (Exception e) {
				}
			}
			// try {
			// runr = Integer.parseInt(params2[0].trim());
			// } catch (Exception e) {
			// // TODO: handle exception
			// }
			// if (runr > 1) {
			// try {
			// new File(Constant.config).delete();
			// } catch (Exception e) {
			//
			// }
			// try {
			// new File(Constant.config).createNewFile();
			// } catch (Exception e) {
			// // TODO: handle exception
			// }
			// Constant.curmodel = "";
			// }

			int web = 0;
			try {
				web = Integer.parseInt(params2[1].trim());
			} catch (Exception e) {

			}
			if (web == 0) {
				Constant.WEB = false;
			} else {
				Constant.WEB = true;
			}
			// runr++;
		} else {
			Constant.WEB = false;
			// runr = 0;
		}

		if (Constant.WEB) {
			while (true) {
				if (Constant.WEBGO) {
					// Tool.saveConfig("runr!" + runr, Constant.config2);
					initConfig();
					break;
				} else {
					if (isConnect()) {
						try {
							// new IsNetwork().start();
							// Thread.sleep(500);
							Constant.openurl = true;
						} catch (Exception e) {

						}
						if (Constant.openurl) {
							Constant.WEBGO = true;
							// Tool.saveConfig("runr!" + runr,
							// Constant.config2);
							initConfig();
							break;
						}
					}
					try {
						Thread.sleep(1000);
					} catch (Exception e) {

					}
				}

			}
		} else {
			// Tool.saveConfig("runr!" + runr, Constant.config2);
			initConfig();
		}

		// Tool.saveConfig("runr!0", Constant.config2);

		if (Constant.timer != null) {
			Constant.timer.cancel();
			Constant.timer = null;
		}
		if (Constant.task != null) {
			Constant.task.cancel();
			Constant.task = null;
		}
		if (Constant.handler != null) {
			Constant.handler = null;
		}
		Constant.handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					timeDo();
					break;
				case 2:
					try {
						InputStream is = getAssets().open("textfont.apk");
						FileOutputStream fos = new FileOutputStream(
								Constant.tempDir + "textfont.apk");

						byte[] buffer = new byte[1024];
						int readLen = 0;
						while ((readLen = is.read(buffer, 0, 1024)) >= 0) {
							fos.write(buffer, 0, readLen);
						}
						is = getAssets().open("scrollfont.apk");
						fos = new FileOutputStream(Constant.tempDir
								+ "scrollfont.apk");
						byte[] buffer2 = new byte[1024];
						int readLen2 = 0;
						while ((readLen2 = is.read(buffer2, 0, 1024)) >= 0) {
							fos.write(buffer2, 0, readLen2);
						}
						is = getAssets().open("vsplayerservice.apk");
						fos = new FileOutputStream(Constant.tempDir
								+ "vsplayerservice.apk");
						byte[] buffer3 = new byte[1024];
						int readLen3 = 0;
						while ((readLen3 = is.read(buffer3, 0, 1024)) >= 0) {
							fos.write(buffer3, 0, readLen3);
						}
						fos.close();
						is.close();
						Tool.install(Constant.tempDir + "textfont.apk");
						Tool.install(Constant.tempDir + "scrollfont.apk");
						Tool.install(Constant.tempDir + "vsplayerservice.apk");
						Toast.makeText(Vsplayer.this, "清理完成", 1).show();
						Constant.onkeys = false;
						initService();
					} catch (Exception e) {

					}
					break;
				}
				super.handleMessage(msg);
			}

		};

		Constant.task = new TimerTask() {
			public void run() {
				Message message = new Message();
				message.what = 1;
				Constant.handler.sendMessage(message);
			}

		};
		Constant.timer = new Timer();
		Constant.timer.schedule(Constant.task, 1000, 1000);

		if (Constant.firstrun) {
			Constant.firstrun = false;
			String path = "";
			String[] strs;
			String[] strss;
			int pos = 0;
			long atotal = 0;
			String file = "";
			String[] down = Tool.loadConfig(new String[] { "downstr" },
					Constant.config);
			if (down.length > 0) {
				try {
					Constant.downstr = down[0].trim();
					if (Constant.downstr.length() > 0) {
						pos = Constant.downstr.indexOf('+');
						path = Constant.downstr.substring(2, pos).trim();
					}
				} catch (Exception e) {
				}
				strs = path.split("\\/");

				if (strs.length == 2) {
					try {
						atotal = Long.parseLong(strs[0].trim());
					} catch (Exception e) {
					}
					try {
						file = strs[1].trim();
					} catch (Exception e) {
					}
					strss = file.split("\\|");
					long dtotal = 0;
					if (strss.length > 1) {
						for (int i = 0; i < strss.length; i++) {
							File f = new File(Constant.fileDir + strss[i]);
							dtotal += f.length();
							f = null;
						}
					} else {
						File f = new File(Constant.fileDir + file);
						dtotal = f.length();
						f = null;
					}
					if (dtotal < atotal && dtotal != 0) {
						Constant.downfinish = true;
						Constant.li.writeLog("0000 " + R.string.log32);
					}
				}
			}
		}

		UsbReceiver receiver = new UsbReceiver();

		IntentFilter filter = new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);
		filter.addAction(Intent.ACTION_MEDIA_CHECKING);
		filter.addAction(Intent.ACTION_MEDIA_EJECT);
		filter.addAction(Intent.ACTION_MEDIA_REMOVED);
		filter.addAction(Intent.ACTION_MEDIA_SHARED);
		filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		filter.addDataScheme("file");
		registerReceiver(receiver, filter);

		if (console == null) {
			console = new Console("/dev/ttyS2", 9600);
		}
		byte[] bytes = new byte[3];
		bytes[0] = (byte) 0xE0;
		bytes[1] = (byte) 0x05;
		bytes[2] = (byte) 0xE5;
		try {
			console.send(bytes, 0, 3);
		} catch (Exception e) {
		}

		bytes = new byte[3];
		bytes[0] = (byte) 0xE1;
		bytes[1] = (byte) 0x05;
		bytes[2] = (byte) 0xE6;
		try {
			Thread.sleep(1000);
			console.send(bytes, 0, 3);
		} catch (Exception e) {
		}
		initService();
		initSetOff();
//		close(this);
	}

	private void inittq() {
		if (Constant.tqdo) {
			tqweb = new WebView(this);
			tqweb.getSettings().setDefaultTextEncodingName("utf-8");
			tqweb.setOnKeyListener(this);
			tqweb.setOnTouchListener(this);
			tqweb.getSettings().setJavaScriptEnabled(true);
			tqweb.getSettings().setAllowFileAccess(true);
			tqweb.getSettings().setPluginsEnabled(true);
			tqweb.getSettings().setPluginState(PluginState.ON);
			tqweb.getSettings().setBuiltInZoomControls(false);
			tqweb.getSettings().setSupportZoom(true);
			if (Constant.isZoom) {
				tqweb.setInitialScale((int) ((Constant.scaleZoomW) * 100));
			} else {
				tqweb.setInitialScale(100);
			}
			tqweb.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
			tqweb.setBackgroundColor(0);

			tqweb.loadUrl("file://" + Constant.TQDIR + "/tq.html");
			abslayout.addView(tqweb, new AbsoluteLayout.LayoutParams(
					Constant.tqwidth, Constant.tqheight, Constant.tqx,
					Constant.tqy));
		}
	}

	private void inithl() {
		if (Constant.hldo) {
			hlweb = new WebView(this);
			hlweb.getSettings().setDefaultTextEncodingName("utf-8");
			hlweb.setOnKeyListener(this);
			hlweb.setOnTouchListener(this);
			hlweb.getSettings().setJavaScriptEnabled(true);
			hlweb.getSettings().setAllowFileAccess(true);
			hlweb.getSettings().setPluginsEnabled(true);
			hlweb.getSettings().setPluginState(PluginState.ON);
			hlweb.getSettings().setBuiltInZoomControls(false);
			hlweb.getSettings().setSupportZoom(true);
			hlweb.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
			hlweb.setBackgroundColor(0);
			if (Constant.isZoom) {
				hlweb.setInitialScale((int) ((Constant.scaleZoomW) * 100));
			} else {
				hlweb.setInitialScale(100);
			}
			hlweb.loadUrl("file://" + Constant.HLDIR + "/hl.html");
			abslayout.addView(hlweb, new AbsoluteLayout.LayoutParams(
					Constant.hlwidth, Constant.hlheight, Constant.hlx,
					Constant.hly));
		}
	}

	private void initTime() {
		if (Constant.tdo) {
			tweb = new WebView(this);
			tweb.getSettings().setDefaultTextEncodingName("utf-8");
			tweb.setOnKeyListener(this);
			tweb.setOnTouchListener(this);
			tweb.getSettings().setJavaScriptEnabled(true);
			tweb.getSettings().setAllowFileAccess(true);
			tweb.getSettings().setPluginsEnabled(true);
			tweb.getSettings().setPluginState(PluginState.ON);
			tweb.getSettings().setBuiltInZoomControls(true);
			tweb.getSettings().setSupportZoom(true);
			tweb.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
			tweb.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
			tweb.setBackgroundColor(0);
			if (Constant.isZoom) {
				tweb.setInitialScale((int) ((Constant.scaleZoomW) * 100));
			} else {
				tweb.setInitialScale(100);
			}
			tweb.loadUrl(Constant.tkey);
			abslayout.addView(tweb,
					new AbsoluteLayout.LayoutParams(Constant.twidth,
							Constant.theight, Constant.tx, Constant.ty));
		}
	}

	private void toPlay2(String urlname, String str, String imgs, String texts,
			String times, String avs, String scs, String exc, String logsc,
			String key) {
		if (vp != null) {
			vp.rel();
			abslayout.removeView(vp);
			vp = null;
		}
		if (cvbs != null) {
			cvbs.closeTV();
			abslayout.removeView(cvbs);
			cvbs = null;
		}
		if (mp != null) {
			mp.rel();
			mp = null;
		}
		if (ims != null) {
			ims.recy();
			abslayout.removeView(ims);
			ims = null;
		}
		if (tqweb != null) {
			tqweb.clearView();
			abslayout.removeView(tqweb);
			tqweb = null;
		}
		if (timeweb != null) {
			timeweb.clearView();
			abslayout.removeView(timeweb);
			timeweb = null;
		}
		if (showCamera != null) {
			showCamera.stop();
			abslayout.removeView(showCamera);
			showCamera = null;
		}
		if (hlweb != null) {
			hlweb.clearView();
			abslayout.removeView(hlweb);
			hlweb = null;
		}

		if (tweb != null) {
			tweb.clearView();
			abslayout.removeView(tweb);
			tweb = null;
		}
		if (excelWeb != null) {
			excelWeb.clearView();
			abslayout.removeView(excelWeb);
			excelWeb = null;
		}

		String urls = "file://" + urlname;
		wv.loadUrl(urls);

		Constant.avl.clear();
		if (str.length() > 0) {
			if (Constant.vwidth > 0) {
				String[] stss = str.split("\\#");
				String sts;
				for (int i = 0; i < stss.length; i++) {
					sts = stss[i].trim();
					if (sts.length() > 0) {
						Constant.avl.add(sts);
					}
				}
				initVideo(0, 0);
			}
		}
		initAV(avs);

		initShowCamera(scs);

		initimage(imgs);

		inittq();

		inithl();

		initText(texts);

		initTime(times, Constant.mfile);

		initExcel(exc, Constant.mfile);

		if (Constant.fontStart) {
			try {
				ComponentName componetName = new ComponentName(
						"com.vshow.scrollfont",
						"com.vshow.scrollfont.ScrollFontActivity");
				Intent intent = new Intent();
				intent.setComponent(componetName);
				startActivity(intent);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

		String pn = key.trim();
		String pns[] = pn.split("\\@");
		String playName = "";
		String sceneName = "";
		String videoName = "";
		String imageName = "";
		try {
			playName = pns[0].trim();
		} catch (Exception e) {

		}
		String ls[] = logsc.split("\\|");
		if (ls.length == 3) {
			sceneName = ls[0].trim();
			videoName = ls[2].trim();
			imageName = ls[1].trim();
		}
		Constant.li.writeLog("0000 :" + R.string.log33 + " " + sceneName + "  "
				+ R.string.log34 + ":" + playName + "   " + R.string.log35
				+ ":" + videoName + "   " + R.string.log36 + ":" + imageName);

	}

	@Override
	public void onBackPressed() {
		Menu.exitApp(this);
	}
	
	public boolean onKey(View v, int keyCode, KeyEvent event) {
//	       if (keyCode == 82 && event.getAction() == KeyEvent.ACTION_DOWN ) {
//	    	   com.zckj.setting.Menu.show(this);
//	    	   return true;
//	        }
//
//	        return false;
		
		
		if (keyCode == KeyEvent.KEYCODE_1
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			/*LinearLayout ll = new LinearLayout(this);
			View view = ll;
			et = new EditText(this);
			ll.addView(et, new AbsoluteLayout.LayoutParams(330,
					LayoutParams.WRAP_CONTENT, 0, 0));

			final Spinner sp = new Spinner(this);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item);
			adapter.add(this.getString(R.string.sd));
			adapter.add(this.getString(R.string.chip));
			// adapter.add(this.getString(R.string.harddisk));
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			sp.setAdapter(adapter);

			int pppa = 0;
			String[] pps = Tool.loadConfig(new String[] { "savedir" },
					Constant.saveDIR);//savedir.ini
			if (pps != null) {
				if (pps.length == 1) {
					try {
						pppa = Integer.parseInt(pps[0].trim());
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			}
			sp.setSelection(pppa, true);
			ll.addView(sp);

			// Tool.disable_watchdog();
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			String[] pgcof = Tool.loadConfig(new String[] { "srvip" },
					Constant.config2);//srvip.ini
			String temip = "";
			if (pgcof != null) {
				temip = pgcof[0].trim();
			}
			if (temip.equals("")) {
				temip = Constant.SRVIP;
			}
			et.setText(temip);
			WifiManager wifi = (WifiManager) getSystemService(WIFI_SERVICE);
			WifiInfo info = wifi.getConnectionInfo();
			String wifiStr = info.getMacAddress();
			
			builder.setTitle(
					this.getString(R.string.code) + " :[" + Constant.mac
							+ "]  " + Constant.version + "  Wi-Fi Mac:["
							+ wifiStr + "] " + this.getString(R.string.Srvip))
					.setView(view);

			builder.setPositiveButton(R.string.confirm,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog1, int btn) {
						
							Tool.saveConfig("srvip!"
									+ et.getText().toString().trim(),
									Constant.config2);
									
							int pos = sp.getSelectedItemPosition();
							Tool.saveConfig("savedir!" + pos, Constant.saveDIR);
							Constant.li.writeLog("0000 " + R.string.log37
									+ et.getText().toString().trim());
							init();
							
							return;
						}
					});
			builder.setNegativeButton(R.string.cancel,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog1, int btn) {

						}
					});
			builder.setNeutralButton(R.string.checknetwork,
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							StrictMode
									.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
											.detectDiskReads()
											.detectDiskWrites().detectNetwork()
											.penaltyLog().build());
							StrictMode
									.setVmPolicy(new StrictMode.VmPolicy.Builder()
											.detectLeakedSqlLiteObjects()
											.penaltyLog().penaltyDeath()
											.build());
							if (Constant.sc.connect(et.getText().toString()
									.trim())) {
								Toast.makeText(
										Vsplayer.this,
										getResources().getString(
												R.string.connect),
										Toast.LENGTH_LONG).show();
							} else {
								Toast.makeText(
										Vsplayer.this,
										getResources().getString(
												R.string.disconnect),
										Toast.LENGTH_LONG).show();
							}
						}
					});
			builder.create().show();*/
		} else if (keyCode == KeyEvent.KEYCODE_3
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			// Tool.disable_watchdog();
			/*startActivity(new Intent(
					android.provider.Settings.ACTION_DISPLAY_SETTINGS));*/
		} else if (keyCode == KeyEvent.KEYCODE_2
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			// Tool.disable_watchdog();
			/*startActivity(new Intent(
					android.provider.Settings.ACTION_WIRELESS_SETTINGS));*/

		} else if (keyCode == KeyEvent.KEYCODE_4
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			/*Constant.getOpenTime1 = "00";
			Constant.getOpenTime1_2 = "00";
			Constant.getColseTime1 = "00";
			Constant.getColseTime1_2 = "00";
			Constant.getOpenTime2 = "00";
			Constant.getOpenTime2_2 = "00";
			Constant.getColseTime2 = "00";
			Constant.getColseTime2_2 = "00";
			Constant.getOpenTime3 = "00";
			Constant.getOpenTime3_2 = "00";
			Constant.getColseTime3 = "00";
			Constant.getColseTime3_2 = "00";
			String[] shutTime = Constant.shut.split("\\^");
			String[] shutTimes1 = shutTime[0].split("\\_");
			if (shutTimes1.length == 3) {
				Constant.oneTime = true;
				try {
					Constant.day = Integer.parseInt(shutTimes1[0].trim());
				} catch (Exception e) {
				}
				if (Constant.day == 8 || Constant.day == Constant.curday) {
					try {
						String[] hm = shutTimes1[1].trim().split(":");
						if (hm.length == 2) {
							Constant.getOpenTime1 = hm[0].trim();
							Constant.getOpenTime1_2 = hm[1].trim();

						}
						Constant.setOpenTime1 = shutTimes1[1].trim();
					} catch (Exception e) {
					}
					try {
						String[] hm = shutTimes1[2].trim().split(":");
						if (hm.length == 2) {
							Constant.getColseTime1 = hm[0].trim();
							Constant.getColseTime1_2 = hm[1].trim();

						}
						Constant.setColseTime1 = shutTimes1[1].trim();
					} catch (Exception e) {
					}
				}
			}
			if (shutTime.length > 1) {
				String[] shutTimes2 = shutTime[1].split("\\_");
				if (shutTimes2.length == 3) {
					Constant.twoTime = true;
					try {
						Constant.day = Integer.parseInt(shutTimes2[0].trim());
					} catch (Exception e) {
					}
					if (Constant.day == 8 || Constant.day == Constant.curday) {
						try {
							String[] hm = shutTimes2[1].trim().split(":");
							if (hm.length == 2) {
								Constant.getOpenTime2 = hm[0].trim();
								Constant.getOpenTime2_2 = hm[1].trim();
							}
							Constant.setOpenTime2 = shutTimes2[1].trim();
						} catch (Exception e) {
						}
						try {
							String[] hm = shutTimes2[2].trim().split(":");
							if (hm.length == 2) {
								Constant.getColseTime2 = hm[0].trim();
								Constant.getColseTime2_2 = hm[1].trim();
							}
							Constant.setColseTime2 = shutTimes2[2].trim();
						} catch (Exception e) {
						}
					}
				}
			}
			if (shutTime.length > 2) {
				String[] shutTimes3 = shutTime[2].split("\\_");
				if (shutTimes3.length == 3) {
					Constant.threeTime = true;
					try {
						Constant.day = Integer.parseInt(shutTimes3[0].trim());
					} catch (Exception e) {
					}
					if (Constant.day == 8 || Constant.day == Constant.curday) {
						try {
							String[] hm = shutTimes3[1].trim().split(":");
							if (hm.length == 2) {
								Constant.getOpenTime3 = hm[0].trim();
								Constant.getOpenTime3_2 = hm[1].trim();
							}
							Constant.setOpenTime3 = shutTimes3[1].trim();
						} catch (Exception e) {
						}
						try {
							String[] hm = shutTimes3[2].trim().split(":");
							if (hm.length == 2) {
								Constant.getColseTime3 = hm[0].trim();
								Constant.getColseTime3_2 = hm[1].trim();
							}
							Constant.setColseTime3 = shutTimes3[2].trim();
						} catch (Exception e) {
						}
					}
				}
			}
			AbsoluteLayout off = new AbsoluteLayout(this);
			tvOpen1 = new EditText(this);
			tvOpen1.setSingleLine();
			tvOpen1.setInputType(EditorInfo.TYPE_CLASS_PHONE);
			tvOpen1.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
					2) });

			tvopen1_2 = new EditText(this);
			tvopen1_2.setSingleLine();
			tvopen1_2.setInputType(EditorInfo.TYPE_CLASS_PHONE);
			tvopen1_2
					.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
							2) });

			tvColse1 = new EditText(this);
			tvColse1.setSingleLine();
			tvColse1.setInputType(EditorInfo.TYPE_CLASS_PHONE);
			tvColse1.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
					2) });

			tvColse1_2 = new EditText(this);
			tvColse1_2.setSingleLine();
			tvColse1_2.setInputType(EditorInfo.TYPE_CLASS_PHONE);
			tvColse1_2
					.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
							2) });

			TextView tv1 = new TextView(this);
			TextView tv2 = new TextView(this);
			TextView tv3 = new TextView(this);
			TextView tv4 = new TextView(this);
			TextView tv5 = new TextView(this);
			TextView tv6 = new TextView(this);
			tv1.setText(R.string.firstopentime);
			tv1.setTextSize(20);
			tv2.setText(R.string.firstclosetime);
			tv2.setTextSize(20);
			tv3.setText("H:");
			tv3.setTextSize(20);
			tv4.setText("M:");
			tv4.setTextSize(20);
			tv5.setText("H:");
			tv5.setTextSize(20);
			tv6.setText("M:");
			tv6.setTextSize(20);
			tvOpen1.setText(Constant.getOpenTime1);
			tvopen1_2.setText(Constant.getOpenTime1_2);
			tvColse1.setText(Constant.getColseTime1);
			tvColse1_2.setText(Constant.getColseTime1_2);
			off.addView(tv1, new AbsoluteLayout.LayoutParams(200, 40, 0, 6));
			off.addView(tv3, new AbsoluteLayout.LayoutParams(200, 40, 200, 6));
			off.addView(tvOpen1,
					new AbsoluteLayout.LayoutParams(60, 40, 220, 0));
			off.addView(tv4, new AbsoluteLayout.LayoutParams(200, 40, 280, 6));
			off.addView(tvopen1_2, new AbsoluteLayout.LayoutParams(60, 40, 300,
					0));

			off.addView(tv2, new AbsoluteLayout.LayoutParams(200, 40, 0, 46));
			off.addView(tv5, new AbsoluteLayout.LayoutParams(200, 40, 200, 46));
			off.addView(tvColse1, new AbsoluteLayout.LayoutParams(60, 40, 220,
					40));
			off.addView(tv6, new AbsoluteLayout.LayoutParams(200, 40, 280, 46));
			off.addView(tvColse1_2, new AbsoluteLayout.LayoutParams(60, 40,
					300, 40));

			tvOpen2 = new EditText(this);
			tvOpen2.setSingleLine();
			tvOpen2.setInputType(EditorInfo.TYPE_CLASS_PHONE);
			tvOpen2.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
					2) });

			tvOpen2_2 = new EditText(this);
			tvOpen2_2.setSingleLine();
			tvOpen2_2.setInputType(EditorInfo.TYPE_CLASS_PHONE);
			tvOpen2_2
					.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
							2) });

			tvColse2 = new EditText(this);
			tvColse2.setSingleLine();
			tvColse2.setInputType(EditorInfo.TYPE_CLASS_PHONE);
			tvColse2.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
					2) });

			tvColse2_2 = new EditText(this);
			tvColse2_2.setSingleLine();
			tvColse2_2.setInputType(EditorInfo.TYPE_CLASS_PHONE);
			tvColse2_2
					.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
							2) });

			TextView tv7 = new TextView(this);
			TextView tv8 = new TextView(this);
			TextView tv9 = new TextView(this);
			TextView tv10 = new TextView(this);
			TextView tv11 = new TextView(this);
			TextView tv12 = new TextView(this);
			tv7.setText(R.string.secondopentime);
			tv7.setTextSize(20);
			tv8.setText(R.string.secondclosetime);
			tv8.setTextSize(20);
			tv9.setText("H:");
			tv9.setTextSize(20);
			tv10.setText("M:");
			tv10.setTextSize(20);
			tv11.setText("H:");
			tv11.setTextSize(20);
			tv12.setText("M:");
			tv12.setTextSize(20);
			tvOpen2.setText(Constant.getOpenTime2);
			tvOpen2_2.setText(Constant.getOpenTime2_2);
			tvColse2.setText(Constant.getColseTime2);
			tvColse2_2.setText(Constant.getColseTime2_2);
			off.addView(tv7, new AbsoluteLayout.LayoutParams(200, 40, 0, 86));
			off.addView(tv9, new AbsoluteLayout.LayoutParams(200, 40, 200, 86));
			off.addView(tvOpen2, new AbsoluteLayout.LayoutParams(60, 40, 220,
					80));
			off.addView(tv10, new AbsoluteLayout.LayoutParams(200, 40, 280, 86));
			off.addView(tvOpen2_2, new AbsoluteLayout.LayoutParams(60, 40, 300,
					80));

			off.addView(tv8, new AbsoluteLayout.LayoutParams(200, 40, 0, 126));
			off.addView(tv11,
					new AbsoluteLayout.LayoutParams(200, 40, 200, 126));
			off.addView(tvColse2, new AbsoluteLayout.LayoutParams(60, 40, 220,
					120));
			off.addView(tv12,
					new AbsoluteLayout.LayoutParams(200, 40, 280, 126));
			off.addView(tvColse2_2, new AbsoluteLayout.LayoutParams(45, 40,
					300, 120));

			tvOpen3 = new EditText(this);
			tvOpen3.setSingleLine();
			tvOpen3.setInputType(EditorInfo.TYPE_CLASS_PHONE);
			tvOpen3.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
					2) });

			tvOpen3_2 = new EditText(this);
			tvOpen3_2.setSingleLine();
			tvOpen3_2.setInputType(EditorInfo.TYPE_CLASS_PHONE);
			tvOpen3_2
					.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
							2) });

			tvColse3 = new EditText(this);
			tvColse3.setSingleLine();
			tvColse3.setInputType(EditorInfo.TYPE_CLASS_PHONE);
			tvColse3.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
					2) });

			tvColse3_2 = new EditText(this);
			tvColse3_2.setSingleLine();
			tvColse3_2.setInputType(EditorInfo.TYPE_CLASS_PHONE);
			tvColse3_2
					.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
							2) });

			TextView tv13 = new TextView(this);
			TextView tv14 = new TextView(this);
			TextView tv15 = new TextView(this);
			TextView tv16 = new TextView(this);
			TextView tv17 = new TextView(this);
			TextView tv18 = new TextView(this);
			tv13.setText(R.string.thirdopentime);
			tv13.setTextSize(20);
			tv14.setText(R.string.thirdclosetime);
			tv14.setTextSize(20);
			tv15.setText("H:");
			tv15.setTextSize(20);
			tv16.setText("M:");
			tv16.setTextSize(20);
			tv17.setText("H:");
			tv17.setTextSize(20);
			tv18.setText("M:");
			tv18.setTextSize(20);
			tvOpen3.setText(Constant.getOpenTime3);
			tvOpen3_2.setText(Constant.getOpenTime3_2);
			tvColse3.setText(Constant.getColseTime3);
			tvColse3_2.setText(Constant.getColseTime3_2);
			off.addView(tv13, new AbsoluteLayout.LayoutParams(200, 40, 0, 166));
			off.addView(tv15,
					new AbsoluteLayout.LayoutParams(200, 40, 200, 166));
			off.addView(tvOpen3, new AbsoluteLayout.LayoutParams(60, 40, 220,
					160));
			off.addView(tv16,
					new AbsoluteLayout.LayoutParams(200, 40, 280, 166));
			off.addView(tvOpen3_2, new AbsoluteLayout.LayoutParams(60, 40, 300,
					160));

			off.addView(tv14, new AbsoluteLayout.LayoutParams(200, 40, 0, 206));
			off.addView(tv17,
					new AbsoluteLayout.LayoutParams(200, 40, 200, 206));
			off.addView(tvColse3, new AbsoluteLayout.LayoutParams(60, 40, 220,
					200));
			off.addView(tv18,
					new AbsoluteLayout.LayoutParams(200, 40, 280, 206));
			off.addView(tvColse3_2, new AbsoluteLayout.LayoutParams(60, 40,
					300, 200));

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.setopenorcolse);
			builder.setView(off);
			builder.setPositiveButton(R.string.confirm,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog1, int btn) {
							Constant.setOpenTime1 = tvOpen1.getText()
									.toString()
									+ ":"
									+ tvopen1_2.getText().toString();
							Constant.setOpenTime2 = tvOpen2.getText()
									.toString()
									+ ":"
									+ tvOpen2_2.getText().toString();
							Constant.setOpenTime3 = tvOpen3.getText()
									.toString()
									+ ":"
									+ tvOpen3_2.getText().toString();
							Constant.setColseTime1 = tvColse1.getText()
									.toString()
									+ ":"
									+ tvColse1_2.getText().toString();
							Constant.setColseTime2 = tvColse2.getText()
									.toString()
									+ ":"
									+ tvColse2_2.getText().toString();
							Constant.setColseTime3 = tvColse3.getText()
									.toString()
									+ ":"
									+ tvColse3_2.getText().toString();
							if (count(Constant.setOpenTime1) == 4
									|| count(Constant.setColseTime1) == 4) {
								Constant.oneTime = false;
							} else {
								Constant.oneTime = true;
							}
							if (count(Constant.setOpenTime2) == 4
									|| count(Constant.setColseTime2) == 4) {
								Constant.twoTime = false;
							} else {
								Constant.twoTime = true;
							}
							if (count(Constant.setOpenTime3) == 4
									|| count(Constant.setColseTime3) == 4) {
								Constant.threeTime = false;
							} else {
								Constant.threeTime = true;
							}
							String info = "";
							if (Constant.oneTime && !Constant.twoTime
									&& !Constant.threeTime) {
								info = 8 + "_" + Constant.setOpenTime1 + "_"
										+ Constant.setColseTime1;
							} else if (Constant.oneTime && Constant.twoTime
									&& !Constant.threeTime) {
								info = 8 + "_" + Constant.setOpenTime1 + "_"
										+ Constant.setColseTime1 + "^" + 8
										+ "_" + Constant.setOpenTime2 + "_"
										+ Constant.setColseTime2;
							} else if (Constant.oneTime && Constant.twoTime
									&& Constant.threeTime) {
								info = 8 + "_" + Constant.setOpenTime1 + "_"
										+ Constant.setColseTime1 + "^" + 8
										+ "_" + Constant.setOpenTime2 + "_"
										+ Constant.setColseTime2 + "^" + 8
										+ "_" + Constant.setOpenTime3 + "_"
										+ Constant.setColseTime3;
							} else if (!Constant.oneTime && !Constant.twoTime
									&& !Constant.threeTime) {
								Tool.saveConfig("shut!" + " ", Constant.config);//shut
							}
							if (!info.equals("")) {
								Tool.saveConfig("shut!" + info, Constant.config);
								Constant.li.writeLog("0000 " + R.string.log38
										+ info);
							}
							Constant.oneTime = false;
							Constant.twoTime = false;
							Constant.threeTime = false;
							init();
							initSetOff();
						}
					});
			builder.setNegativeButton(R.string.cancel,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog1, int btn) {

						}
					});
			builder.setNeutralButton(R.string.clearall,
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							Tool.saveConfig("shut!" + " ", Constant.config);
							Constant.getOpenTime1 = "00";
							Constant.getOpenTime1_2 = "00";
							Constant.getColseTime1 = "00";
							Constant.getColseTime1_2 = "00";
							Constant.getOpenTime2 = "00";
							Constant.getOpenTime2_2 = "00";
							Constant.getColseTime2 = "00";
							Constant.getColseTime2_2 = "00";
							Constant.getOpenTime3 = "00";
							Constant.getOpenTime3_2 = "00";
							Constant.getColseTime3 = "00";
							Constant.getColseTime3_2 = "00";
							Constant.oneTime = false;
							Constant.twoTime = false;
							Constant.threeTime = false;
							init();
							initSetOff();
							Constant.li.writeLog("0000 " + R.string.log39);
						}
					});

			builder.create().show();*/

		} else if (keyCode == KeyEvent.KEYCODE_5
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			// Constant.change = 27;
			AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			am.setStreamMute(AudioManager.STREAM_MUSIC, true);
			
			// Gpio.writeGpio('h', 12, 1);
		} else if (keyCode == KeyEvent.KEYCODE_6
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			// Constant.change = 28;
			AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			am.setStreamMute(AudioManager.STREAM_MUSIC, false);
			// Gpio.writeGpio('h', 12, 0);
		} else if (keyCode == KeyEvent.KEYCODE_9
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if (!Constant.onkeys) {
				Constant.onkeys = true;
				AlertDialog.Builder builder = new Builder(Vsplayer.this);
				builder.setMessage("是否清理终端程序与数据");
				builder.setTitle("提示");
				builder.setPositiveButton("确认",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								Toast.makeText(Vsplayer.this, "正在清理数据", 1)
										.show();
								Constant.change = 41;
							}
						});
				builder.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {

							}
						});
				builder.create().show();
			}
		} else if (keyCode == 93 && event.getAction() == KeyEvent.ACTION_DOWN) {//Page Down
			try {
				Gpio.writeGpio('h', 12, 0);
				Thread.sleep(200);
				Gpio.writeGpio('h', 12, 1);
			} catch (Exception e) {
				// TODO: handle exception
			}

		} else if (keyCode == 168 && event.getAction() == KeyEvent.ACTION_DOWN) {//KEYCODE_ZOOM_IN
			Constant.chu = false;
			GetAllItem gal = new GetAllItem();
			List list = gal.get();
			clearWebCache();

			Item it;
			try {
				OutputStreamWriter fw = new OutputStreamWriter(
						new FileOutputStream(Constant.tempDir + "item.html"),
						"utf-8");
				fw.write("<meta http-equiv='content-type' content='text/html;charset=utf-8'><body>");
				for (int i = 0; i < list.size(); i++) {
					it = (Item) list.get(i);
					fw.append("<a href=\"");
					fw.append(it.link);
					fw.append("\">");
					fw.append(it.name);
					fw.append("</a> &nbsp;");
					if ((i % 15 == 0) && (i != 0)) {
						fw.append("<br><br>");
					}
				}
				fw.append("</body>");
				fw.close();
			} catch (Exception e) {
				// TODO: handle exception
			}

			if (vp != null) {
				vp.rel();
				abslayout.removeView(vp);
				vp = null;
			}
			if (cvbs != null) {
				cvbs.closeTV();
				abslayout.removeView(cvbs);
				cvbs = null;
			}
			if (mp != null) {
				mp.rel();
				mp = null;
			}
			if (ims != null) {
				ims.recy();
				abslayout.removeView(ims);
				ims = null;
			}

			if (tqweb != null) {
				tqweb.clearView();
				abslayout.removeView(tqweb);
				tqweb = null;
			}

			if (timeweb != null) {
				timeweb.clearView();
				abslayout.removeView(timeweb);
				timeweb = null;
			}
			if (showCamera != null) {
				showCamera.stop();
				abslayout.removeView(showCamera);
				showCamera = null;
			}

			if (hlweb != null) {
				hlweb.clearView();
				abslayout.removeView(hlweb);
				hlweb = null;
			}

			if (tweb != null) {
				tweb.clearView();
				abslayout.removeView(tweb);
				tweb = null;
			}

			if (excelWeb != null) {
				excelWeb.clearView();
				abslayout.removeView(excelWeb);
				excelWeb = null;
			}

			SeeImageView mmi;
			for (int i = 0; i < Constant.imglist.size(); i++) {
				mmi = (SeeImageView) Constant.imglist.get(i);
				mmi.recy();
				abslayout.removeView(mmi);
				mmi = null;
			}
			Constant.imglist.clear();

			stopText();

			Constant.itemgo = true;
			wv.loadUrl("file://" + Constant.tempDir + "item.html");
		} else if (keyCode == 169 && event.getAction() == KeyEvent.ACTION_DOWN) {//KEYCODE_ZOOM_OUT
			Constant.afvideo = false;
			Constant.curmodel = "";
			init();
		} else if (keyCode == 86 && event.getAction() == KeyEvent.ACTION_DOWN) {//KEYCODE_MEDIA_STOP
			if (vp != null) {
				Constant.itemgo = true;
				vp.pause2();
				Constant.li.writeLog("0000 " + R.string.log40);
			}
		} else if (keyCode == 85 && event.getAction() == KeyEvent.ACTION_DOWN) {//KEYCODE_MEDIA_PLAY_PAUSE
			if (vp != null) {
				Constant.itemgo = false;
				vp.start();
				Constant.li.writeLog("0000 " + R.string.log41);
			}
		} else if (keyCode == 24 && event.getAction() == KeyEvent.ACTION_DOWN) {//KEYCODE_VOLUME_UP
			try {
				AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
				mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
						AudioManager.ADJUST_RAISE,
						AudioManager.FX_FOCUS_NAVIGATION_UP);
				Constant.li.writeLog("0000 " + R.string.log42);
			} catch (Exception e) {
				Constant.li.writeLog("0000 " + R.string.log43);
			}

		} else if (keyCode == 25 && event.getAction() == KeyEvent.ACTION_DOWN) {//KEYCODE_VOLUME_DOWN
			try {
				AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
				mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
						AudioManager.ADJUST_LOWER,
						AudioManager.FX_FOCUS_NAVIGATION_UP);
				Constant.li.writeLog("0000 " + R.string.log44);
			} catch (Exception e) {
				Constant.li.writeLog("0000 " + R.string.log45);
			}

		} else if (keyCode == 99 && event.getAction() == KeyEvent.ACTION_DOWN) {//KEYCODE_BUTTON_X
			try {
				AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
				if (mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == 0) {
					mAudioManager
							.setStreamMute(AudioManager.STREAM_MUSIC, true);
					Constant.li.writeLog("0000 " + R.string.log46);
				} else {
					mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC,
							false);
					Constant.li.writeLog("0000 " + R.string.log47);
				}
			} catch (Exception e) {
				Constant.li.writeLog("0000 " + R.string.log48);
			}

		} else if (keyCode == 0 && event.getAction() == KeyEvent.ACTION_DOWN) {//KEYCODE_UNKNOWN

			// Tool.disable_watchdog();

		} else if (keyCode == KeyEvent.KEYCODE_DPAD_UP
				&& event.getAction() == KeyEvent.ACTION_DOWN) {//方向键 up

			if (vp != null) {
				vp.last();
				Constant.li.writeLog("0000  " + R.string.log49);
			} else {
				return false;
			}
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if (vp != null) {
				vp.next();
				Constant.li.writeLog("0000  " + R.string.log50);
			} else {
				return false;
			}
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if (vp != null) {
				vp.slow();
				Constant.li.writeLog("0000  " + R.string.log51);
			} else {
				return false;
			}
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if (vp != null) {
				vp.fast();
				Constant.li.writeLog("0000  " + R.string.log52);
			} else {
				return false;
			}
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
			return false;
		}else if (keyCode == 82 && event.getAction() == KeyEvent.ACTION_DOWN ) {//KEYCODE_MENU
	    	   com.zckj.setting.Menu.show(this);
	    	   return true;
	        } 
		else {
			return false;
		}
		return true;
	}

	
	@Override
	// public boolean dispatchTouchEvent(MotionEvent ev) {
	// // 注意这里不能用ONTOUCHEVENT方法，不然无效的
	// if (!Constant.itemgo) {
	// Constant.chu = true;
	// Constant.chutime = Constant.urltime;
	// }
	// // wv.dispatchTouchEvent(ev);
	// wv.onTouchEvent(ev);
	// // 这几行代码也要执行，将webview载入MotionEvent对象一下，况且用载入把，不知道用什么表述合适
	// return super.dispatchTouchEvent(ev);
	// }
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			float sidex = 0;
			float sidey = 0;
			if (!Constant.itemgo) {
				Constant.chu = true;
				Constant.chutime = Constant.urltime;
			}

			sidex = event.getX();
			sidey = event.getY();
			if (sidex < (dm.widthPixels / 5) && sidey < (dm.heightPixels / 5)) {
				Constant.outsideCount1 = true;
			} else if (sidex > (dm.widthPixels - (dm.widthPixels / 5))
					&& sidey < (dm.heightPixels / 5)) {
				Constant.outsideCount2 = true;
			} else if (sidex > (dm.widthPixels - (dm.widthPixels / 5))
					&& sidey > (dm.heightPixels - (dm.heightPixels / 5))) {
				Constant.outsideCount3 = true;
			} else if (sidex < (dm.widthPixels / 5)
					&& sidey > (dm.heightPixels - (dm.heightPixels / 5))) {
				Constant.outsideCount4 = true;
			} else {
				Constant.outsideCount1 = false;
				Constant.outsideCount2 = false;
				Constant.outsideCount3 = false;
				Constant.outsideCount4 = false;
			}
		}
		return false;
	}

	private void init() {
		initConfig();
	}

	private int getWeekOfDate() {
		Calendar cal = Calendar.getInstance();
		int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (w <= 0) {
			w = 7;
		}
		return w;
	}

	private void initShow() {
		try {
			dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);
			Constant.width = dm.widthPixels;
			Constant.height = dm.heightPixels;
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void initparam() {
		Constant.msg = "";
		Constant.downmsg = "";
		Constant.tempmsg = "";
		Constant.change = 0;
		Constant.chu = false;
		Constant.itemgo = false;

	}

	private void initConfig() {
		initparam();
		// Tool.disable_watchdog();
		File fsddsd = new File(Constant.USBUPDATE + "vsplayer.apk");
		File fsddsd2 = new File(Constant.USBUPDATE2 + "vsplayer.apk");

		if (fsddsd.exists()) {
			Tool.copyFile(Constant.updateDir + "vsplayer.apk", fsddsd);
			onDestroy2();
			Intent ite = new Intent(this, BootUpReceiver.class);
			ite.setAction("myAction");
			PendingIntent SENDER = PendingIntent.getBroadcast(this, 0, ite,
					PendingIntent.FLAG_CANCEL_CURRENT);
			AlarmManager ALARM = (AlarmManager) getSystemService(ALARM_SERVICE);
			ALARM.set(AlarmManager.RTC_WAKEUP,
					System.currentTimeMillis() + 90000, SENDER);
			Tool.install(Constant.updateDir + "vsplayer.apk");
			Constant.li.writeLog("0000  远程更新apk");
			finish();
			return;
		} else if (fsddsd2.exists()) {
			Tool.copyFile(Constant.updateDir + "vsplayer.apk", fsddsd2);
			onDestroy2();
			Intent ite = new Intent(this, BootUpReceiver.class);
			ite.setAction("myAction");
			PendingIntent SENDER = PendingIntent.getBroadcast(this, 0, ite,
					PendingIntent.FLAG_CANCEL_CURRENT);
			AlarmManager ALARM = (AlarmManager) getSystemService(ALARM_SERVICE);
			ALARM.set(AlarmManager.RTC_WAKEUP,
					System.currentTimeMillis() + 90000, SENDER);
			Tool.install(Constant.updateDir + "vsplayer.apk");
			Constant.li.writeLog("0000  " + R.string.log53);
			finish();
			return;
		}
		File fff = new File(Constant.USBPAN);
		File fff2 = new File(Constant.USBPAN2);
		File of3 = new File(Constant.offDir);
		if (fff.exists()) {
			Tool.deleteDirectory(of3);
			of3.mkdir();
			File[] fs = fff.listFiles();
			File fsd;
			String fd;
			String fnn;
			File fnan;
			File[] fs2;
			File fsd2;
			for (int i = 0; i < fs.length; i++) {
				fsd = fs[i];
				if (!fsd.isDirectory()) {
					Tool.copyFile(Constant.offDir + fsd.getName(), fsd);
					fnn = Constant.offDir + fsd.getName();
					if (fnn.endsWith(".zip")) {
						fd = fnn.substring(0, fnn.lastIndexOf("."));
						try {
							HttpDown.unzip(fnn, fd);
						} catch (Exception e) {

						}
					}
				} else {
					fnan = new File(Constant.offDir + fsd.getName());
					fnan.mkdir();
					fs2 = fsd.listFiles();
					for (int j = 0; j < fs2.length; j++) {
						fsd2 = fs2[j];
						if (!fsd2.isDirectory()) {
							Tool.copyFile(Constant.offDir + fsd.getName()
									+ File.separator + fsd2.getName(), fsd2);
						}
					}
				}
			}
		} else if (fff2.exists()) {
			Tool.deleteDirectory(of3);
			of3.mkdir();
			File[] fs = fff2.listFiles();
			File fsd;
			String fd;
			String fnn;
			File fnan;
			File[] fs2;
			File fsd2;
			for (int i = 0; i < fs.length; i++) {
				fsd = fs[i];
				if (!fsd.isDirectory()) {
					Tool.copyFile(Constant.offDir + fsd.getName(), fsd);
					fnn = Constant.offDir + fsd.getName();
					if (fnn.endsWith(".zip")) {
						fd = fnn.substring(0, fnn.lastIndexOf("."));
						try {
							HttpDown.unzip(fnn, fd);
						} catch (Exception e) {

						}
					}
				} else {
					fnan = new File(Constant.offDir + fsd.getName());
					fnan.mkdir();
					fs2 = fsd.listFiles();
					for (int j = 0; j < fs2.length; j++) {
						fsd2 = fs2[j];
						if (!fsd2.isDirectory()) {
							Tool.copyFile(Constant.offDir + fsd.getName()
									+ File.separator + fsd2.getName(), fsd2);
						}
					}
				}
			}
		}
		clearWebCache();

		// Tool.enable_watchdog();

		Constant.curday = getWeekOfDate();

		String[] params2 = Tool.loadConfig(new String[] { "srvip", "g3",
				"video", "out", "dsitem", "tbtime", "dsTime" },
				Constant.config2);

		try {
			Constant.tuichu = params2[3].trim();
		} catch (Exception e) {
		}

		Constant.VIDEOAGAIN = 0;
		Constant.dsTime = 0;
		Constant.dsItem = 0;
		Constant.tbtime = "";
		if (params2 == null) {
			Constant.sc.closeSocket();
			Constant.SRVIP = "192.168.1.101";
			Constant.G3G = false;
		} else {

			if (!params2[4].trim().equals("")) {
				Constant.dsItem = Integer.parseInt(params2[4].trim());
			}
			Constant.tbtime = params2[5].trim();
			if (!params2[6].trim().equals("")) {
				Constant.dsTime = Integer.parseInt(params2[6].trim());
			}

			String stsvip = params2[0].trim();
			int ggg3 = 0;
			try {
				ggg3 = Integer.parseInt(params2[1].trim());
			} catch (Exception e) {

			}
			if (ggg3 == 0) {
				Constant.G3G = false;
			} else {
				Constant.G3G = true;
			}

			try {
				Constant.VIDEOAGAIN = Integer.parseInt(params2[2].trim());
			} catch (Exception e) {

			}
			Constant.VIDEOAGAIN = 5;
			if (!stsvip.equals(Constant.SRVIP)) {
				Constant.sc.closeSocket();
			}
			Constant.SRVIP = stsvip;
			if (Constant.SRVIP.length() < 1) {
				Constant.sc.closeSocket();
				Constant.SRVIP = "192.168.1.101";
			}
		}

		if ((Constant.SRVIP.indexOf(".com") == -1)
				&& (Constant.SRVIP.indexOf(".net") == -1)
				&& (Constant.SRVIP.indexOf(".cn") == -1)
				&& (Constant.SRVIP.indexOf(".cc") == -1)) {
			String[] ssrvip = Constant.SRVIP.split("\\.");
			if ((ssrvip.length != 4) && (ssrvip.length != 6)) {
				Constant.SRVIP = "192.168.1.101";
			}

			for (int i = 0; i < ssrvip.length; i++) {
				try {
					int curdian = Integer.parseInt(ssrvip[i].trim());
					if (curdian < 0 || curdian > 255) {
						Constant.SRVIP = "192.168.1.101";
						break;
					}
				} catch (Exception e) {
					Constant.SRVIP = "192.168.1.101";
					break;
				}
			}
		}

		String[] downparam = Tool.loadConfig(new String[] { "downtotal",
				"alltotal" }, Constant.config3);
		Constant.downtotal = 0;
		Constant.alltotal = 0;
		if (downparam != null) {
			try {
				Constant.downtotal = Long.parseLong(downparam[0].trim());
			} catch (Exception e) {

			}
			try {
				Constant.alltotal = Long.parseLong(downparam[1].trim());
			} catch (Exception e) {

			}
		}

		// 串口
		// File tf = new File(Constant.tfil);
		// if (tf.exists()) {
		// try {
		// BufferedReader fad = new BufferedReader(new FileReader(
		// Constant.tfil));
		// Constant.elevatorStr = fad.readLine().trim();
		// fad.close();
		// } catch (Exception e) {
		//
		// }
		// Constant.eleopen = true;
		// } else {
		// Constant.eleopen = false;
		// }

		String[] params = Tool.loadConfig(new String[] { "playlist",
				"timelist", "fontcontent", "up", "backcolor", "fonttype",
				"fontcolor", "fontspeed", "fontsize", "shut", "xiansu", "lian",
				"url", "urltime", "sendkey", "xuan", "cblist", "camera",
				"worktime", "sync", "dplist", "scrollfont" }, Constant.config);

		String[] offparam = Tool.loadConfig(new String[] { "playlist",
				"timelist", "cblist", "imgtime", "scale", "dplist" },
				Constant.config4);

		Date date = new Date();
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateNow = sf.format(date);
		String dn = dateNow.split(" ")[0];
		SimpleDateFormat sf2 = new SimpleDateFormat("HH:mm:ss");
		String timeNow = sf2.format(date);
		String[] pls;
		String pl;
		String[] pts;
		String tem11;
		String tem22;
		String tem33;
		String tem44;
		String tem55;

		int temp11 = 0;

		Constant.nexttime = "";

		String plst = "";
		String cblist = "";
		String timelist = "";
		String dplist = "";
		Constant.imgtime = 0;
		if (offparam == null) {
			Constant.offshow = false;
			Constant.scale = 0;
			plst = params[0].trim();
			timelist = params[1].trim();
			cblist = params[16].trim();
			dplist = params[20].trim();
		} else {
			plst = offparam[0].trim();
			timelist = offparam[1].trim();
			cblist = offparam[2].trim();
			dplist = offparam[5].trim();
			try {
				Constant.imgtime = Integer.parseInt(offparam[3].trim());
			} catch (Exception e) {
				// TODO: handle exception
			}
			try {
				Constant.scale = Integer.parseInt(offparam[4].trim());
			} catch (Exception e) {
			}
			Constant.offshow = true;
		}
		try {
			Constant.scrollfont = params[21].trim();
		} catch (Exception e) {
			// TODO: handle exception
		}

		Constant.modelcontent = plst;
		Constant.cblbStr = this.getString(R.string.log11);
		if (timelist.length() > 0) {
			pls = timelist.split("\\^");
			for (int i = 0; i < pls.length; i++) {
				pl = pls[i].trim();
				pts = pl.split("\\_");
				if (pts.length == 4) {
					tem11 = pts[0].trim();
					tem22 = pts[1].trim();
					tem33 = pts[2].trim();
					tem44 = pts[3].trim();
					try {
						temp11 = Integer.parseInt(tem11);
					} catch (Exception e) {
						// TODO: handle exception
					}
					if (temp11 == 8 || temp11 == Constant.curday) {
						if ((tem22.compareTo(timeNow) <= 0)
								&& (tem33.compareTo(timeNow) > 0)) {
							Constant.nexttime = tem33;
							Constant.modelcontent = tem44;
							Constant.cblbStr = this.getString(R.string.log54);
							break;
						} else if (tem22.compareTo(timeNow) > 0) {
							if (Constant.nexttime.isEmpty()) {
								Constant.nexttime = tem22;
							} else {
								if (tem22.compareTo(Constant.nexttime) <= 0) {
									Constant.nexttime = tem22;
								}
							}
						} else {
							if (Constant.showInfo) {
								Constant.li.writeLog("0000 " + R.string.log55);
								Constant.showInfo = false;
							}
						}
					}
				}
			}
		}

		if (cblist.length() > 0) {
			String nexcb = "";
			String ds = "";
			pls = cblist.split("\\^");
			for (int i = 0; i < pls.length; i++) {
				pl = pls[i].trim();
				pts = pl.split("\\_");
				if (pts.length == 3) {
					tem11 = pts[0].trim();
					tem22 = pts[1].trim();
					tem33 = pts[2].trim();
					try {
						ds = tem22.split(" ")[0];
					} catch (Exception e) {

					}
					if ((tem11.compareTo(dateNow) <= 0)
							&& (tem22.compareTo(dateNow) > 0)) {
						if (ds.equals(dn)) {
							try {
								Constant.nexttime = tem22.split(" ")[1];
							} catch (Exception e) {

							}
						} else {
							Constant.nexttime = "";
						}
						Constant.modelcontent = tem33;
						Constant.cblbStr = this.getString(R.string.log56);
						break;
					} else if (tem11.compareTo(dateNow) > 0) {
						if (nexcb.isEmpty()) {
							nexcb = tem11;
						} else {
							if (tem11.compareTo(nexcb) <= 0) {
								nexcb = tem11;
							}
						}
					} else {
						if (Constant.showInfo) {
							Constant.li.writeLog("0000 " + R.string.log57);
							Constant.showInfo = false;
						}
					}
				}
			}

			if (nexcb.length() > 0) {
				try {
					ds = nexcb.split(" ")[0];
				} catch (Exception e) {

				}
				if (ds.equals(dn)) {
					try {
						if (Constant.nexttime == "") {
							Constant.nexttime = nexcb.split(" ")[1];
						} else {
							if (Constant.nexttime
									.compareTo(nexcb.split(" ")[1]) > 0) {
								Constant.nexttime = nexcb.split(" ")[1];
							}
						}
					} catch (Exception e) {

					}
				}
			}
		}

		// 140142967673884
		// dplist
		// ="2014-05-19 09:32:00_2014-06-19 15:36:53_2_2_140142967673884";
		if (dplist.length() > 0 && !Constant.dpTimeCount) {
			String dpStartDay = "";
			String dpStartTime = "";
			String dpEndDay = "";
			String dpEndTime = "";
			int endTimeDay = -1;
			int startTimeDay = -1;
			pls = dplist.split("\\_");
			if (pls.length == 5) {
				tem11 = pls[0].trim();
				tem22 = pls[1].trim();
				// 分钟
				tem33 = pls[2].trim();
				// 次数
				tem44 = pls[3].trim();
				tem55 = pls[4].trim();
				try {
					Constant.dpPlayCount = Integer.parseInt(tem44);
				} catch (Exception e) {
				}

				if (!tem33.equals("")) {
					try {
						Constant.dpTime = Integer.parseInt(tem33) * 60;
					} catch (Exception e) {
					}

				}
				if (tem11.split(" ").length == 2) {
					dpStartDay = tem11.split(" ")[0];
					dpStartTime = tem11.split(" ")[1];
					startTimeDay = compareStartTimeDay(dn, dpStartDay);
				}
				if (tem22.split(" ").length == 2) {
					dpEndDay = tem22.split(" ")[0];
					dpEndTime = tem22.split(" ")[1];
					endTimeDay = compareEndTimeDay(dn, dpEndDay);
				}

				if (startTimeDay == 0 && endTimeDay == 1) {
					Constant.dpIsStart = true;
					Constant.modelcontent = tem55;
				} else if (startTimeDay == 2) {
					if (compareTime(timeNow, dpStartTime)) {
						if (!compareTime(timeNow, dpEndTime)) {
							Constant.dpIsStart = true;
							Constant.modelcontent = tem55;
						}
						if (endTimeDay == 1) {
							Constant.dpIsStart = true;
							Constant.modelcontent = tem55;
						} else if (endTimeDay == 2) {
							if (!compareTime(timeNow, dpEndTime)) {
								Constant.dpIsStart = true;
								Constant.modelcontent = tem55;
							}
						}
					}
				} else if (startTimeDay == 0 && endTimeDay == 2) {
					if (!compareTime(timeNow, dpEndTime)) {
						Constant.dpIsStart = true;
						Constant.modelcontent = tem55;
					}
				}
			}
			if (Constant.dpIsStart) {
				if (vp != null) {
					String vpGoon = vp.getcur();
					String vps[] = vpGoon.split("_");
					if (vps.length == 2) {
						try {
							Constant.VpGoONCur = Integer
									.parseInt(vps[0].trim());
						} catch (Exception e) {
							// TODO: handle exception
						}
						try {
							Constant.VpGoONTime = Integer.parseInt(vps[1]
									.trim());
						} catch (Exception e) {
							// TODO: handle exception
						}
					}

				}
			}
		}

		// Constant.fonttime =
		// "2014-06-09 10:21:17_2014-06-09 10:22:17^2013-06-09 11:00:17_201-06-09 12:22:17";
		// dateNow
		Constant.scrollfont = "fontcontent|爱我的的疯狂破自行车上次正常安文峰起案子从而非啊色粉啊制成品*up|-1*backcolor|0*fonttype|0*fontcolor|-16777216*fontspeed|5*fontsize|50*fontcontent2|*up2|*backcolor2|*fonttype2|*fontcolor2|*fontspeed2|*fontsize2|*fonttime|2014-11-27 16:10:07_2015-11-27 18:00:00";
		if (Constant.scrollfont.length() > 0) {
			boolean stopf = true;
			String font[] = null;
			String stimes = "";
			String etimes = "";
			String setime[] = null;
			String fts[] = null;
			String fc[] = null;
			String ft[] = Constant.scrollfont.split("\\^");
			for (int i = 0; i < ft.length; i++) {
				font = ft[i].split("\\*");
				if (font.length == 15) {

					try {
						fts = font[14].split("\\|");
					} catch (Exception e) {

					}

					if (fts.length == 2) {
						try {
							setime = fts[1].split("\\_");
						} catch (Exception e) {
						}
						if (setime.length == 2) {
							try {
								stimes = setime[0].trim();
							} catch (Exception e) {
								// TODO: handle exception
							}
							try {
								etimes = setime[1].trim();
							} catch (Exception e) {
								// TODO: handle exception
							}
							if (!compareFontTimeDay(dateNow, stimes)) {
								if (Constant.nextfontTime.equals("")) {
									Constant.nextfontTime = stimes;
									stopf = true;
									Constant.fontStart = false;
								} else {
									if (compareFontTimeDay(
											Constant.nextfontTime, stimes)) {
										Constant.nextfontTime = stimes;
										stopf = true;
										Constant.fontStart = false;
									}
								}

							} else if (compareFontTimeDay(dateNow, stimes)
									&& (compareFontTimeDay(etimes, dateNow))) {
								Constant.nextfontTime = etimes;
								stopf = false;
								Constant.fontStart = true;
								Tool.saveConfig("fontindex!" + i,
										Constant.config);
								fc = font[0].split("\\|");
								try {
									Constant.fontcontent = fc[1].trim();
								} catch (Exception e) {
									// TODO: handle exception
								}

								fc = font[2].split("\\|");
								try {
									Constant.backcolor = Integer.parseInt(fc[1]
											.trim());
								} catch (Exception e) {
									// TODO: handle exception
								}

								fc = font[3].split("\\|");
								try {
									Constant.fonttype = Integer.parseInt(fc[1]
											.trim());
								} catch (Exception e) {
									// TODO: handle exception
								}

								fc = font[4].split("\\|");
								try {
									Constant.fontcolor = Integer.parseInt(fc[1]
											.trim());
								} catch (Exception e) {
									// TODO: handle exception
								}

								fc = font[5].split("\\|");
								try {
									Constant.fontspeed = Integer.parseInt(fc[1]
											.trim());
								} catch (Exception e) {
									// TODO: handle exception
								}

								fc = font[6].split("\\|");
								try {
									Constant.fontsize = Integer.parseInt(fc[1]
											.trim());
								} catch (Exception e) {
									// TODO: handle exception
								}

							}
						}
					}
				}
			}
			if (stopf) {
				Constant.fontStart = false;
				Tool.saveConfig("fontindex! ", Constant.config);
				stopFont();
			}
		} else {
			Constant.fontStart = true;
		}

		// Constant.fontcontent = params[2].trim();

		Constant.up = 0;
		// Constant.backcolor = Color.WHITE; // 濠婃艾濮╃�妤�閼冲本娅欐０婊嗗
		// Constant.fonttype = 0; // 濠婃艾濮╃�妤�鐎涙ぞ缍嬬猾璇茬�
		// Constant.fontcolor = Color.BLACK; // 濠婃艾濮╃�妤�鐎涙ぞ缍嬫０婊嗗
		// Constant.fontspeed = 2.0f; // 濠婃艾濮╃�妤�濠婃艾濮╅柅鐔峰
		// Constant.fontsize = 35f; // 濠婃艾濮╃�妤�閺傚洤鐡ф径褍鐨� Constant.xiansu = 0;
		// // 娑撳娴囬梽鎰帮拷
		Constant.lian = 10; // 韫囧啳鐑︽潻鐐村复閺冨爼妫� Constant.urltime = 0; //
							// 鐟欙附鎳滈梻鎾閺冨爼妫�
		// if (Constant.fontcontent.length() > 0) {
		// try {
		// Constant.up = Integer.parseInt(params[3].trim());
		// } catch (Exception e) {
		//
		// }
		// try {
		// Constant.backcolor = Integer.parseInt(params[4].trim());
		// } catch (Exception e) {
		//
		// }
		// try {
		// Constant.fonttype = Integer.parseInt(params[5].trim());
		// } catch (Exception e) {
		//
		// }
		// try {
		// Constant.fontcolor = Integer.parseInt(params[6].trim());
		// } catch (Exception e) {
		//
		// }
		// try {
		// Constant.fontspeed = Float.parseFloat(params[7].trim());
		// } catch (Exception e) {
		//
		// }
		// try {
		// Constant.fontsize = Float.parseFloat(params[8].trim());
		// } catch (Exception e) {
		//
		// }
		//
		// }

		try {
			Constant.lian = Integer.parseInt(params[11].trim());
		} catch (Exception e) {

		}
		if (Constant.lian == 0) {
			Constant.lian = 10;
		}

		try {
			Constant.xiansu = Integer.parseInt(params[10].trim());
		} catch (Exception e) {

		}

		try {
			Constant.urltime = Integer.parseInt(params[13].trim());
		} catch (Exception e) {

		}
		if (Constant.urltime == 0) {
			Constant.urltime = 30;
		}

		Constant.camera = 0;
		try {
			Constant.camera = Integer.parseInt(params[17].trim());
		} catch (Exception e) {

		}
		Constant.shut = params[9].trim();
		// Constant.shut = "8_09:05_10:00^8_11:00_12:00^8_16:00_17:30";
		Constant.sendkey = params[14].trim();
		// HashSet hss = new HashSet();
		// HashSet hs2 = new HashSet();
		// int nextday = 0;
		// StartTime st;
		// String endtime = "";
		// String starttime = "";
		// String temstart = "";
		// Constant.CloseTime = "";
		// Constant.dayStr = 0;
		// ArrayList<String> al = new ArrayList<String>();
		// boolean shezhi = false;
		// if (Constant.shut.length() > 0) {
		// pls = Constant.shut.split("\\^");
		// for (int i = 0; i < pls.length; i++) {
		// pl = pls[i].trim();
		// pts = pl.split("\\_");
		// if (pts.length == 3) {
		// tem11 = pts[0].trim();
		// temstart = pts[1].trim();
		// tem33 = pts[2].trim();
		// try {
		// temp11 = Integer.parseInt(tem11);
		// } catch (Exception e) {
		// // TODO: handle exception
		// }
		// if (temp11 == 8 || temp11 == Constant.curday) {
		// hss.add(temstart);
		// if (tem33.compareTo(timeNow) >= 0) {
		// if (endtime.isEmpty()) {
		// endtime = tem33;
		// Constant.CloseTime = tem33;
		// Constant.dayStr = temp11;
		// } else {
		// if (tem33.compareTo(endtime) <= 0) {
		// endtime = tem33;
		// Constant.CloseTime = tem33;
		// Constant.dayStr = temp11;
		//
		// }
		// }
		// }
		// }
		// if (temp11 != Constant.curday) {
		// st = new StartTime();
		// if (temp11 < Constant.curday) {
		// temp11 = temp11 + 7;
		// } else if (temp11 == 8) {
		// temp11 = Constant.curday + 1;
		// }
		// st.day = temp11;
		// st.startt = temstart;
		// hs2.add(st);
		// }
		// }
		// }
		// if (endtime.length() > 0) {
		// Iterator it = hss.iterator();
		// String temit;
		// while (it.hasNext()) {
		// temit = (String) it.next();
		// if (temit.compareTo(endtime) >= 0) {
		// if (starttime.isEmpty()) {
		// starttime = temit;
		// } else {
		// if (temit.compareTo(starttime) <= 0) {
		// starttime = temit;
		// }
		// }
		// }
		// }
		// if (starttime.isEmpty()) {
		// Iterator it2 = hs2.iterator();
		// StartTime sst;
		// while (it2.hasNext()) {
		// sst = (StartTime) it2.next();
		// if (nextday == 0) {
		// nextday = sst.day;
		// starttime = sst.startt;
		// } else {
		// if (sst.day < nextday) {
		// nextday = sst.day;
		// starttime = sst.startt;
		// } else if (sst.day == nextday) {
		// if (sst.startt.compareTo(starttime) <= 0) {
		// starttime = sst.startt;
		// }
		// }
		// }
		// }
		// }
		//
		// if (!Constant.CloseTime.equals("")) {
		// String hour = "";
		// String minute = "";
		// if (nextday != 0) {
		// nextday = nextday - Constant.curday;
		// }
		//
		// String time[] = Constant.CloseTime.split("\\:");
		// if (time.length == 2) {
		// try {
		// hour = time[0].trim();
		// } catch (Exception e) {
		// }
		// try {
		// minute = time[1].trim();
		// } catch (Exception e) {
		// }
		// }
		// try {
		// Constant.CloseTimes = (Integer.parseInt(hour) * 60)
		// + Integer.parseInt(minute);
		// } catch (Exception e) {
		// }
		// }
		//
		// byte s1 = 100;
		// byte s2 = 10;
		// byte e1 = 100;
		// byte e2 = 10;
		// int yonghour = 0;
		// int yongfen = 0;
		// int curhour = 0;
		// int curfen = 0;
		// String[] yong;
		// String[] curr;
		// int shenf = 0;
		// int shenh = 0;
		// if (!endtime.isEmpty()) {
		// yong = endtime.split("\\:");
		// curr = timeNow.split("\\:");
		// if (curr.length == 3) {
		// try {
		// curhour = Integer.parseInt(curr[0]);
		// curfen = Integer.parseInt(curr[1]);
		// } catch (Exception e) {
		// // TODO: handle exception
		// }
		// }
		// if (yong.length == 2) {
		// try {
		// yonghour = Integer.parseInt(yong[0]);
		// yongfen = Integer.parseInt(yong[1]);
		// } catch (Exception e) {
		// // TODO: handle exception
		// }
		// shenh = yonghour;
		// shenf = yongfen;
		// }
		// if (yongfen < curfen) {
		// yongfen = yongfen + 60 - curfen;
		// yonghour = yonghour - 1 - curhour;
		// } else {
		// yongfen = yongfen - curfen;
		// yonghour = yonghour - curhour;
		// }
		// if ((yonghour == 0) && (yongfen < 5)) {
		// yongfen = 5;
		// }
		// e1 = (byte) yonghour;
		// e2 = (byte) yongfen;
		// }
		// if (!starttime.isEmpty()) {
		// yonghour = 0;
		// yongfen = 0;
		// yong = starttime.split("\\:");
		// if (yong.length == 2) {
		// try {
		// yonghour = Integer.parseInt(yong[0]);
		// yongfen = Integer.parseInt(yong[1]);
		// } catch (Exception e) {
		// // TODO: handle exception
		// }
		// }
		// if (nextday > 0) {
		// yonghour = nextday * 24 + yonghour;
		// }
		// if (yongfen < shenf) {
		// yongfen = yongfen + 60 - shenf;
		// yonghour = yonghour - 1 - shenh;
		// } else {
		// yongfen = yongfen - shenf;
		// yonghour = yonghour - shenh;
		// }
		// if ((yonghour == 0) && (yongfen < 5)) {
		// yongfen = 5;
		// }
		// s1 = (byte) yonghour;
		// s2 = (byte) yongfen;
		// }
		// shezhi = true;
		// Constant.li.writeLog("0000 " + R.string.log58 + starttime
		// + R.string.log59 + endtime);
		// Tool.setPowerOnOff(e1, e2, s1, s2, (byte) 3);
		// }
		// }
		// if (!Constant.worktime.equals("")) {
		// String[] time = Constant.worktime.split("\\_");
		// int nextday2 = 0;
		// int starttimeh = 0;
		// int starttimem = 0;
		// int endtimeh = 0;
		// int endtimem = 0;
		// int nowtimeh = 0;
		// int nowtimem = 0;
		// String nowtime = "";
		// if (time.length == 2) {
		// Constant.starttime = time[0].trim();
		// Constant.endtime = time[1].trim();
		// String sh[] = Constant.starttime.split("\\:");
		// if (sh.length == 2) {
		// try {
		// starttimeh = Integer.parseInt(sh[0].trim());
		// } catch (Exception e) {
		// }
		// try {
		// starttimem = Integer.parseInt(sh[1].trim());
		// } catch (Exception e) {
		// }
		// }
		// String eh[] = Constant.endtime.split("\\:");
		// if (eh.length == 2) {
		// try {
		// endtimeh = Integer.parseInt(eh[0].trim());
		// } catch (Exception e) {
		// }
		// try {
		// endtimem = Integer.parseInt(eh[1].trim());
		// } catch (Exception e) {
		// }
		// }
		// String nh[] = timeNow.split("\\:");
		// if (nh.length == 3) {
		// try {
		// nowtimeh = Integer.parseInt(nh[0].trim());
		// } catch (Exception e) {
		// }
		// try {
		// nowtimem = Integer.parseInt(nh[1].trim());
		// } catch (Exception e) {
		// }
		// nowtime = nowtimeh + ":" + nowtimem;
		// }
		// // 7:00 15:10 15:30
		// if (endtimeh < starttimeh) {
		// nextday2 = 1;
		// } else if (endtimeh == starttimeh) {
		// if (endtimem <= starttimem) {
		// nextday2 = 1;
		// }
		// }else if(starttimeh < endtimeh && nowtimeh > endtimeh){
		// nextday2 = 1;
		// }else if(starttimeh < endtimeh && nowtimeh > endtimeh){
		// if(nowtimem > endtimem){
		// nextday2 = 1;
		// }
		// }
		// if (nowtimeh < starttimeh || nowtimeh > endtimeh) {
		// Constant.endtime = nowtime;
		// } else if (nowtimeh == starttimeh || nowtimeh == endtimeh) {
		// if (nowtimem < starttimem) {
		// Constant.endtime = nowtime;
		// } else if (nowtimem >= endtimem) {
		// Constant.endtime = nowtime;
		// }
		// }
		//
		// byte s1 = 100;
		// byte s2 = 10;
		// byte e1 = 100;
		// byte e2 = 10;
		// int yonghour = 0;
		// int yongfen = 0;
		// int curhour = 0;
		// int curfen = 0;
		// String[] yong;
		// String[] curr;
		// int shenf = 0;
		// int shenh = 0;
		// if (!Constant.endtime.isEmpty()) {
		// yong = Constant.endtime.split("\\:");
		// curr = timeNow.split("\\:");
		// if (curr.length == 3) {
		// try {
		// curhour = Integer.parseInt(curr[0]);
		// curfen = Integer.parseInt(curr[1]);
		// } catch (Exception e) {
		// // TODO: handle exception
		// }
		// }
		// if (yong.length == 2) {
		// try {
		// yonghour = Integer.parseInt(yong[0]);
		// yongfen = Integer.parseInt(yong[1]);
		// } catch (Exception e) {
		// // TODO: handle exception
		// }
		// shenh = yonghour;
		// shenf = yongfen;
		// }
		// if (yongfen < curfen) {
		// yongfen = yongfen + 60 - curfen;
		// yonghour = yonghour - 1 - curhour;
		// } else {
		// yongfen = yongfen - curfen;
		// yonghour = yonghour - curhour;
		// }
		// if ((yonghour == 0) && (yongfen < 5)) {
		// yongfen = 5;
		// }
		// e1 = (byte) yonghour;
		// e2 = (byte) yongfen;
		// }
		// if (!Constant.starttime.isEmpty()) {
		// yonghour = 0;
		// yongfen = 0;
		// yong = Constant.starttime.split("\\:");
		// if (yong.length == 2) {
		// try {
		// yonghour = Integer.parseInt(yong[0]);
		// yongfen = Integer.parseInt(yong[1]);
		// } catch (Exception e) {
		// // TODO: handle exception
		// }
		// }
		// if (nextday2 > 0) {
		// yonghour = nextday * 24 + yonghour;
		// }
		// if (yongfen < shenf) {
		// yongfen = yongfen + 60 - shenf;
		// yonghour = yonghour - 1 - shenh;
		// } else {
		// yongfen = yongfen - shenf;
		// yonghour = yonghour - shenh;
		// }
		// if ((yonghour == 0) && (yongfen < 5)) {
		// yongfen = 5;
		// }
		// s1 = (byte) yonghour;
		// s2 = (byte) yongfen;
		// }
		// Constant.li.writeLog("0000 " + R.string.log58 + starttime
		// + R.string.log59 + endtime);
		// shezhi = true;
		// Tool.setPowerOnOff(e1, e2, s1, s2, (byte) 3);
		// }
		// }
		//
		// if (!shezhi) {
		// Tool.setPowerOnOff((byte) 0, (byte) 5, (byte) 0, (byte) 5, (byte) 0);
		// }
		try {
			Constant.sync = Integer.parseInt(params[19].trim());
		} catch (Exception e) {
		}
		if (Constant.sync == 1) {
			if (ms == null) {
				Constant.li.writeLog("0000 " + R.string.log60);
				ms = new MainServer();
				ms.start();
			}
		}
		if (!Constant.guanji) {
			initCamera();
			initPlay();
			if (Constant.fontStart) {
				initFont();
			}
		}
	}

	private void upCamera() {
		// Tool.disable_watchdog();
		// Constant.curmodel = "";
		// stopPlay();

		DoZip zip = new DoZip(Constant.cDir, Constant.cDir + ".zip");

		if (new File(Constant.cDir + ".zip").exists()) {
			new File(Constant.cDir + ".zip").delete();
		}
		zip.start();
		//
		// Tool.enable_watchdog();
		// init();
	}

	private void initCamera() {
		if (Constant.sPic != null) {
			Constant.sPic.releaseCamera();
			Constant.sPic = null;
		}
		if (Constant.rcamera != null) {
			Constant.rcamera.rel();
			abslayout.removeView(Constant.rcamera);
			Constant.rcamera = null;
		}
		if (Constant.scamera != null) {
			Constant.scamera.stopMonitoring();
			Constant.scamera = null;
		}
		if (Constant.camera == 1) {
			Constant.li.writeLog("0000 " + R.string.log61);
			Constant.sPic = new SeePic();
		} else if (Constant.camera == 2) {
			Constant.li.writeLog("0000 " + R.string.log62);
			Constant.rcamera = new RecordVideo(this);
			abslayout.addView(Constant.rcamera,
					new AbsoluteLayout.LayoutParams(1, 1, 1, 1));
		} else if (Constant.camera == 3) {
			Constant.li.writeLog("0000 " + R.string.log63);
			Constant.scamera = new SocketCamera(Constant.mac, Constant.SRVIP,
					8514);
			Constant.scamera.startMonitoring();
		}
	}

	private void initAV(String str) {
		String sc[] = str.split("\\*");
		int w = 0;
		int h = 0;
		int x = 0;
		int y = 0;
		if (sc.length == 4) {
			try {
				x = Integer.parseInt(sc[0].trim());
			} catch (Exception e) {
			}
			try {
				y = Integer.parseInt(sc[1].trim());
			} catch (Exception e) {
			}
			try {
				w = Integer.parseInt(sc[2].trim());
			} catch (Exception e) {
			}
			try {
				h = Integer.parseInt(sc[3].trim());
			} catch (Exception e) {
			}
			try {
				cvbs = new CVBS(this, w, h, x, y);
			} catch (Exception e) {
			}

			abslayout
					.addView(cvbs, new AbsoluteLayout.LayoutParams(w, h, x, y));
		}
	}

	private void initShowCamera(String str) {
		String sc[] = str.split("\\*");
		int w = 0;
		int h = 0;
		int x = 0;
		int y = 0;
		if (sc.length == 4) {
			try {
				x = Integer.parseInt(sc[0].trim());
			} catch (Exception e) {
			}
			try {
				y = Integer.parseInt(sc[1].trim());
			} catch (Exception e) {
			}
			try {
				w = Integer.parseInt(sc[2].trim());
			} catch (Exception e) {
			}
			try {
				h = Integer.parseInt(sc[3].trim());
			} catch (Exception e) {
			}
			showCamera = new ShowCamera(this);
			abslayout.addView(showCamera, new AbsoluteLayout.LayoutParams(w, h,
					x, y));
		}
	}

	private void stopPlay() {
		Constant.curmodel = "";
		if (vp != null) {
			vp.stop();
		}
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

		if (Constant.sPic != null) {
			Constant.sPic.releaseCamera();
			Constant.sPic = null;
		}
		if (Constant.rcamera != null) {
			Constant.rcamera.rel();
			abslayout.removeView(Constant.rcamera);
			Constant.rcamera = null;
		}
		if (Constant.scamera != null) {
			Constant.scamera.stopMonitoring();
			Constant.scamera = null;
		}

		if (vp != null) {
			vp.rel();
			abslayout.removeView(vp);
			vp = null;
		}
		if (cvbs != null) {
			cvbs.closeTV();
			abslayout.removeView(cvbs);
			cvbs = null;
		}
		if (mp != null) {
			mp.rel();
			mp = null;
		}
		if (ims != null) {
			ims.recy();
			abslayout.removeView(ims);
			ims = null;
		}

		SeeImageView mmi;
		for (int i = 0; i < Constant.imglist.size(); i++) {
			mmi = (SeeImageView) Constant.imglist.get(i);
			mmi.recy();
			abslayout.removeView(mmi);
			mmi = null;
		}
		Constant.imglist.clear();

		if (tqweb != null) {
			tqweb.clearView();
			abslayout.removeView(tqweb);
			tqweb = null;
		}
		if (timeweb != null) {
			timeweb.clearView();
			abslayout.removeView(timeweb);
			timeweb = null;
		}

		if (showCamera != null) {
			showCamera.stop();
			abslayout.removeView(showCamera);
			showCamera = null;
		}

		if (hlweb != null) {
			hlweb.clearView();
			abslayout.removeView(hlweb);
			hlweb = null;
		}

		if (tweb != null) {
			tweb.clearView();
			abslayout.removeView(tweb);
			tweb = null;
		}

		if (excelWeb != null) {
			excelWeb.clearView();
			abslayout.removeView(excelWeb);
			excelWeb = null;
		}

		stopText();

		stopFont();
	}

	private void toPlay(PlayItem pi, int cur, int tto) {
		if (vp != null) {
			vp.rel();
			abslayout.removeView(vp);
			vp = null;
		}
		if (cvbs != null) {
			cvbs.closeTV();
			abslayout.removeView(cvbs);
			cvbs = null;
		}
		if (mp != null) {
			mp.rel();
			mp = null;
		}
		if (ims != null) {
			ims.recy();
			abslayout.removeView(ims);
			ims = null;
		}

		if (tqweb != null) {
			tqweb.clearView();
			abslayout.removeView(tqweb);
			tqweb = null;
		}

		if (timeweb != null) {
			timeweb.clearView();
			abslayout.removeView(timeweb);
			timeweb = null;
		}

		if (hlweb != null) {
			hlweb.clearView();
			abslayout.removeView(hlweb);
			hlweb = null;
		}
		if (tweb != null) {
			tweb.clearView();
			abslayout.removeView(tweb);
			tweb = null;
		}
		if (showCamera != null) {
			showCamera.stop();
			abslayout.removeView(showCamera);
			showCamera = null;
		}
		if (excelWeb != null) {
			excelWeb.clearView();
			abslayout.removeView(excelWeb);
			excelWeb = null;
		}
		Constant.tqx = 0;
		Constant.tqy = 0;
		Constant.tqwidth = 0;
		Constant.tqheight = 0;
		Constant.tqdo = false;

		Constant.hlx = 0;
		Constant.hly = 0;
		Constant.hlwidth = 0;
		Constant.hlheight = 0;
		Constant.hldo = false;

		Constant.tx = 0;
		Constant.ty = 0;
		Constant.twidth = 0;
		Constant.theight = 0;
		Constant.tdo = false;

		Constant.vx = 0;
		Constant.vy = 0;
		Constant.vwidth = 0;
		Constant.vheight = 0;
		Constant.avl.clear();
		Constant.isHuDong = false;
		if (pi != null) {
			Constant.mfile = pi.item;
			Tool.saveConfig("item!" + Constant.mfile, Constant.config);
			String fname = pi.item.trim();

			String url;
			if (Constant.offshow) {
				url = "file://" + Constant.offDir + fname + "/index.html";
			} else {
				url = "file://" + Constant.fileDir + fname + "/index.html";
			}
			wv.loadUrl(url);
			String videos = pi.video;
			if (videos.length() > 0) {
				if (pi.videowidth > 0) {
					Constant.vx = pi.videox;
					Constant.vy = pi.videoy;
					Constant.vwidth = pi.videowidth;
					Constant.vheight = pi.videoheight;
					if (Constant.isZoom) {
						Constant.vx = (int) (Constant.vx * Constant.scaleZoomW);
						Constant.vy = (int) (Constant.vy * Constant.scaleZoomH);
						Constant.vwidth = (int) (Constant.vwidth * Constant.scaleZoomW);
						Constant.vheight = (int) (Constant.vheight * Constant.scaleZoomH);
					}
					String[] stss = videos.split("\\#");
					String sts;
					for (int i = 0; i < stss.length; i++) {
						sts = stss[i].trim();
						if (sts.length() > 0) {
							Constant.avl.add(sts);
						}
					}
					initVideo(cur, tto);
				}
			}
			initShowCamera(pi.scs);
			initAV(pi.avs);
			initimage(pi.imgs);

			if (pi.tqdo) {
				Constant.tqdo = pi.tqdo;
				Constant.tqx = pi.tqx;
				Constant.tqy = pi.tqy;
				Constant.tqwidth = pi.tqwidth;
				Constant.tqheight = pi.tqheight;
				if (Constant.isZoom) {
					Constant.tqx = (int) (Constant.tqx * Constant.scaleZoomH);
					Constant.tqy = (int) (Constant.tqy * Constant.scaleZoomW);
					Constant.tqwidth = (int) (Constant.tqwidth * Constant.scaleZoomW);
					Constant.tqheight = (int) (Constant.tqwidth * Constant.scaleZoomH);
				}
				inittq();
			}

			if (pi.hldo) {
				Constant.hldo = pi.hldo;
				Constant.hlx = pi.hlx;
				Constant.hly = pi.hly;
				Constant.hlwidth = pi.hlwidth;
				Constant.hlheight = pi.hlheight;
				if (Constant.isZoom) {
					Constant.hlx = (int) (Constant.hlx * Constant.scaleZoomH);
					Constant.hly = (int) (Constant.hly * Constant.scaleZoomW);
					Constant.hlwidth = (int) (Constant.hlwidth * Constant.scaleZoomW);
					Constant.hlheight = (int) (Constant.hlheight * Constant.scaleZoomH);
				}
				inithl();
			}

			if (pi.tdo) {
				Constant.tdo = pi.tdo;
				Constant.tx = pi.tx;
				Constant.ty = pi.ty;
				Constant.twidth = pi.twidth;
				Constant.theight = pi.theight;
				if (Constant.isZoom) {
					Constant.tx = (int) (Constant.tx * Constant.scaleZoomH);
					Constant.ty = (int) (Constant.ty * Constant.scaleZoomW);
					Constant.twidth = (int) (Constant.twidth * Constant.scaleZoomW);
					Constant.theight = (int) (Constant.theight * Constant.scaleZoomH);
				}
				initTime();
			}

			initText(pi.texts);
			initTime(pi.time, fname);
			initExcel(pi.excelStr, fname);

			if (sf != null) {
				sf.bringToFront();
			}
			String pn = pi.playName.trim();
			String pns[] = pn.split("\\@");
			String playName = "";
			String sceneName = "";
			String videoName = "";
			String imageName = "";
			try {
				playName = pns[0].trim();
			} catch (Exception e) {

			}
			if (pi.logStr != null) {
				String ls[] = pi.logStr.split("\\|");
				if (ls.length == 3) {
					sceneName = ls[0].trim();
					videoName = ls[2].trim();
					imageName = ls[1].trim();
				}
				if (Constant.isHuDong) {
					Constant.cblbStr = this.getString(R.string.log64);
				}

				Constant.li.writeLog("0000 " + R.string.log65
						+ Constant.cblbStr + " " + R.string.log66 + ": "
						+ sceneName + "  " + R.string.log34 + ":" + playName
						+ "   " + R.string.log35 + ":" + videoName + "   "
						+ R.string.log36 + ":" + imageName);
			}

		} else {

			SeeImageView mmi;
			for (int i = 0; i < Constant.imglist.size(); i++) {
				mmi = (SeeImageView) Constant.imglist.get(i);
				mmi.recy();
				abslayout.removeView(mmi);
				mmi = null;
			}
			Constant.imglist.clear();

			Constant.mfile = "";
			Tool.saveConfig("item!" + Constant.mfile, Constant.config);
			initText("");
			wv.setInitialScale(100);
			if (isLunarSetting()) {
				wv.loadUrl("file://" + Constant.novideoDir + "no.html");
			} else {
				wv.loadUrl("file://" + Constant.novideoDir + "noen.html");
			}
			Constant.li.writeLog("0000 " + R.string.log67);
		}

	}

	public boolean isLunarSetting() {
		String language = getLanguageEnv();

		if (language != null
				&& (language.trim().equals("zh-CN") || language.trim().equals(
						"zh-TW")))
			return true;
		else
			return false;
	}

	private String getLanguageEnv() {
		Locale l = Locale.getDefault();
		String language = l.getLanguage();
		String country = l.getCountry().toLowerCase();
		if ("zh".equals(language)) {
			if ("cn".equals(country)) {
				language = "zh-CN";
			} else if ("tw".equals(country)) {
				language = "zh-TW";
			}
		} else if ("pt".equals(language)) {
			if ("br".equals(country)) {
				language = "pt-BR";
			} else if ("pt".equals(country)) {
				language = "pt-PT";
			}
		}
		return language;
	}

	private void againPlay(int curplayt) {
		Constant.avlcur = 0;
		Constant.avlbian = false;
		if (Constant.playList.size() > 0) {
			if (!Constant.afvideo) {
				Constant.afvideo = true;
				if (Constant.VIDEOAGAIN > 0) {
					try {
						BufferedReader fr = new BufferedReader(new FileReader(
								Constant.config3));
						String sd = fr.readLine().trim();
						String[] sds = sd.split("\\_");
						if (sds.length == 6) {
							String mod = sds[0].trim();
							int curf = Integer.parseInt(sds[1].trim());
							int curf2 = Integer.parseInt(sds[2].trim());
							int curf3 = Integer.parseInt(sds[3].trim());
							int curf4 = Integer.parseInt(sds[4].trim());
							int curf5 = Integer.parseInt(sds[5].trim());
							if (mod.equals(Constant.modelcontent)) {
								Constant.curplay = curf;
								Constant.avlcur = curf3;
								if (curf < Constant.playList.size()) {
									PlayItem pl = Constant.playList
											.get(Constant.curplay);
									pl.curtime = curf2;
									toPlay(Constant.playList
											.get(Constant.curplay),
											curf4, curf5);
									return;
								}
							}
						}
					} catch (Exception e) {

					}
				}
			}

			toPlay(Constant.playList.get(curplayt), 0, 0);

		} else {
			toPlay(null, 0, 0);
		}
	}

	public void initPlay() {
		if (Constant.imgtime > 0) {
			Constant.playname = "";
			Constant.playtime = 0;
			Constant.avl.clear();
			stopPlay();
			String fnam;
			Constant.vx = 0;
			Constant.vy = 0;
			Constant.vwidth = Constant.width;
			Constant.vheight = Constant.height;
			ArrayList alvi = new ArrayList();
			ArrayList alim = new ArrayList();
			String vvstr = "";
			String mmstr = "";
			if (new File(Constant.offDir + Constant.modelcontent).exists()) {
				Constant.mfile = Constant.modelcontent;
				Tool.saveConfig("item!" + Constant.mfile, Constant.config);
				File[] fsfile = new File(Constant.offDir
						+ Constant.modelcontent).listFiles();
				for (int i = 0; i < fsfile.length; i++) {
					fnam = fsfile[i].getName();
					if (fnam.toLowerCase().endsWith(".jpg")
							|| fnam.toLowerCase().endsWith(".png")
							|| fnam.toLowerCase().endsWith(".gif")) {
						alim.add(fnam);
					} else {
						alvi.add(fnam);
					}
				}
				Collections.sort(alvi);
				for (int i = 0; i < alvi.size(); i++) {
					if (vvstr.isEmpty()) {
						vvstr = (String) alvi.get(i);
					} else {
						vvstr = vvstr + "|" + (String) alvi.get(i);
					}
				}
				Collections.sort(alim);
				for (int i = 0; i < alim.size(); i++) {
					if (mmstr.isEmpty()) {
						mmstr = (String) alim.get(i);
					} else {
						mmstr = mmstr + "|" + (String) alim.get(i);
					}
				}
				if (!vvstr.isEmpty()) {
					vvstr = "0_0@" + vvstr;
					Constant.avl.add(vvstr);
				}
				if (!mmstr.isEmpty()) {
					mmstr = "1_" + Constant.imgtime + "@" + mmstr;
					Constant.avl.add(mmstr);
				}
				initVideo(0, 0);
			}
			return;
		}
		String[] ss = Constant.modelcontent.split("\\|");
		String tems;
		long temlen = 0;
		for (int i = 0; i < ss.length; i++) {
			tems = ss[i].trim();
			if (tems.length() > 0) {
				if (Constant.offshow) {
					temlen += new File(Constant.offDir + tems + ".zip")
							.length();
				} else {
					temlen += new File(Constant.fileDir + tems + ".zip")
							.length();
				}
			}
		}
		if (Constant.curmodel.length() > 0) {
			if (Constant.curmodel.equals(Constant.modelcontent)) {
				if (temlen == Constant.totalfilelen) {
					return;
				}
			}
		}
		Constant.curmodel = Constant.modelcontent;
		Constant.totalfilelen = temlen;
		Constant.playList.clear();
		Constant.curplay = 0;
		Constant.tqagain = 0;
		Constant.allTime = 0;
		Constant.tqkey = "";
		String temsss;
		String temread = "";
		PlayItem pi;
		String[] msts;
		String tfil;
		File tf;
		for (int i = 0; i < ss.length; i++) {
			temsss = ss[i].trim();
			if (temsss.length() > 0) {
				pi = new PlayItem();
				pi.item = temsss;

				if (Constant.offshow) {
					tfil = Constant.offDir + temsss + "/" + "fbl.txt";
				} else {
					tfil = Constant.fileDir + temsss + "/" + "fbl.txt";
				}
				tf = new File(tfil);
				if (tf.exists()) {
					try {
						BufferedReader fad = new BufferedReader(new FileReader(
								tfil));
						temread = fad.readLine().trim();
						fad.close();
					} catch (Exception e) {

					}
					msts = temread.split("\\*");
					if (msts.length == 2) {
						int w = 0;
						int h = 0;
						try {
							w = Integer.parseInt(msts[0].trim());
						} catch (Exception e) {
						}
						try {
							h = Integer.parseInt(msts[1].trim());
						} catch (Exception e) {
						}
						if ((w == Constant.width && h == Constant.height)) {
							Constant.isZoom = false;
							Tool.saveConfig("scaleZoomW!0%scaleZoomH!0",
									Constant.config);
							wv.setInitialScale(100);
						} else if ((w != 0) && (h != 0)) {
							Constant.li.writeLog("0000 " + R.string.log68);
							try {
								Constant.scaleZoomW = (double) Constant.width
										/ (double) w;
							} catch (Exception e) {
							}
							try {
								Constant.scaleZoomH = (double) Constant.height
										/ (double) h;
							} catch (Exception e) {
							}
							Tool.saveConfig("scaleZoomW!" + Constant.scaleZoomW
									+ "%scaleZoomH!" + Constant.scaleZoomH,
									Constant.config);
							Constant.isZoom = true;
							wv.setInitialScale((int) ((Constant.scaleZoomW) * 100));
						}

					}
				}

				if (Constant.offshow) {
					tfil = Constant.offDir + temsss + "/" + "vsjm.txt";
				} else {
					tfil = Constant.fileDir + temsss + "/" + "vsjm.txt";
				}
				tf = new File(tfil);
				if (tf.exists()) {
					try {
						BufferedReader fad = new BufferedReader(new FileReader(
								tfil));
						temread = fad.readLine().trim();
						fad.close();
					} catch (Exception e) {

					}
					msts = temread.split("\\*");
					if (msts.length == 7) {
						int tx = 0;
						int ty = 0;
						int tw = 0;
						int th = 0;
						String tv = msts[2].trim();
						if (tv.length() > 0) {
							try {
								tx = Integer.parseInt(msts[3].trim());
								ty = Integer.parseInt(msts[4].trim());
								tw = Integer.parseInt(msts[5].trim());
								th = Integer.parseInt(msts[6].trim());
							} catch (Exception e) {

							}
							if (tw == 0 || th == 0) {
								tw = 1;
								th = 1;
							}
							pi.video = tv;
							pi.videox = tx;
							pi.videoy = ty;
							pi.videowidth = tw;
							pi.videoheight = th;
						}
						try {
							pi.totaltime = Integer.parseInt(msts[1].trim());
							Constant.allTime += pi.totaltime;
						} catch (Exception e) {

						}
					}
				}

				if (Constant.offshow) {
					tfil = Constant.offDir + temsss + "/" + "tq.txt";
				} else {
					tfil = Constant.fileDir + temsss + "/" + "tq.txt";
				}
				tf = new File(tfil);
				if (tf.exists()) {
					try {
						BufferedReader fad = new BufferedReader(new FileReader(
								tfil));
						temread = fad.readLine().trim();
						fad.close();
					} catch (Exception e) {

					}
					msts = temread.split("\\*");
					if (msts.length == 10) {
						Constant.tqkey = msts[0].trim();
						Constant.tqsize = msts[2].trim();
						Constant.tqcolor = msts[3].trim();
						Constant.tqimgsize = msts[8].trim();
						try {
							Constant.tqurl = Integer.parseInt(msts[9].trim());
						} catch (Exception e) {

						}
						try {
							Constant.tqagain = Integer.parseInt(msts[1].trim());
						} catch (Exception e) {

						}
						try {
							pi.tqx = Integer.parseInt(msts[4].trim());
						} catch (Exception e) {

						}
						try {
							pi.tqy = Integer.parseInt(msts[5].trim());
						} catch (Exception e) {

						}
						try {
							pi.tqwidth = Integer.parseInt(msts[6].trim());
						} catch (Exception e) {

						}
						try {
							pi.tqheight = Integer.parseInt(msts[7].trim());
						} catch (Exception e) {

						}

						if (pi.tqwidth == 0 || pi.tqheight == 0) {
							pi.tqwidth = 1;
							pi.tqheight = 1;
						}
						pi.tqdo = true;
					}
				}

				if (Constant.offshow) {
					tfil = Constant.offDir + temsss + "/" + "hl.txt";
				} else {
					tfil = Constant.fileDir + temsss + "/" + "hl.txt";
				}
				tf = new File(tfil);
				if (tf.exists()) {
					try {
						BufferedReader fad = new BufferedReader(new FileReader(
								tfil));
						temread = fad.readLine().trim();
						fad.close();
					} catch (Exception e) {

					}
					msts = temread.split("\\_");
					if (msts.length == 6) {
						Constant.hlkey = msts[0].trim();
						try {
							Constant.hltime = Integer.parseInt(msts[1].trim());
						} catch (Exception e) {

						}
						try {
							pi.hlx = Integer.parseInt(msts[2].trim());
						} catch (Exception e) {

						}
						try {
							pi.hly = Integer.parseInt(msts[3].trim());
						} catch (Exception e) {

						}
						try {
							pi.hlwidth = Integer.parseInt(msts[4].trim());
						} catch (Exception e) {

						}
						try {
							pi.hlheight = Integer.parseInt(msts[5].trim());
						} catch (Exception e) {

						}

						if (pi.hlwidth == 0 || pi.hlheight == 0) {
							pi.hlwidth = 1;
							pi.hlheight = 1;
						}
						pi.hldo = true;
					}
				}

				if (Constant.offshow) {
					tfil = Constant.offDir + temsss + "/" + "wtime.txt";
				} else {
					tfil = Constant.fileDir + temsss + "/" + "wtime.txt";
				}
				tf = new File(tfil);
				if (tf.exists()) {
					try {
						BufferedReader fad = new BufferedReader(new FileReader(
								tfil));
						temread = fad.readLine().trim();
						fad.close();
					} catch (Exception e) {

					}
					msts = temread.split("\\*");
					if (msts.length == 5) {
						Constant.tkey = msts[0].trim();

						try {
							pi.tx = Integer.parseInt(msts[1].trim());
						} catch (Exception e) {

						}
						try {
							pi.ty = Integer.parseInt(msts[2].trim());
						} catch (Exception e) {

						}
						try {
							pi.twidth = Integer.parseInt(msts[3].trim());
						} catch (Exception e) {

						}
						try {
							pi.theight = Integer.parseInt(msts[4].trim());
						} catch (Exception e) {

						}

						if (pi.twidth == 0 || pi.theight == 0) {
							pi.twidth = 1;
							pi.theight = 1;
						}
						pi.tdo = true;
					}
				}

				if (Constant.offshow) {
					tfil = Constant.offDir + temsss + "/" + "img.txt";
				} else {
					tfil = Constant.fileDir + temsss + "/" + "img.txt";
				}
				tf = new File(tfil);
				if (tf.exists()) {
					try {
						BufferedReader fad = new BufferedReader(new FileReader(
								tfil));
						pi.imgs = fad.readLine().trim();
						fad.close();
					} catch (Exception e) {

					}
				}

				if (Constant.offshow) {
					tfil = Constant.offDir + temsss + "/" + "text.txt";
				} else {
					tfil = Constant.fileDir + temsss + "/" + "text.txt";
				}
				tf = new File(tfil);
				if (tf.exists()) {
					try {
						BufferedReader fad = new BufferedReader(new FileReader(
								tfil));
						pi.texts = fad.readLine().trim();
						fad.close();
					} catch (Exception e) {

					}
				}

				if (Constant.offshow) {
					tfil = Constant.offDir + temsss + "/" + "time.txt";
				} else {
					tfil = Constant.fileDir + temsss + "/" + "time.txt";
				}
				tf = new File(tfil);
				if (tf.exists()) {
					try {
						BufferedReader fad = new BufferedReader(new FileReader(
								tfil));
						pi.time = fad.readLine().trim();
						fad.close();
					} catch (Exception e) {

					}
				}

				if (Constant.offshow) {
					tfil = Constant.offDir + temsss + "/" + "table.txt";
				} else {
					tfil = Constant.fileDir + temsss + "/" + "table.txt";
				}
				tf = new File(tfil);
				if (tf.exists()) {
					try {
						BufferedReader fad = new BufferedReader(new FileReader(
								tfil));
						pi.excelStr = fad.readLine().trim();
						fad.close();
					} catch (Exception e) {

					}
				}

				if (Constant.offshow) {
					tfil = Constant.offDir + temsss + "/" + "av.txt";
				} else {
					tfil = Constant.fileDir + temsss + "/" + "av.txt";
				}
				tf = new File(tfil);
				if (tf.exists()) {
					try {
						BufferedReader fad = new BufferedReader(new FileReader(
								tfil));
						pi.avs = fad.readLine().trim();
						fad.close();
					} catch (Exception e) {

					}
				}

				if (Constant.offshow) {
					tfil = Constant.offDir + temsss + "/" + "camera.txt";
				} else {
					tfil = Constant.fileDir + temsss + "/" + "camera.txt";
				}
				tf = new File(tfil);
				if (tf.exists()) {
					try {
						BufferedReader fad = new BufferedReader(new FileReader(
								tfil));
						pi.scs = fad.readLine().trim();
						fad.close();
					} catch (Exception e) {

					}
				}
				if (Constant.offshow) {
					tfil = Constant.offDir + temsss + "/" + "key.txt";
				} else {
					tfil = Constant.fileDir + temsss + "/" + "key.txt";
				}
				tf = new File(tfil);
				if (tf.exists()) {
					try {
						BufferedReader fad = new BufferedReader(new FileReader(
								tfil));
						pi.playName = fad.readLine().trim();
						fad.close();
					} catch (Exception e) {

					}
				}
				if (Constant.offshow) {
					tfil = Constant.offDir + temsss + "/" + "logsc.txt";
				} else {
					tfil = Constant.fileDir + temsss + "/" + "logsc.txt";
				}
				tf = new File(tfil);
				if (tf.exists()) {
					try {
						BufferedReader fad = new BufferedReader(new FileReader(
								tfil));
						pi.logStr = fad.readLine();
						fad.close();
					} catch (Exception e) {

					}
				}

				Constant.playname = pi.item;
				Constant.playtime = 0;
				Constant.playList.add(pi);
			}
		}
		againPlay(0);
	}

	public void agavin() {
		if (vp != null) {
			vp.rel();
			abslayout.removeView(vp);
			vp = null;
		}
		if (cvbs != null) {
			cvbs.closeTV();
			abslayout.removeView(cvbs);
			cvbs = null;
		}
		if (mp != null) {
			mp.rel();
			mp = null;
		}
		if (ims != null) {
			ims.recy();
			abslayout.removeView(ims);
			ims = null;
		}
		if (showCamera != null) {
			showCamera.stop();
			abslayout.removeView(showCamera);
			showCamera = null;
		}

		if (excelWeb != null) {
			excelWeb.clearView();
			abslayout.removeView(excelWeb);
			excelWeb = null;
		}
		Constant.avlcur++;
		if (Constant.avlcur >= Constant.avl.size()) {
			Constant.avlcur = 0;
		}
		String sds = (String) Constant.avl.get(Constant.avlcur);
		String[] ssds = sds.split("\\@");
		String sssds = "";
		int stype = 0;
		int slptime = 0;
		String str;
		if (ssds.length == 2) {
			try {
				sssds = ssds[0].trim();
			} catch (Exception e) {
				// TODO: handle exception
			}
			str = ssds[1].trim();

			String[] ssssds = sssds.split("\\_");
			if (ssssds.length == 2) {
				try {
					stype = Integer.parseInt(ssssds[0].trim());
				} catch (Exception e) {
					// TODO: handle exception
				}
				try {
					slptime = Integer.parseInt(ssssds[1].trim());
				} catch (Exception e) {
					// TODO: handle exception
				}

			}

			if (stype == 0) {

				vp = new VideoPlayer(this);
				vp.init(str, Constant.vwidth, Constant.vheight);

				if (Constant.scale == 1) {
					int vxs = 0;
					int vys = 0;
					if (Constant.vpX != 0) {
						vxs = (Constant.vwidth - Constant.vpX) / 2;
						vys = Constant.vy;
					}
					if (Constant.vpY != 0) {
						vys = (Constant.vheight - Constant.vpY) / 2;
						vxs = Constant.vx;
					}
					abslayout.addView(vp, new AbsoluteLayout.LayoutParams(
							Constant.vwidth, Constant.vheight, vxs, vys));
					vys = 0;
					vxs = 0;
					Constant.vpX = 0;
					Constant.vpY = 0;
				} else {
					abslayout.addView(vp, new AbsoluteLayout.LayoutParams(
							Constant.vwidth, Constant.vheight, Constant.vx,
							Constant.vy));
				}

			} else if (stype == 1) {
				ims = new ImageSee(this);
				ims.init(str, slptime);
				ims.setOnClickListener(new OnClickListener() {

					public void onClick(View arg0) {
						showPopupWindow(ims.getBitmap());
						ims.recy();
					}
				});
				abslayout.addView(ims, new AbsoluteLayout.LayoutParams(
						Constant.vwidth, Constant.vheight, Constant.vx,
						Constant.vy));
			} else if (stype == 2) {
				mp = new Mp3Player();
				mp.init(str);
			}
			// else if (stype == 3) {
			// cvbs = new cvbs(this, Constant.vwidth, Constant.vheight);
			// abslayout.addView(cvbs, new AbsoluteLayout.LayoutParams(
			// Constant.vwidth, Constant.vheight, Constant.vx,
			// Constant.vy));
			// }
		}
	}

	public void initimage(String str) {
		SeeImageView mmi;
		for (int i = 0; i < Constant.imglist.size(); i++) {
			mmi = (SeeImageView) Constant.imglist.get(i);
			mmi.recy();
			abslayout.removeView(mmi);
			mmi = null;
		}

		Constant.imglist.clear();
		if (str.length() > 0) {
			Constant.ix = 0;
			Constant.iy = 0;
			Constant.iw = 1;
			Constant.ih = 1;
			int time = 5;
			String link = "0";
			String[] strs = str.split("\\*");
			String instr;
			String[] ininstr;
			for (int i = 0; i < strs.length; i++) {
				instr = strs[i];
				ininstr = instr.split("\\~");
				if (ininstr.length == 8) {
					try {
						Constant.ix = Integer.parseInt(ininstr[0].trim());
						Constant.iy = Integer.parseInt(ininstr[1].trim());
						Constant.iw = Integer.parseInt(ininstr[2].trim());
						Constant.ih = Integer.parseInt(ininstr[3].trim());
						if (Constant.isZoom) {
							Constant.ix = (int) (Constant.ix * Constant.scaleZoomW);
							Constant.iy = (int) (Constant.iy * Constant.scaleZoomH);
							Constant.iw = (int) (Constant.iw * Constant.scaleZoomW);
							Constant.ih = (int) (Constant.ih * Constant.scaleZoomH);
						}
						time = Integer.parseInt(ininstr[4].trim());
						link = ininstr[6].trim();
						Constant.effectsId = Integer
								.parseInt(ininstr[7].trim());
					} catch (Exception e) {
						// TODO: handle exception
					}
					if (Constant.iw <= 0) {
						Constant.iw = 1;
					}
					if (Constant.ih <= 0) {
						Constant.ih = 1;
					}

					final SeeImageView miv = new SeeImageView(this);
					if (link.length() > 5) {
						Constant.isHuDong = true;
						miv.setOnClickListener(new OnClickListener() {
							public void onClick(View v) {
								Constant.curmodel = "";
								Constant.tqagain = 0;
								Constant.tqkey = "";
								Constant.tqdo = false;

								Constant.hltime = 0;
								Constant.hlkey = "";
								Constant.hldo = false;

								Constant.tkey = "";
								Constant.tdo = false;

								if (!Constant.itemgo) {
									Constant.chu = true;
									Constant.chutime = Constant.urltime;
								} else {
									Constant.itemgo = false;
								}
								String md = ((SeeImageView) v).getlink().trim();
								if (md.length() > 5) {
									Constant.chutime = Constant.urltime;
									File tdsdf = null;
									String temread = "";
									String[] msts;

									if (Constant.offshow) {
										tdsdf = new File(Constant.offDir + md
												+ File.separator + "vsjm.txt");
									} else {
										tdsdf = new File(Constant.fileDir + md
												+ File.separator + "vsjm.txt");
									}

									String tv = "";
									if (tdsdf.exists()) {
										try {
											BufferedReader fad = new BufferedReader(
													new FileReader(tdsdf));
											temread = fad.readLine().trim();
											fad.close();
										} catch (Exception e) {

										}
										msts = temread.split("\\*");
										if (msts.length == 7) {
											tv = msts[2].trim();
											if (tv.length() > 0) {
												try {
													Constant.vx = Integer
															.parseInt(msts[3]
																	.trim());
													Constant.vy = Integer
															.parseInt(msts[4]
																	.trim());
													Constant.vwidth = Integer
															.parseInt(msts[5]
																	.trim());
													Constant.vheight = Integer
															.parseInt(msts[6]
																	.trim());
													if (Constant.vwidth == 0
															|| Constant.vheight == 0) {
														Constant.vwidth = 1;
														Constant.vheight = 1;
													}
													if (Constant.isZoom) {
														Constant.vx = (int) (Constant.vx * Constant.scaleZoomW);
														Constant.vy = (int) (Constant.vy * Constant.scaleZoomH);
														Constant.vwidth = (int) (Constant.vwidth * Constant.scaleZoomW);
														Constant.vheight = (int) (Constant.vheight * Constant.scaleZoomH);
													}
												} catch (Exception e) {

												}
											}
										}
									}

									if (Constant.offshow) {
										tdsdf = new File(Constant.offDir + md
												+ File.separator + "tq.txt");
									} else {
										tdsdf = new File(Constant.fileDir + md
												+ File.separator + "tq.txt");
									}
									if (tdsdf.exists()) {
										try {
											BufferedReader fad = new BufferedReader(
													new FileReader(tdsdf));
											temread = fad.readLine().trim();
											fad.close();
										} catch (Exception e) {

										}
										msts = temread.split("\\*");
										if (msts.length == 10) {
											Constant.tqkey = msts[0].trim();
											Constant.tqsize = msts[2].trim();
											Constant.tqcolor = msts[3].trim();
											Constant.tqimgsize = msts[8].trim();
											try {
												Constant.tqurl = Integer
														.parseInt(msts[9]
																.trim());
											} catch (Exception e) {

											}
											try {
												Constant.tqagain = Integer
														.parseInt(msts[1]
																.trim());
											} catch (Exception e) {

											}
											try {
												Constant.tqx = Integer
														.parseInt(msts[4]
																.trim());
											} catch (Exception e) {

											}
											try {
												Constant.tqy = Integer
														.parseInt(msts[5]
																.trim());
											} catch (Exception e) {

											}
											try {
												Constant.tqwidth = Integer
														.parseInt(msts[6]
																.trim());
											} catch (Exception e) {

											}
											try {
												Constant.tqheight = Integer
														.parseInt(msts[7]
																.trim());
											} catch (Exception e) {

											}
											if (Constant.tqwidth == 0
													|| Constant.tqheight == 0) {
												Constant.tqwidth = 1;
												Constant.tqheight = 1;
											}
											Constant.tqdo = true;
										}
									}

									if (Constant.offshow) {
										tdsdf = new File(Constant.offDir + md
												+ File.separator + "hl.txt");
									} else {
										tdsdf = new File(Constant.fileDir + md
												+ File.separator + "hl.txt");
									}
									if (tdsdf.exists()) {
										try {
											BufferedReader fad = new BufferedReader(
													new FileReader(tdsdf));
											temread = fad.readLine().trim();
											fad.close();
										} catch (Exception e) {

										}
										msts = temread.split("\\_");
										if (msts.length == 6) {
											Constant.hlkey = msts[0].trim();
											try {
												Constant.hltime = Integer
														.parseInt(msts[1]
																.trim());
											} catch (Exception e) {

											}
											try {
												Constant.hlx = Integer
														.parseInt(msts[2]
																.trim());
											} catch (Exception e) {

											}
											try {
												Constant.hly = Integer
														.parseInt(msts[3]
																.trim());
											} catch (Exception e) {

											}
											try {
												Constant.hlwidth = Integer
														.parseInt(msts[4]
																.trim());
											} catch (Exception e) {

											}
											try {
												Constant.hlheight = Integer
														.parseInt(msts[5]
																.trim());
											} catch (Exception e) {

											}
											if (Constant.hlwidth == 0
													|| Constant.hlheight == 0) {
												Constant.hlwidth = 1;
												Constant.hlheight = 1;
											}
											Constant.hldo = true;
										}
									}

									if (Constant.offshow) {
										tdsdf = new File(Constant.offDir + md
												+ File.separator + "wtime.txt");
									} else {
										tdsdf = new File(Constant.fileDir + md
												+ File.separator + "wtime.txt");
									}
									if (tdsdf.exists()) {
										try {
											BufferedReader fad = new BufferedReader(
													new FileReader(tdsdf));
											temread = fad.readLine().trim();
											fad.close();
										} catch (Exception e) {

										}
										msts = temread.split("\\*");
										if (msts.length == 5) {
											Constant.tkey = msts[0].trim();

											try {
												Constant.tx = Integer
														.parseInt(msts[1]
																.trim());
											} catch (Exception e) {

											}
											try {
												Constant.ty = Integer
														.parseInt(msts[2]
																.trim());
											} catch (Exception e) {

											}
											try {
												Constant.twidth = Integer
														.parseInt(msts[3]
																.trim());
											} catch (Exception e) {

											}
											try {
												Constant.theight = Integer
														.parseInt(msts[4]
																.trim());
											} catch (Exception e) {

											}
											if (Constant.twidth == 0
													|| Constant.theight == 0) {
												Constant.twidth = 1;
												Constant.theight = 1;
											}
											Constant.tdo = true;
										}
									}

									String imgs = "";
									String tfil;
									if (Constant.offshow) {
										tfil = Constant.offDir + md + "/"
												+ "img.txt";
									} else {
										tfil = Constant.fileDir + md + "/"
												+ "img.txt";
									}
									tdsdf = new File(tfil);
									if (tdsdf.exists()) {
										try {
											BufferedReader fad = new BufferedReader(
													new FileReader(tfil));
											imgs = fad.readLine().trim();
											fad.close();
										} catch (Exception e) {

										}
									}

									String texts = "";
									if (Constant.offshow) {
										tfil = Constant.offDir + md + "/"
												+ "text.txt";
									} else {
										tfil = Constant.fileDir + md + "/"
												+ "text.txt";
									}
									tdsdf = new File(tfil);
									if (tdsdf.exists()) {
										try {
											BufferedReader fad = new BufferedReader(
													new FileReader(tfil));
											texts = fad.readLine().trim();
											fad.close();
										} catch (Exception e) {

										}
									}
									String times = "";
									if (Constant.offshow) {
										tfil = Constant.offDir + md + "/"
												+ "time.txt";
									} else {
										tfil = Constant.fileDir + md + "/"
												+ "time.txt";
									}
									tdsdf = new File(tfil);
									if (tdsdf.exists()) {
										try {
											BufferedReader fad = new BufferedReader(
													new FileReader(tfil));
											times = fad.readLine().trim();
											fad.close();
										} catch (Exception e) {

										}
									}

									String avs = "";
									if (Constant.offshow) {
										tfil = Constant.offDir + md + "/"
												+ "av.txt";
									} else {
										tfil = Constant.fileDir + md + "/"
												+ "av.txt";
									}
									tdsdf = new File(tfil);
									if (tdsdf.exists()) {
										try {
											BufferedReader fad = new BufferedReader(
													new FileReader(tfil));
											avs = fad.readLine().trim();
											fad.close();
										} catch (Exception e) {

										}
									}

									String scs = "";
									if (Constant.offshow) {
										tfil = Constant.offDir + md + "/"
												+ "camera.txt";
									} else {
										tfil = Constant.fileDir + md + "/"
												+ "camera.txt";
									}
									tdsdf = new File(tfil);
									if (tdsdf.exists()) {
										try {
											BufferedReader fad = new BufferedReader(
													new FileReader(tfil));
											scs = fad.readLine().trim();
											fad.close();
										} catch (Exception e) {

										}
									}

									String exc = "";
									if (Constant.offshow) {
										tfil = Constant.offDir + md + "/"
												+ "table.txt";
									} else {
										tfil = Constant.fileDir + md + "/"
												+ "table.txt";
									}
									tdsdf = new File(tfil);
									if (tdsdf.exists()) {
										try {
											BufferedReader fad = new BufferedReader(
													new FileReader(tfil));
											exc = fad.readLine().trim();
											fad.close();
										} catch (Exception e) {

										}
									}

									String logsc = "";
									if (Constant.offshow) {
										tfil = Constant.offDir + md + "/"
												+ "logsc.txt";
									} else {
										tfil = Constant.fileDir + md + "/"
												+ "logsc.txt";
									}
									tdsdf = new File(tfil);
									if (tdsdf.exists()) {
										try {
											BufferedReader fad = new BufferedReader(
													new FileReader(tfil));
											logsc = fad.readLine();
											fad.close();
										} catch (Exception e) {

										}
									}

									String key = "";
									if (Constant.offshow) {
										tfil = Constant.offDir + md + "/"
												+ "key.txt";
									} else {
										tfil = Constant.fileDir + md + "/"
												+ "key.txt";
									}
									tdsdf = new File(tfil);
									if (tdsdf.exists()) {
										try {
											BufferedReader fad = new BufferedReader(
													new FileReader(tfil));
											key = fad.readLine().trim();
											fad.close();
										} catch (Exception e) {

										}
									}

									Constant.mfile = md;
									Tool.saveConfig("item!" + Constant.mfile,
											Constant.config);
									String dir = "";
									if (Constant.offshow) {
										dir = Constant.offDir;
									} else {
										dir = Constant.fileDir;
									}

									toPlay2(dir + md + File.separator
											+ "index.html", tv, imgs, texts,
											times, avs, scs, exc, logsc, key);
								}
							}
						});
					} else {
						Constant.isHuDong = false;
						miv.setOnTouchListener(new OnTouchListener() {

							public boolean onTouch(View arg0, MotionEvent event) {

								if (event.getAction() == MotionEvent.ACTION_DOWN) {
									Constant.downX = event.getX();
									float sidex = 0;
									float sidey = 0;
									sidex = event.getX();
									sidey = event.getY();

									if (sidex < (Constant.iw / 5)
											&& sidey < (Constant.ih / 5)) {
										Constant.outsideCount1 = true;
									} else if (sidex > (Constant.iw - (Constant.iw / 5))
											&& sidey < (Constant.ih / 10)) {
										Constant.outsideCount2 = true;
									} else if (sidex > (Constant.iw - (Constant.iw / 5))
											&& sidey > (Constant.ih - (Constant.ih / 5))) {
										Constant.outsideCount3 = true;
									} else if (sidex < (Constant.iw / 10)
											&& sidey > (Constant.ih - (Constant.ih / 5))) {
										Constant.outsideCount4 = true;
									} else {
										Constant.outsideCount1 = false;
										Constant.outsideCount2 = false;
										Constant.outsideCount3 = false;
										Constant.outsideCount4 = false;
									}
									return true;
								}
								if (event.getAction() == MotionEvent.ACTION_MOVE) {
									Constant.moveX = event.getX();

									return true;
								}
								if (event.getAction() == MotionEvent.ACTION_UP) {
									float distance = 0;
									distance = Constant.downX - Constant.moveX;
									if (distance > 100 && Constant.moveX != 0) {
										miv.gotime = 0;
										miv.cur++;
										if (miv.cur >= miv.ttal) {
											miv.cur = 0;
										}
										miv.tos();
										Constant.downX = 0;
										Constant.moveX = 0;

									} else if (((distance < 100 && distance > 10) || (distance < 0 && distance > -1000))
											&& Constant.moveX != 0) {
										miv.gotime = 0;
										miv.cur--;
										if (miv.cur < 0) {
											miv.cur = (miv.ttal - 1);
										}
										miv.tos();
										Constant.downX = 0;
										Constant.moveX = 0;
									} else if (Constant.moveX == 0
											|| (distance <= 10 && distance >= 0)) {

										if (Constant.interval) {
											// showPopupWindow(miv.getBitmap());
											// miv.recy();
											Constant.downX = 0;
											Constant.moveX = 0;
											Constant.interval = false;
										}
									}
									return true;
								}

								return false;
							}
						});

					}

					miv.init(ininstr[5].trim(), time, link, Constant.effectsId,Constant.iw,Constant.ih);
					abslayout
							.addView(miv, new AbsoluteLayout.LayoutParams(
									Constant.iw, Constant.ih, Constant.ix,
									Constant.iy));
					Constant.imglist.add(miv);
				}
			}
		}
	}

	public void initVideo(int cur, int tto) {
		if (Constant.dpIsStop) {
			cur = Constant.VpGoONCur;
			tto = Constant.VpGoONTime;
		}
		if (Constant.avl.size() > 0) {
			if (Constant.avlcur >= Constant.avl.size()) {
				Constant.avlcur = 0;
			}
			String sds = (String) Constant.avl.get(Constant.avlcur);
			String[] ssds = sds.split("\\@");
			String sssds = "";
			int stype = 0;
			int slptime = 0;
			String str;
			if (ssds.length == 2) {
				try {
					sssds = ssds[0].trim();
				} catch (Exception e) {
					// TODO: handle exception
				}
				str = ssds[1].trim();

				String[] ssssds = sssds.split("\\_");
				if (ssssds.length == 2) {
					try {
						stype = Integer.parseInt(ssssds[0].trim());
					} catch (Exception e) {
						// TODO: handle exception
					}
					try {
						slptime = Integer.parseInt(ssssds[1].trim());
					} catch (Exception e) {
						// TODO: handle exception
					}

				}

				if (stype == 0) {
					vp = new VideoPlayer(this);
					vp.setSeek(cur, tto);
					vp.init(str, Constant.vwidth, Constant.vheight);

					if (Constant.scale == 1) {
						int vxs = 0;
						int vys = 0;
						if (Constant.vpX != 0) {
							vxs = (Constant.vwidth - Constant.vpX) / 2;
							vys = Constant.vy;
						}
						if (Constant.vpY != 0) {
							vys = (Constant.vheight - Constant.vpY) / 2;
							vxs = Constant.vx;
						}
						abslayout.addView(vp, new AbsoluteLayout.LayoutParams(
								Constant.vwidth, Constant.vheight, vxs, vys));
						vys = 0;
						vxs = 0;
						Constant.vpX = 0;
						Constant.vpY = 0;
					} else {

						abslayout.addView(vp, new AbsoluteLayout.LayoutParams(
								Constant.vwidth, Constant.vheight, Constant.vx,
								Constant.vy));
					}
				} else if (stype == 1) {
					ims = new ImageSee(this);
					ims.init(str, slptime);
					ims.setOnClickListener(new OnClickListener() {

						public void onClick(View arg0) {
							showPopupWindow(ims.getBitmap());
							ims.recy();
						}
					});

					abslayout.addView(ims, new AbsoluteLayout.LayoutParams(
							Constant.vwidth, Constant.vheight, Constant.vx,
							Constant.vy));
				} else if (stype == 2) {
					mp = new Mp3Player();
					mp.init(str);
				}
				// else if (stype == 3) {
				// cvbs = new cvbs(this, Constant.vwidth, Constant.vheight);
				// abslayout.addView(cvbs, new AbsoluteLayout.LayoutParams(
				// Constant.vwidth, Constant.vheight, Constant.vx,
				// Constant.vy));
				// }
			}
		}
	}

	public void stopText() {
		String pakageName = "com.vshow.textfont";
		ActivityManager activityMgr = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		try {
			activityMgr.forceStopPackage(pakageName);
		} catch (Exception e) {
		}

	}

	public void stopFont() {
		String pakageName = "com.vshow.scrollfont";
		ActivityManager activityMgr = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		try {
			activityMgr.forceStopPackage(pakageName);
		} catch (Exception e) {
		}
	}

	public void initService() {
		if (!isRunning(Vsplayer.this, "com.example.vsplayerservice")) {
			try {
				ComponentName componetName = new ComponentName(
						"com.example.vsplayerservice",
						"com.example.vsplayerservice.MainActivity");
				Intent intent = new Intent();
				intent.setComponent(componetName);
				startActivity(intent);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	private void initText(String text) {
		stopText();
		String rss = Constant.fileDir + Constant.mfile + "/" + "rss.txt";
		File file = new File(rss);
		if (text.length() > 0 || file.length() > 0) {
			try {
				ComponentName componetName = new ComponentName(
						"com.vshow.textfont",
						"com.vshow.textfont.TextFontActivity");
				Intent intent = new Intent();
				intent.setComponent(componetName);
				startActivity(intent);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	private void initFont() {
		stopFont();
		try {
			ComponentName componetName = new ComponentName(
					"com.vshow.scrollfont",
					"com.vshow.scrollfont.ScrollFontActivity");
			Intent intent = new Intent();
			intent.setComponent(componetName);
			startActivity(intent);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void installApk() {
		String[] upd = Tool.loadConfig(new String[] { "update" },
				Constant.config2);
		if (upd != null) {
			if (upd.length > 0) {
				try {
					int upint = Integer.parseInt(upd[0].trim());
					if (upint == 0) {
						if ((checkApkExist("com.vshow.textfont")
								&& checkApkExist("com.vshow.scrollfont") && checkApkExist("com.example.vsplayerservice"))) {
							return;
						}
					} else {
						Constant.updateapk = true;
						Tool.saveConfig("update!0", Constant.config2);
					}
				} catch (Exception e) {
					if (checkApkExist("com.vshow.textfont")
							&& checkApkExist("com.vshow.scrollfont")
							&& checkApkExist("com.example.vsplayerservice")) {
						return;
					}
				}
			}
		}
		try {
			InputStream is = getAssets().open("textfont.apk");
			FileOutputStream fos = new FileOutputStream(Constant.tempDir
					+ "textfont.apk");

			byte[] buffer = new byte[1024];
			int readLen = 0;
			while ((readLen = is.read(buffer, 0, 1024)) >= 0) {
				fos.write(buffer, 0, readLen);
			}
			is = getAssets().open("scrollfont.apk");
			fos = new FileOutputStream(Constant.tempDir + "scrollfont.apk");
			byte[] buffer2 = new byte[1024];
			int readLen2 = 0;
			while ((readLen2 = is.read(buffer2, 0, 1024)) >= 0) {
				fos.write(buffer2, 0, readLen2);
			}

			is = getAssets().open("vsplayerservice.apk");
			fos = new FileOutputStream(Constant.tempDir + "vsplayerservice.apk");
			byte[] buffer3 = new byte[1024];
			int readLen3 = 0;
			while ((readLen3 = is.read(buffer3, 0, 1024)) >= 0) {
				fos.write(buffer3, 0, readLen3);
			}
			fos.close();
			is.close();
			Tool.install(Constant.tempDir + "textfont.apk");
			Tool.install(Constant.tempDir + "scrollfont.apk");
			Tool.install(Constant.tempDir + "vsplayerservice.apk");
		} catch (Exception e) {

		}
	}

	public boolean checkApkExist(String packageName) {
		if (packageName == null || "".equals(packageName))
			return false;
		try {
			ApplicationInfo info = getPackageManager().getApplicationInfo(
					packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
			return true;
		} catch (Exception e) {
			return false;

		}
	}

	public void showPopupWindow(Bitmap bm) {
		if (matrix != null) {
			matrix = null;
		}
		if (savedMatrix != null) {
			savedMatrix = null;
		}
		if (start != null) {
			start = null;
		}
		if (mid != null) {
			mid = null;
		}
		if (mPoupuWindow != null) {
			mPoupuWindow = null;
		}

		Constant.isPop = true;
		matrix = new Matrix();
		savedMatrix = new Matrix();
		start = new PointF();
		mid = new PointF();
		final DisplayMetrics dm = new DisplayMetrics();
		iView = new ImageView(this);
		AbsoluteLayout absoluteLayout = new AbsoluteLayout(this);
		mPoupuWindow = new PopupWindow(absoluteLayout,
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, true);
		mPoupuWindow.showAtLocation(absoluteLayout, Gravity.CENTER, 0, 0);
		try {
			getWindowManager().getDefaultDisplay().getMetrics(dm);
			newBm = Bitmap.createScaledBitmap(bm, dm.widthPixels,
					dm.heightPixels, true);
			iView.setImageBitmap(newBm);
			iView.setScaleType(ScaleType.MATRIX);
		} catch (Exception e) {
		}

		absoluteLayout.addView(iView, new AbsoluteLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 0, 0));

		iView.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction() & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_DOWN:
					Constant.popTime = 30;
					Constant.chick = true;
					savedMatrix.set(matrix);
					start.set(event.getX(), event.getY());
					break;

				case MotionEvent.ACTION_UP:
					if (Constant.chick) {
						Constant.isPop = false;
						try {
							if (newBm != null) {
								try {
									if (!newBm.isRecycled()) {
										newBm.recycle();
									}

								} catch (Exception e) {
								}
							}
							mPoupuWindow.dismiss();
							Constant.interval = true;
						} catch (Exception e) {
						}

					}
					break;
				case MotionEvent.ACTION_POINTER_UP:
					Constant.mode = Constant.NONE;
					break;
				case MotionEvent.ACTION_POINTER_DOWN:
					Constant.chick = false;
					Constant.oldDist = spacing(event);
					if (Constant.oldDist > 10f) {
						savedMatrix.set(matrix);
						midPoint(mid, event);
						Constant.mode = Constant.ZOOM;
					}
					break;
				case MotionEvent.ACTION_MOVE:

					if (Constant.mode == Constant.ZOOM) {
						Constant.chick = false;
						float newDist = spacing(event);
						if (newDist > 10f) {
							matrix.set(savedMatrix);
							float scale = newDist / Constant.oldDist;
							matrix.postScale(scale, scale, mid.x, mid.y);
						}
					}
					break;
				}

				try {
					iView.setImageMatrix(matrix);
				} catch (Exception e) {
				}

				return true;
			}
		});
		mPoupuWindow.setOnDismissListener(new OnDismissListener() {
			public void onDismiss() {
				Constant.isPop = false;
				try {
					if (newBm != null) {
						try {
							if (!newBm.isRecycled()) {
								newBm.recycle();
							}

						} catch (Exception e) {
						}
					}
					mPoupuWindow.dismiss();
					Constant.interval = true;
				} catch (Exception e) {
				}

			}
		});
	}

	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	private void initConfigFile() {
		Constant.config = Constant.sdcardDir + "/config.ini";
		Constant.config2 = Constant.sdcardDir + "/config2.ini";
		Constant.config3 = Constant.sdcardDir + "/config3.ini";
		Constant.advance = Constant.sdcardDir + "/advance.ini";

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
			}
		}
		if (!new File(Constant.advance).exists()) {
			try {
				new File(Constant.advance).createNewFile();
			} catch (Exception e) {
			}
		}
	}

	public void initExcel(String excelstr, String play) {
		if (play == null || excelstr == null || ("").equals(excelstr)
				|| ("").equals(play)) {
			return;
		}
		String excel[];
		int x = 0;
		int y = 0;
		int w = 0;
		int h = 0;
		excel = excelstr.trim().split("\\*");
		if (excel.length == 4) {
			try {
				x = Integer.parseInt(excel[0].trim());
			} catch (Exception e) {
			}
			try {
				y = Integer.parseInt(excel[1].trim());
			} catch (Exception e) {
			}
			try {
				w = Integer.parseInt(excel[2].trim());
			} catch (Exception e) {
			}
			try {
				h = Integer.parseInt(excel[3].trim());
			} catch (Exception e) {
			}

			if (w == 0 || h == 0) {
				w = 1;
				h = 1;
			}
			if (Constant.isZoom) {
				x = (int) (x * Constant.scaleZoomW);
				y = (int) (y * Constant.scaleZoomH);
				w = (int) (w * Constant.scaleZoomW);
				h = (int) (h * Constant.scaleZoomH);
			}

			excelWeb = new WebView(this);
			excelWeb.getSettings().setDefaultTextEncodingName("utf-8");
			excelWeb.setOnKeyListener(this);
			excelWeb.setOnTouchListener(this);
			excelWeb.getSettings().setJavaScriptEnabled(true);
			excelWeb.getSettings().setAllowFileAccess(true);
			excelWeb.getSettings().setPluginsEnabled(true);
			excelWeb.getSettings().setPluginState(PluginState.ON);
			excelWeb.getSettings().setBuiltInZoomControls(false);
			excelWeb.getSettings().setSupportZoom(true);
			excelWeb.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
			excelWeb.setBackgroundColor(0);
			// if (Constant.isZoom) {
			// excelWeb.setInitialScale((int) ((Constant.scaleZoomW) * 100));
			// }else {
			// excelWeb.setInitialScale(100);
			// }
			String dir = "";
			if (Constant.offshow) {
				dir = Constant.offDir;
			} else {
				dir = Constant.fileDir;
			}
			excelWeb.loadUrl("file://" + dir + play + "/table.html");
			abslayout.addView(excelWeb, new AbsoluteLayout.LayoutParams(w, h,
					x, y));
		}
	}

	public void initTime(String time, String play) {
		if (play == null || time == null || ("").equals(time)
				|| ("").equals(play)) {
			return;
		}
		String times[];
		int x = 0;
		int y = 0;
		int w = 0;
		int h = 0;
		times = time.trim().split("\\*");
		if (times.length == 4) {
			try {
				x = Integer.parseInt(times[0].trim());
			} catch (Exception e) {
			}
			try {
				y = Integer.parseInt(times[1].trim());
			} catch (Exception e) {
			}
			try {
				w = Integer.parseInt(times[2].trim());
			} catch (Exception e) {
			}
			try {
				h = Integer.parseInt(times[3].trim());
			} catch (Exception e) {
			}

			if (Constant.isZoom) {
				x = (int) (x * Constant.scaleZoomW);
				y = (int) (y * Constant.scaleZoomH);
				w = (int) (w * Constant.scaleZoomW);
				h = (int) (h * Constant.scaleZoomH);
			}

			if (w == 0 || h == 0) {
				w = 1;
				h = 1;
			}
			timeweb = new WebView(this);
			timeweb.getSettings().setDefaultTextEncodingName("utf-8");
			timeweb.setOnKeyListener(this);
			timeweb.setOnTouchListener(this);
			timeweb.getSettings().setJavaScriptEnabled(true);
			timeweb.getSettings().setAllowFileAccess(true);
			timeweb.getSettings().setPluginsEnabled(true);
			timeweb.getSettings().setPluginState(PluginState.ON);
			timeweb.getSettings().setBuiltInZoomControls(false);
			timeweb.getSettings().setSupportZoom(true);
			timeweb.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
			timeweb.setBackgroundColor(0);
			String dir = "";
			if (Constant.offshow) {
				dir = Constant.offDir;
			} else {
				dir = Constant.fileDir;
			}
			timeweb.loadUrl("file://" + dir + play + "/time.html");
			abslayout.addView(timeweb, new AbsoluteLayout.LayoutParams(w, h, x,
					y));
		}
	}

	public int count(String str) {
		int count = 0;
		for (int i = 0; i < str.length(); i++) {
			if ("0".equals(str.substring(i, i + 1))) {
				count++;
			}
		}
		return count;
	}

	// 2014-5-19 2014-5-01
	public int compareStartTimeDay(String nowTimeDay, String compareTimeDay) {
		// 0超过 1没超过 2是等于
		int isExceed = 0;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date d1 = df.parse(nowTimeDay);
			Date d2 = df.parse(compareTimeDay);
			if (d1.getTime() < d2.getTime()) {
				isExceed = 1;
			} else if (d1.getTime() == d2.getTime()) {
				isExceed = 2;
			} else {
				isExceed = 0;
			}
		} catch (Exception e) {
		}
		return isExceed;
	}

	public boolean compareTime(String nowTime, String compareTime) {
		boolean isExceed = false;
		DateFormat df = new SimpleDateFormat("HH:mm:ss");
		try {
			Date d1 = df.parse(nowTime);
			Date d2 = df.parse(compareTime);
			if (d1.getTime() >= d2.getTime()) {
				isExceed = true;
			} else {
				isExceed = false;
			}
		} catch (Exception e) {
		}
		return isExceed;

	}

	public int compareEndTimeDay(String nowTimeDay, String compareTimeDay) {
		// 0超过 1没超过 2是等于
		int isExceed = 0;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date d1 = df.parse(nowTimeDay);
			Date d2 = df.parse(compareTimeDay);
			if (d1.getTime() < d2.getTime()) {
				isExceed = 1;
			} else if (d1.getTime() == d2.getTime()) {
				isExceed = 2;
			} else {
				isExceed = 0;
			}
		} catch (Exception e) {
		}
		return isExceed;

	}

	public long getEndTime(String nowTimeDay, String compareTimeDay) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date d1 = null;
		Date d2 = null;
		try {
			d1 = df.parse(nowTimeDay);
			d2 = df.parse(compareTimeDay);
		} catch (Exception e) {
		}
		return d2.getTime() - d1.getTime();
	}

	// 17:15:20
	// "HH-mm-ss"
	public int syncTime(String times) {
		int second = 0;
		String[] strs = times.split("\\:");
		if (strs.length == 3) {
			try {
				second = (Integer.parseInt(strs[0].trim()) * 3600)
						+ (Integer.parseInt(strs[1].trim()) * 60)
						+ (Integer.parseInt(strs[2].trim()));
			} catch (Exception e) {
			}

		}
		return second;
	}

	public void initSetOff() {
		Intent intent = new Intent("setoff");
		sendBroadcast(intent);
	}

	public static boolean isRunning(Context context, String packageName) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> infos = am.getRunningAppProcesses();
		for (RunningAppProcessInfo rapi : infos) {
			if (rapi.processName.equals(packageName))
				return true;
		}
		return false;
	}

	public void writeFile(int writestr) throws IOException {
		FileWriter fw = null;
		if (writestr == 1) {
			try {
				fw = new FileWriter(new File(
						"/sys/devices/platform/disp/hdmi_ctrl"));
				fw.write("1");
				fw.flush();
			} catch (Exception e) {
				// TODO: handle exception
			} finally {
				try {
					fw.close();
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
		} else {
			try {
				fw = new FileWriter(new File(
						"/sys/devices/platform/disp/hdmi_ctrl"));
				fw.write("0");
				fw.flush();
			} catch (Exception e) {
				// TODO: handle exception
			} finally {
				try {
					fw.close();
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
		}
	}

	public boolean compareFontTimeDay(String nowTimeDay, String compareTimeDay) {
		boolean isExceed = false;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date d1 = df.parse(nowTimeDay);
			Date d2 = df.parse(compareTimeDay);
			if (d1.getTime() >= d2.getTime()) {
				isExceed = true;
			} else {
				isExceed = false;
			}
		} catch (Exception e) {
		}
		return isExceed;

	}

	long downTime = 0;
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			downTime = System.currentTimeMillis();
		}else if (ev.getAction() == MotionEvent.ACTION_UP) {
			long upTime = System.currentTimeMillis();
			if ( ( upTime - downTime) > 3000) {
				Menu.exitApp(this);
				downTime = 0;
				return true;
			}
		}
		
		
		return super.dispatchTouchEvent(ev);
	}
}