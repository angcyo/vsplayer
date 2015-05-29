package com.linux.vshow;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import android.util.Log;

import com.xboot.stdcall.posix;

public class Tool {

	public static void setPowerOnOff(byte on_h, byte on_m, byte off_h,
			byte off_m, byte enable) {
		int fd, ret;
		fd = posix.open("/dev/McuCom", posix.O_RDWR, 0666);
		if (fd < 0) {
			posix.close(fd);
			return;
		}
		ret = posix.poweronoff(off_h, off_m, on_h, on_m, enable, fd);
		if (ret != 0) {
			posix.close(fd);
			return;
		}
		posix.close(fd);
	}

	// public static void enable_watchdog() {
	// int fd, ret;
	// fd = posix.open("/dev/McuCom", posix.O_RDWR, 0666);
	// if (fd < 0) {
	// return;
	// }
	// ret = posix.watchdogenable((byte) 1, fd);
	// if (ret != 0) {
	// posix.close(fd);
	// return;
	// }
	// posix.close(fd);
	// }

	public static void enable_sync(String str) {
		int fd;
		fd = posix.open(str, posix.O_SYNC, 0666);
		if (fd < 0) {
			return;
		}
		posix.fsync(fd);
		posix.close(fd);
	}

	// public static void disable_watchdog() {
	// int fd, ret;
	// fd = posix.open("/dev/McuCom", posix.O_RDWR, 0666);
	// if (fd < 0) {
	// return;
	// }
	// ret = posix.watchdogenable((byte) 0, fd);
	// if (ret != 0) {
	// posix.close(fd);
	// return;
	// }
	// posix.close(fd);
	// }

	// public static void feed_watchdog() {
	// int fd, ret;
	// fd = posix.open("/dev/McuCom", posix.O_RDWR, 0666);
	// if (fd < 0) {
	// return;
	// }
	// ret = posix.watchdogfeed(fd);
	// if (ret != 0) {
	// posix.close(fd);
	// return;
	// }
	// posix.close(fd);
	// }

	public static String[] loadConfig(String[] keys, String config) {
		FileInputStream s;
		try {
			s = new FileInputStream(config);
		} catch (Exception e) {
			return null;
		}
		String[] tkeys = new String[keys.length];
		Properties properties = new Properties();
		try {
			properties.load(s);
		} catch (Exception e) {
			try {
				s.close();
			} catch (Exception ex) {

			}
			return null;
		}
		for (int i = 0; i < tkeys.length; i++) {
			tkeys[i] = properties.getProperty(keys[i], "");
		}
		try {
			s.close();
		} catch (Exception e) {

		}
		return tkeys;
	}

	public static void saveConfig(String content, String config) {
		if (content.length() < 3) {
			return;
		}
		String[] sts = content.split("\\%");
		if (sts.length < 1) {
			return;
		}
		Properties prop = new Properties();
		String[] s2;
		InputStream fis = null;
		OutputStream fos = null;
		try {
			fis = new FileInputStream(config);
			try {
				prop.load(fis);
				try {
					fis.close();
				} catch (Exception e) {

				}
			} catch (Exception e) {
				try {
					fis.close();
				} catch (Exception e2) {

				}
			}
		} catch (Exception e) {

		}
		try {
			fos = new FileOutputStream(config);
		} catch (Exception e) {
			return;
		}
		for (int i = 0; i < sts.length; i++) {
			// if (i == 1) {
			// writeLog(sts[i].trim());
			// }
			s2 = sts[i].split("\\!");

			if (s2.length == 2) {
				if (i == 1 && sts.length == 2) {
					writeLog(s2[0].trim(), s2[1].trim());
				}
				prop.setProperty(s2[0].trim(), s2[1].trim());
			}
		}
		try {
			prop.store(fos, "");
		} catch (IOException e) {

		}
		try {
			fos.close();
		} catch (IOException e) {

		}
		enable_sync(config);
	}

