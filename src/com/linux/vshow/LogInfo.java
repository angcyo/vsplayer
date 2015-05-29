package com.linux.vshow;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;

public class LogInfo {

	private final static int UPINFO = 30;

	public void writeLog(String info) {
		if (info != null || ("").equals(info)) {
			File file = new File(getDate());
			if (!file.exists()) {
				try {
					file.createNewFile();
					writeLog("0000  创建日志");
					deleteLog(new File(Constant.LogDir));
				} catch (Exception e) {

				}
			}
			BufferedWriter writer = null;
			try {
				writer = new BufferedWriter(new FileWriter(file, true));
				writer.write(getDateTime() + "  " + info + "\r\n");
				Log.v("message", getDateTime() + "  " + info);
				writer.flush();
			} catch (Exception e) {
				file.delete();
				writeLog("0001 日志错误删除日志");
			} finally {
				try {
					if (writer != null) {
						writer.close();
					}
				} catch (IOException e) {
				}
			}
		}
//		Tool.enable_sync(getDate());
	}

	private String getDate() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

		return Constant.LogDir + File.separator + df.format(new Date())
				+ ".txt";
	}

	private String getDateTime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		return df.format(new Date());
	}

	public void deleteLog(File path) {
		int dateNumber = 0;
		String earliest = "";
		if (path.exists() && path.isDirectory()) {
			File[] files = path.listFiles();
			if (files.length > UPINFO) {
				for (int i = 0; i < files.length; i++) {
					dateNumber = 0;
					if (i > 0) {
						try {
							dateNumber = compare_date(files[i].getPath(),
									earliest);
							if (dateNumber == 1) {
								earliest = files[i].getPath();
							}
						} catch (Exception e) {
						}
					} else {
						earliest = files[i].getPath();
					}
				}
				new File(earliest).delete();
			}
		}
	}

	public int compare_date(String DATE1, String DATE2) {
		DATE1 = DATE1.replace(Constant.LogDir + File.separator, "");
		DATE1 = DATE1.replace(".txt", "");
		DATE2 = DATE2.replace(Constant.LogDir + File.separator, "");
		DATE2 = DATE2.replace(".txt", "");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date dt1 = df.parse(DATE1);
			Date dt2 = df.parse(DATE2);
			if (dt1.getTime() > dt2.getTime()) {
				return 1;
			} else if (dt1.getTime() < dt2.getTime()) {
				return -1;
			} else {
				return 0;
			}
		} catch (Exception exception) {
		}
		return 0;
	}

}
