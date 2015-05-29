package com.linux.vshow;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipOutputStream;

public class DoZip extends Thread {

	String inputFileName = "";
	String zipFileName = "";

	public DoZip(String inputFileName, String zipFileName) {
		this.inputFileName = inputFileName;
		this.zipFileName = zipFileName;
	}

	@Override
	public void run() {
		try {
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
					zipFileName));
			zip(out, new File(inputFileName), "");
			out.close();
			if (Constant.upCameraFile) {
				Constant.change = 40;
			} else {
				Constant.change = 16;
			}
		} catch (Exception e) {
		}

	}

	// public void zip(String inputFileName, String zipFileName) throws
	// Exception {
	// zip(zipFileName, new File(inputFileName));
	// }
	//
	// private void zip(String zipFileName, File inputFile) throws Exception {
	// ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
	// zipFileName));
	// zip(out, inputFile, "");
	// out.close();
	// }

	private void zip(ZipOutputStream out, File f, String base) throws Exception {
		if (f.isDirectory()) {
			File[] fl = f.listFiles();
			out.putNextEntry(new org.apache.tools.zip.ZipEntry(base + "/"));
			base = base.length() == 0 ? "" : base + "/";
			for (int i = 0; i < fl.length; i++) {
				zip(out, fl[i], base + fl[i].getName());
			}
		} else {
			out.putNextEntry(new org.apache.tools.zip.ZipEntry(base));
			FileInputStream in = new FileInputStream(f);
			byte[] buffer = new byte[4096];
			int readLen = 0;
			while ((readLen = in.read(buffer, 0, 4096)) >= 0) {
				out.write(buffer, 0, readLen);
			}
			in.close();
		}
	}

}
