package com.linux.vshow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class GetAllItem {
	public List get() {
		ArrayList list = new ArrayList();
		Item it;
		File f = new File(Constant.fDir);
		File[] fd = f.listFiles();
		File tf;
		File tff;
		String tmodel = "";
		BufferedReader fad;
		Item ix;
		boolean ydso = true;
		for (int i = 0; i < fd.length; i++) {
			tf = fd[i];
			if (tf.isDirectory()) {
				tff = new File(Constant.fileDir + tf.getName() + File.separator
						+ "key.txt");
				if (tff.exists()) {
					try {
						fad = new BufferedReader(new InputStreamReader(
								new FileInputStream(tff), "UTF-8"));
						tmodel = fad.readLine().trim();
						fad.close();
					} catch (Exception e) {

					}
					if (tmodel.length() > 0) {
						it = new Item();
						it.name = (tmodel.split("\\@"))[0];
						ydso = true;
						for (int j = 0; j < list.size(); j++) {
							ix = (Item) list.get(j);
							if (ix.name.equals(it.name)) {
								ydso = false;
								if (tf.getName().compareTo(ix.link) > 0) {
									ix.link = tf.getName();
								}
							}
						}
						if (ydso) {
							it.link = tf.getName();
							list.add(it);
						}
					}
				}
			}
		}
		return list;
	}
}
