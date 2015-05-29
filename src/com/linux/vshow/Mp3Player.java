package com.linux.vshow;

import java.io.File;
import java.util.ArrayList;
import android.media.AudioManager;
import android.media.MediaPlayer;

public class Mp3Player {

	private MediaPlayer mediaPlayer = null;
	private String[] al;
	private int cur = 0;
	private ArrayList<String> array = new ArrayList();
	private int error = 0;

	private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
		public void onCompletion(MediaPlayer mp) {
			next();
		}
	};

	public void next() {
		if (error > 5) {
			if (Constant.avl.size() > 1) {
				Constant.avlbian = true;
				return;
			}
			return;
		}
		if (array.size() == 1) {
			try {
				mediaPlayer.reset();
			} catch (Exception e) {

			}
			try {
				cur = 0;
				mediaPlayer.setDataSource(array.get(cur));
				mediaPlayer.prepare();
				mediaPlayer.start();
			} catch (Exception e) {
				error++;
				next();
				return;
			}
		} else {
			if (error == 0) {
				if (Constant.avl.size() > 1) {
					Constant.avlbian = true;
					return;
				}
			}
			try {
				mediaPlayer.reset();
			} catch (Exception e) {

			}
			cur++;
			if (cur >= array.size()) {
				cur = 0;
			}
			try {
				mediaPlayer.setDataSource(array.get(cur));
				mediaPlayer.prepare();
				mediaPlayer.start();
			} catch (Exception e) {
				error++;
				next();
				return;
			}
		}
		error = 0;
	}

	private MediaPlayer.OnErrorListener mErrorListener = new MediaPlayer.OnErrorListener() {
		public boolean onError(MediaPlayer mp, int framework_err, int impl_err) {
//			Constant.change = 20;
			return true;
		}
	};

	public void rel() {
		if (mediaPlayer != null) {
			try {
				mediaPlayer.release();
			} catch (Exception e) {
				// TODO: handle exception
			}
			mediaPlayer = null;
		}
	}

	public void init(String files) {
		String temdir;
		if (Constant.offshow) {
			temdir = Constant.offDir;
		} else {
			temdir = Constant.fileDir;
		}
		files = files.trim();
		if (files.length() < 1) {
			return;
		}
		array.clear();
		error = 0;
		if (files.indexOf(':') == -1) {
			al = files.split("\\|");
			String str;
			for (int i = 0; i < al.length; i++) {
				str = al[i].trim();
				if (str.length() > 0) {
					str = temdir + str;
					if (new File(str).exists()) {
						array.add(str);
					}
				}
			}
		}
		if (array.size() == 0) {
			return;
		}
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnCompletionListener(mCompletionListener);
		mediaPlayer.setOnErrorListener(mErrorListener);
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try {
			mediaPlayer.setDataSource(array.get(cur));
			mediaPlayer.prepare();
			mediaPlayer.start();
		} catch (Exception e) {
			error++;
			next();
		}
	}
}
