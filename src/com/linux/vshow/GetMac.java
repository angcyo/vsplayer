package com.linux.vshow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class GetMac {
	public void getLocalMacAddress() {
		// String[] suiji = { "E", "0", "2", "1", "3", "5", "D", "8", "C", "B",
		// "9", "A", "F", "7", "6", "4" };
		File ff = new File(Constant.mc);
		FileWriter fff;
		String mac = "";
		if (!ff.exists()) {
			try {
				ff.createNewFile();
				fff = new FileWriter(ff);
				mac = getMacAddress();
				if (mac != null) {
					fff.write(mac);
					// fff.write(suiji[new Random().nextInt(11)]
					// + suiji[new Random().nextInt(9)] + ":"
					// + suiji[new Random().nextInt(7)]
					// + suiji[new Random().nextInt(10)] + ":"
					// + suiji[new Random().nextInt(12)]
					// + suiji[new Random().nextInt(13)] + ":"
					// + suiji[new Random().nextInt(5)]
					// + suiji[new Random().nextInt(8)] + ":"
					// + suiji[new Random().nextInt(16)]
					// + suiji[new Random().nextInt(15)] + ":"
					// + suiji[new Random().nextInt(6)]
					// + suiji[new Random().nextInt(14)]);
				}
				fff.flush();
				fff.close();
				Tool.enable_sync(Constant.mc);
			} catch (Exception e) {

			}
		}

		try {
			BufferedReader fr = new BufferedReader(new FileReader(ff));
			Constant.mac = fr.readLine().trim();
			fr.close();
		} catch (Exception e) {

		}
	}

	public String getMacAddress() {
		try {
			return loadFileAsString("/sys/class/net/eth0/address")
					.toUpperCase().substring(0, 17);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String loadFileAsString(String filePath)
			throws java.io.IOException {
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
		}
		reader.close();
		return fileData.toString();
	}

}
