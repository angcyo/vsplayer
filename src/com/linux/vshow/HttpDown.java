package com.linux.vshow;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Enumeration;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import android.os.StatFs;

public class HttpDown {

	private String url;
	private String str;
	private String msg;
	private int down;
	public boolean out = false;
	public boolean dodo = false;

	private void pdSDCard() {
		StatFs sf = new StatFs(Constant.sdcardDir);
		long blocSize = sf.getBlockSize();
		long availCount = sf.getAvailableBlocks() * blocSize / 1024;
		long t = 10 * 1024;
		if (availCount < t) {
//			Tool.disable_watchdog();
			Tool.saveConfig("playlist! " + "%timelist! " + "%cblist! ",
					Constant.config);
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
//			Tool.enable_watchdog();
		}
	}

//	private void pdSDCard() {
//		StatFs sf = new StatFs(Constant.sdcardDir);
//		long blocSize = sf.getBlockSize();
//		long availCount = sf.getAvailableBlocks() * blocSize / 1024;
//		long t = 10 * 1024;
//		if (availCount < t) {
//			Tool.disable_watchdog();
//			String play[] = Tool.loadConfig(new String[] { "playlist",
//					"timelist", "cblist" }, Constant.config);
//			String pl[] = null;
//			String plist = "";
//			String tl[] = null;
//			String tlist = "";
//			String cs[] = null;
//			String clist = "";
//			String fileName = "";
//			String fileZip = "";
//			String allFile[] = null;
//			String fileMp4 = "";
//			try {
//				plist = play[0].trim();
//				if (plist != "") {
//					fileName = plist + "|";
//					pl = plist.split("\\|");
//					for (int i = 0; i < pl.length; i++) {
//						fileZip += pl[i] + ".zip ";
//					}
//				}
//			} catch (Exception e) {
//			}
//			try {
//				tlist = play[1].trim();
//				if (tlist != "") {
//					String tstr[] = tlist.split("\\_");
//					if (tstr.length == 4) {
//						tlist = tstr[3].trim();
//						fileName += (tlist + "|");
//						tl = tlist.split("\\|");
//						for (int i = 0; i < tl.length; i++) {
//							fileZip += (tl[i] + ".zip ");
//						}
//					}
//				}
//			} catch (Exception e) {
//			}
//			try {
//				clist = play[2].trim();
//				if (clist != "") {
//					String cstr[] = clist.split("\\_");
//					if (cstr.length == 3) {
//						clist = cstr[2].trim();
//						fileName += clist;
//						cs = clist.split("\\|");
//						for (int i = 0; i < cs.length; i++) {
//							fileZip += (cs[i] + ".zip ");
//						}
//					}
//				}
//			} catch (Exception e) {
//			}
//
//			allFile = fileName.split("\\|");
//			for (int i = 0; i < allFile.length; i++) {
//				String rssread = "";
//				String temread = "";
//				File fM = new File(Constant.fDir + "/" + allFile[i]
//						+ "/vsjm.txt");
//				if (fM.exists()) {
//					try {
//						BufferedReader fad = new BufferedReader(new FileReader(
//								fM));
//						while ((temread = fad.readLine()) != null) {
//							rssread += temread;
//						}
//						fad.close();
//					} catch (Exception e) {
//
//					}
//					String strs[] = rssread.split("\\*");
//					if (strs.length == 7) {
//						try {
//							String mp = strs[2].trim();
//							if (mp != "") {
//								String strs2[] = mp.split("\\@");
//								if (strs2.length == 2) {
//									fileMp4 += strs2[1].trim() + " ";
//								}
//							}
//						} catch (Exception e) {
//						}
//
//					}
//				}
//			}
//
//			File f2 = new File(Constant.fDir);
//			File[] flist = f2.listFiles();
//			for (int i = 0; i < flist.length; i++) {
//				if (!fileName.contains(flist[i].getName())
//						&& !fileZip.contains(flist[i].getName())
//						&& !fileMp4.contains(flist[i].getName())) {
//					flist[i].delete();
//				}
//			}
//
//			File f1 = new File(Constant.tDir);
//			File f3 = new File(Constant.offDir);
//			File f4 = new File(Constant.cDir);
//			Tool.deleteDirectory(f1);
//			f1.mkdir();
//			Tool.deleteDirectory(f3);
//			f3.mkdir();
//			Tool.deleteDirectory(f4);
//			f4.mkdir();
//			Tool.enable_watchdog();
//		}
//	}