	public static void deleteDirectory(File path) {
		if (path.exists() && path.isDirectory()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					try {
						files[i].delete();
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			}
		}
		try {
			path.delete();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static void deleteDirectory2(File path) {
		if (path.exists() && path.isDirectory()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					try {
						files[i].delete();
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			}
		}

	}


	public static String install(String str) {
		String[] args = { "/system/bin/pm", "install", "-r", str };
		String result = "";
		ProcessBuilder processBuilder = new ProcessBuilder(args);
		Process process = null;
		InputStream errIs = null;
		InputStream inIs = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int read = -1;
			process = processBuilder.start();
			errIs = process.getErrorStream();
			while ((read = errIs.read()) != -1) {
				baos.write(read);
			}
			baos.write('\n');
			inIs = process.getInputStream();
			while ((read = inIs.read()) != -1) {
				baos.write(read);
			}
			byte[] data = baos.toByteArray();
			result = new String(data);
			return result;
		} catch (Exception e) {
			return "";
		} finally {
			try {
				if (errIs != null) {
					errIs.close();
				}
				if (inIs != null) {
					inIs.close();
				}
			} catch (Exception e) {
			}
		}
	}
	
	public static void uninstall(String str) {
		String[] args = { "/system/bin/pm", "uninstall",str };
		ProcessBuilder processBuilder = new ProcessBuilder(args);
		String result = "";
		Process process = null;
		InputStream errIs = null;
		InputStream inIs = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int read = -1;
			process = processBuilder.start();
			errIs = process.getErrorStream();
			while ((read = errIs.read()) != -1) {
				baos.write(read);
			}
			baos.write('\n');
			inIs = process.getInputStream();
			while ((read = inIs.read()) != -1) {
				baos.write(read);
			}
			byte[] data = baos.toByteArray();
			result = new String(data);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (errIs != null) {
					errIs.close();
				}
				if (inIs != null) {
					inIs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (process != null) {
				process.destroy();
			}
		}
	}

	

	public static void copyFile(String targetFile, File file) {
		File fsd = new File(targetFile);
		if (fsd.exists()) {
			fsd.delete();
		}
		try {
			FileInputStream is = new FileInputStream(file);
			FileOutputStream fos = new FileOutputStream(targetFile);
			byte[] buffer = new byte[4096];
			int readLen = 0;
			while ((readLen = is.read(buffer, 0, 4096)) >= 0) {
				fos.write(buffer, 0, readLen);
			}
			fos.close();
			is.close();
		} catch (Exception e) {

		}
	}

	public static void write_cmd(byte on_h, byte on_m, byte off_h, byte off_m) {
		try {
			int fd, ret;
			byte buf[] = { 0, 3, 0, 3 };
			fd = posix.open("/dev/McuCom", posix.O_RDWR, 0666);
			if (fd < 0) {
				posix.close(fd);
				return;
			}
			buf[0] = 17;
			buf[1] = on_h;
			ret = posix.write(fd, buf);
			if (ret < 0) {
				posix.close(fd);
				return;
			}
			buf[0] = 18;
			buf[1] = on_m;
			ret = posix.write(fd, buf);
			if (ret < 0) {
				posix.close(fd);
				return;
			}
			buf[0] = 33;
			buf[1] = off_h;
			ret = posix.write(fd, buf);
			if (ret < 0) {
				posix.close(fd);
				return;
			}
			buf[0] = 34;
			buf[1] = off_m;
			ret = posix.write(fd, buf);
			if (ret < 0) {
				posix.close(fd);
				return;
			}
			buf[0] = 49;
			buf[1] = 3;
			ret = posix.write(fd, buf);
			if (ret < 0) {
				posix.close(fd);
				return;
			}
			posix.close(fd);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return;
	}

	public static void saveDownConfig(String content, String config) {
		if (content.length() < 3) {
			return;
		}
		Properties prop = new Properties();
		String[] s2;
		InputStream fis = null;
		OutputStream fos = null;
		try {
			fis = new FileInputStream(config);
			try {
				prop.load(fis);
				try {
					fis.close();
				} catch (Exception e) {

				}
			} catch (Exception e) {
				try {
					fis.close();
				} catch (Exception e2) {

				}
			}
		} catch (Exception e) {

		}
		try {
			fos = new FileOutputStream(config);
		} catch (Exception e) {
			return;
		}

		s2 = content.split("\\~");
		if (s2.length == 2) {
			prop.setProperty(s2[0].trim(), s2[1].trim());

		}
		try {
			prop.store(fos, "");
		} catch (IOException e) {

		}
		try {
			fos.close();
		} catch (IOException e) {

		}
		enable_sync(config);
	}

	private static void writeLog(String str, String str2) {
		if (str.equals("timelist")) {
			if (str2.equals("")) {
				Constant.li.writeLog("0000 清理轮播节目成功");
				Log.v("message", "0000 清理轮播节目成功");
			}

		} else if (str.equals("playlist")) {
			if (str2.equals("")) {
				Constant.li.writeLog("0000 清理播放节目成功");
				Log.v("message", "0000 清理播放节目成功");
			}
		} else if (str.equals("cblist")) {
			if (str2.equals("")) {
				Constant.li.writeLog("0000 清理插播节目成功");
				Log.v("message", "0000 清理插播节目成功");
			}
		} else if (str.equals("xiansu")) {
			if (!str2.equals("")) {
				Constant.li.writeLog("0000 设置下载限速为" + str2);
				Log.v("message", "0000 设置下载限速为" + str2);
			}
		} else if (str.equals("urltime")) {
			if (!str2.equals("")) {
				Constant.li.writeLog("0000 设置触摸间隔时间为" + str2);
				Log.v("message", "0000 设置触摸间隔时间为" + str2);
			}
		} else if (str.equals("lian")) {
			if (!str2.equals("")) {
				Constant.li.writeLog("0000 设置心跳连接时间为" + str2);
				Log.v("message", "0000 设置心跳连接时间为" + str2);
			}
		}
	}
}