	public void start() {

		Constant.downing = true;
		pdSDCard();

		Constant.downtotal = 0;
		Constant.alltotal = 0;

		String[] strrs = str.split("\\/");
		if (strrs.length == 2) {
			try {
				Constant.alltotal = Long.parseLong(strrs[0]);
			} catch (Exception e) {
				// TODO: handle exception
			}
			str = strrs[1];
		}

		if (down == 1) {
			if (str.length() > 0) {
				Constant.li.writeLog("0000 节目正在下载");
				String[] sts = str.split("\\|");
				String tfs;
				
				for (int i = 0; i < sts.length; i++) {
					tfs = sts[i].trim();
					if (tfs.length() == 0) {
						continue;
					}
					dodo = false;
					if ((!tfs.endsWith(".zip"))
							&& (new File(Constant.fileDir + tfs).exists())) {
						Constant.downtotal += new File(Constant.fileDir + tfs)
								.length();
						continue;
					}
					if (tfs.endsWith(".zip")) {
						if (new File(Constant.tempDir + tfs).exists()) {
							new File(Constant.tempDir + tfs).delete();
						}
					}
					while (true) {
						if (out) {
							return;
						}
						if (get(url + "/" + tfs, new File(Constant.tempDir
								+ tfs), new File(Constant.fileDir + tfs)) != 0) {
							break;
						} else {
							if (out) {
								return;
							}
							try {
								Thread.sleep(10000);
							} catch (Exception e) {
								// TODO: handle exception
							}
						}
					}
				}
			}
			Constant.li.writeLog("0000 节目下载完成");
			Tool.saveConfig("downtotal!" + Constant.downtotal + "%alltotal!"
					+ Constant.alltotal, Constant.config3);
			Constant.downing = false;
			Constant.downmsg = msg;
			Constant.change = 2;
		} else if (down == 2) {
			Constant.li.writeLog("0000 下载更新apk中");	
			if (new File(Constant.updateDir + str).exists()) {
				new File(Constant.updateDir + str).delete();
			}
			if (new File(Constant.tempDir + str).exists()) {
				new File(Constant.tempDir + str).delete();
			}
			dodo = false;
			while (true) {
				if (out) {
					return;
				}
				if (get(url + "/" + str, new File(Constant.tempDir + str),
						new File(Constant.updateDir + str)) != 0) {
					break;
				} else {
					if (out) {
						return;
					}
					try {
						Thread.sleep(10000);
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			}
			Constant.li.writeLog("0000 下载更新apk完成");	
			Tool.saveConfig("downtotal!" + Constant.downtotal + "%alltotal!"
					+ Constant.alltotal, Constant.config3);
			Constant.downing = false;
			Constant.msg = msg;
			Constant.downmsg = str;
			Constant.change = 10;
		}
	}

	public void setFile(String url, String str, String msg, int down) {
		this.url = url;
		this.str = str;
		this.msg = msg;
		this.down = down;
	}

	public int get(String urlStr, File tfile, File ffile) {
		long iStartPos = 0;
		RandomAccessFile fs = null;
		HttpURLConnection conn = null;
		InputStream bs = null;
		try {
			URL url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout((Constant.lian * 3) * 1000);
			conn.setReadTimeout((Constant.lian * 3) * 1000);
			if (conn.getResponseCode() == 404) {
				try {
					conn.disconnect();
				} catch (Exception e2) {
				}
				return 1;
			}
			if (ffile.exists()) {
				long flen = conn.getContentLength();
				if (ffile.length() == flen) {
					Constant.downtotal += flen;
					String fnn = ffile.getAbsolutePath();
					String fd = fnn.substring(0, fnn.lastIndexOf("."));
					if (new File(fd + "/tq.html").exists()) {
						Tool.copyFile(Constant.TQDIR + "/tq.html", new File(fd
								+ "/tq.html"));
					}
					if (new File(fd + "/hl.html").exists()) {
						Tool.copyFile(Constant.HLDIR + "/hl.html", new File(fd
								+ "/hl.html"));
					}
					try {
						conn.disconnect();
					} catch (Exception e2) {
					}
					return 1;
				} else {
					ffile.delete();
				}
			}
			try {
				fs = new RandomAccessFile(tfile, "rw");
			} catch (Exception e) {
				File f1 = new File(Constant.tempDir);
				Tool.deleteDirectory2(f1);
			}
			
			Constant.rss = fs;
			iStartPos = fs.length();
			if (iStartPos > 0) {
				if (!dodo) {
					Constant.downtotal += iStartPos;
				}
				dodo = true;
				fs.seek(iStartPos);
				conn.disconnect();
				conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout((Constant.lian * 3) * 1000);
				conn.setReadTimeout((Constant.lian * 3) * 1000);
				conn.setRequestProperty("RANGE", "bytes=" + iStartPos + "-");
			}
			bs = conn.getInputStream();
			byte[] b = new byte[4096];
			int nRead;

			float bian = 1024;
			float shenyu = 0;
			float sltime = 0;
			float last = 0;
			long oldtime = 0;
			long shenyutime = 0;
			if (Constant.xiansu > 0) {
				oldtime = new Date().getTime();
				last = Constant.downtotal;
			}
			while ((nRead = bs.read(b)) != -1) {
				if (out) {
					try {
						fs.close();
					} catch (Exception e2) {
					}
					try {
						bs.close();
					} catch (Exception e2) {
					}
					try {
						conn.disconnect();
					} catch (Exception e2) {
					}
					return 0;
				}
				Constant.downtotal = Constant.downtotal + nRead;
				fs.write(b, 0, nRead);
				if (Constant.xiansu > 0) {
					shenyu = Constant.downtotal - last;
					if (shenyu >= bian) {
						sltime = (1000 * shenyu) / (Constant.xiansu * 1024);
						shenyutime = new Date().getTime() - oldtime;
						if (sltime - shenyutime > 0) {
							try {
								Thread.sleep((long) (sltime - shenyutime));
							} catch (Exception e) {

							}
						}
						last = Constant.downtotal;
						oldtime = new Date().getTime();
					}
				}

			}
			fs.close();
			Constant.rss = null;
			bs.close();
			conn.disconnect();
		} catch (Exception e) {
			try {
				fs.close();
			} catch (Exception e2) {
			}
			Constant.rss = null;
			try {
				bs.close();
			} catch (Exception e2) {
			}
			try {
				conn.disconnect();
			} catch (Exception e2) {
			}
			return 0;
		}
		if (ffile.exists()) {
			ffile.delete();
		}
		tfile.renameTo(ffile);
		String fnn = ffile.getAbsolutePath();
		if (fnn.endsWith(".zip")) {
			String fd = fnn.substring(0, fnn.lastIndexOf("."));
			Tool.deleteDirectory(new File(fd));
			try {
				unzip(fnn, fd);
				if (new File(fd + "/tq.html").exists()) {
					Tool.copyFile(Constant.TQDIR + "/tq.html", new File(fd
							+ "/tq.html"));
				}
				if (new File(fd + "/hl.html").exists()) {
					Tool.copyFile(Constant.HLDIR + "/hl.html", new File(fd
							+ "/hl.html"));
				}
			} catch (Exception e) {

			}
		}
		return 2;
	}

	public static void unzip(String zipFilePath, String targetPath)
			throws Exception {

		OutputStream os = null;
		InputStream is = null;
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(zipFilePath);
			String directoryPath = zipFilePath.substring(0,
					zipFilePath.lastIndexOf("."));
			new File(directoryPath).mkdir();
			Enumeration entryEnum = zipFile.getEntries();
			if (null != entryEnum) {
				ZipEntry zipEntry = null;
				while (entryEnum.hasMoreElements()) {
					zipEntry = (ZipEntry) entryEnum.nextElement();
					if (!zipEntry.isDirectory()) {
						File targetFile = new File(directoryPath
								+ File.separator + zipEntry.getName());
						os = new BufferedOutputStream(new FileOutputStream(
								targetFile));
						is = zipFile.getInputStream(zipEntry);
						byte[] buffer = new byte[4096];
						int readLen = 0;
						while ((readLen = is.read(buffer, 0, 4096)) >= 0) {
							os.write(buffer, 0, readLen);
						}
						os.flush();
						os.close();
						os = null;
						is.close();
						is = null;
					}
				}
			}
		} catch (Exception e) {

		} finally {
			if (null != is) {
				try {
					is.close();
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
			if (null != os) {
				try {
					os.close();
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
			if (null != zipFile) {
				try {
					zipFile.close();
					zipFile = null;
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
		}
	}

}
