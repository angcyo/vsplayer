package com.linux.vshow;

import java.io.File;
import java.io.FileDescriptor;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class VideoPlayer extends SurfaceView {

	public MediaPlayer mediaPlayer = null;
	private int videoWidth;
	private int videoHeight;
	private SurfaceHolder surfaceHolder;
	private String[] al;
	public int cur = 0;
	public ArrayList<String> array = new ArrayList<String>();
	private ArrayList<String> arrayLog = new ArrayList<String>();
	private boolean liu = false;
	private int error = 0;
	private boolean pause = false;
	public int toto = 0;

	private int wVideo = 0;
	private int hVideo = 0;
	private String fileSize = "";
	private String fileindex = "";
	private String fileindex2 = "";
	private int nowindex = 0;

	public ArrayList<Integer> VideoTime = new ArrayList<Integer>();
	public int cur2 = 0;
	public int toto2 = 0;

	public int allTime = 0;
	private Random playRandom = null;

	public void setSeek(int cur, int toto) {
		this.cur = cur;
		this.toto = toto;
	}

	private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
		public void onCompletion(MediaPlayer mp) {
			next();
		}
	};

	private MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
		public void onPrepared(MediaPlayer mp) {
			if (liu) {
				try {
					mediaPlayer.start();
				} catch (Exception e) {

				}
			}
		}
	};

	public void start() {
		if (mediaPlayer != null) {
			if (pause) {
				try {
					mediaPlayer.start();
				} catch (Exception e) {

				}
				pause = false;
			}
		}
	}

	public String getcur() {
		if (mediaPlayer == null) {
			return "0_0";
		}
		// if (liu) {
		// return "0_0";
		// }
		int cr = 0;
		try {
			if (mediaPlayer.isPlaying()) {
				cr = mediaPlayer.getCurrentPosition() / 1000;
			}
			if (cr == 0) {
				if (mediaPlayer.isPlaying()) {
					cr = mediaPlayer.getCurrentPosition() / 1000;
				}
			}
		} catch (Exception e) {

		}
		return cur + "_" + cr;
	}

	public String pause() {
		if (mediaPlayer == null) {
			return "0";
		}
		if (liu) {
			return "0";
		}
		try {
			mediaPlayer.pause();
			pause = true;
		} catch (Exception e) {

		}
		String vf = array.get(cur);
		vf = vf.substring(vf.lastIndexOf("/") + 1, vf.length());
		String[] vfs = vf.split("\\.");
		if (vfs.length == 2) {
			vf = vfs[0] + "_" + vfs[1];
		}
		int curpos = mediaPlayer.getCurrentPosition();
		return vf + "_" + (curpos / 1000);
	}

	public void pause2() {
		if (mediaPlayer == null) {
			return;
		}
		if (liu) {
			return;
		}
		try {
			mediaPlayer.pause();
			pause = true;
		} catch (Exception e) {

		}
	}

	public void fast() {
		if (mediaPlayer != null) {
			nowindex = mediaPlayer.getCurrentPosition();
			nowindex += 1000;
			mediaPlayer.seekTo(nowindex);
		}
	}

	public void slow() {
		if (mediaPlayer != null) {
			nowindex = mediaPlayer.getCurrentPosition();
			nowindex -= 1000;
			if (nowindex < 0) {
				nowindex = 0;
			}
			mediaPlayer.seekTo(nowindex);
		}
	}

	public void next() {
		// if (Constant.playStyle == 1) {
		// if (playRandom == null) {
		// playRandom = new Random();
		// }
		// try {
		// cur = playRandom.nextInt(array.size());
		// } catch (Exception e) {
		// }
		//
		// }
		if (mediaPlayer != null) {
			if (error > 5) {
				if (Constant.avl.size() > 1) {
					Constant.avlbian = true;
					return;
				}
				return;
			}
			if (array.size() == 1) {
				if (liu) {
					try {
						mediaPlayer.reset();
					} catch (Exception e) {

					}
					try {
						cur = 0;
						mediaPlayer.setDataSource(array.get(cur));
						mediaPlayer.prepareAsync();
					} catch (Exception e) {

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
					try {
						cur = 0;
						if (array.get(cur).endsWith(".jm")) {
							RandomAccessFile mRandomFile = new RandomAccessFile(
									array.get(cur), "r");
							FileDescriptor mFileDes = mRandomFile.getFD();
							mediaPlayer.setDataSource(mFileDes, 1734, new File(
									array.get(cur)).length() - 1734);
						} else {
							mediaPlayer.setDataSource(array.get(cur));
						}
						mediaPlayer.prepare();
						mediaPlayer.start();
					} catch (Exception e) {
						error++;
						next();
						return;
					}
				}
			} else {
				cur++;
				if (cur >= array.size()) {
					cur = 0;
					if (error == 0) {
						if (Constant.avl.size() > 1) {
							Constant.avlbian = true;
							return;
						}
					}
				}
				try {
					mediaPlayer.reset();
				} catch (Exception e) {

				}
				try {
					if (array.get(cur).endsWith(".jm")) {
						RandomAccessFile mRandomFile = new RandomAccessFile(
								array.get(cur), "r");
						FileDescriptor mFileDes = mRandomFile.getFD();
						mediaPlayer.setDataSource(mFileDes, 1734, new File(
								array.get(cur)).length() - 1734);
					} else {
						mediaPlayer.setDataSource(array.get(cur));
					}
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
	}

	public void last() {
		if (mediaPlayer != null) {
			if (error > 5) {
				if (Constant.avl.size() > 1) {
					Constant.avlbian = true;
					return;
				}
				return;
			}
			if (array.size() == 1) {
				if (liu) {
					try {
						mediaPlayer.reset();
					} catch (Exception e) {

					}
					try {
						cur = 0;
						mediaPlayer.setDataSource(array.get(cur));
						mediaPlayer.prepareAsync();
					} catch (Exception e) {

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
					try {
						cur = 0;
						if (array.get(cur).endsWith(".jm")) {
							RandomAccessFile mRandomFile = new RandomAccessFile(
									array.get(cur), "r");
							FileDescriptor mFileDes = mRandomFile.getFD();
							mediaPlayer.setDataSource(mFileDes, 1734, new File(
									array.get(cur)).length() - 1734);
						} else {
							mediaPlayer.setDataSource(array.get(cur));
						}
						mediaPlayer.prepare();
						mediaPlayer.start();
					} catch (Exception e) {
						error++;
						next();
						return;
					}
				}
			} else {
				cur--;
				if (cur < 0) {
					cur = array.size() - 1;
					if (error == 0) {
						if (Constant.avl.size() > 1) {
							Constant.avlbian = true;
							return;
						}
					}
				}
				try {
					mediaPlayer.reset();
				} catch (Exception e) {

				}
				try {
					if (array.get(cur).endsWith(".jm")) {
						RandomAccessFile mRandomFile = new RandomAccessFile(
								array.get(cur), "r");
						FileDescriptor mFileDes = mRandomFile.getFD();
						mediaPlayer.setDataSource(mFileDes, 1734, new File(
								array.get(cur)).length() - 1734);
					} else {
						mediaPlayer.setDataSource(array.get(cur));
					}
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
	}

	private MediaPlayer.OnErrorListener mErrorListener = new MediaPlayer.OnErrorListener() {
		public boolean onError(MediaPlayer mp, int framework_err, int impl_err) {
			if (!liu) {
				Constant.change = 20;
			}
			if (arrayLog.size() > 0) {
				Constant.li.writeLog("0000 " + arrayLog.get(cur)
						+ R.string.log69);
			} else {
				Constant.li.writeLog("0000 " + R.string.log69);
			}

			return true;
		}
	};

	public VideoPlayer(Context ct) {
		super(ct);
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// int tempWidth = getDefaultSize(videoWidth, widthMeasureSpec);
		// int tempHeight = getDefaultSize(videoHeight, heightMeasureSpec);
		setMeasuredDimension(videoWidth, videoHeight);
	}

	SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback() {
		public void surfaceChanged(SurfaceHolder holder, int format, int w,
				int h) {

		}

		public void surfaceCreated(SurfaceHolder holder) {

			try {
				if (mediaPlayer == null) {
					mediaPlayer = new MediaPlayer();
					mediaPlayer.setOnCompletionListener(mCompletionListener);

					if (liu) {
						mediaPlayer.setOnPreparedListener(mPreparedListener);
					}
					mediaPlayer.setOnErrorListener(mErrorListener);
					mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				}
				mediaPlayer.setDisplay(surfaceHolder);
			} catch (Exception e) {

			}

			if (Constant.firstVideo) {
				try {
					mediaPlayer.reset();
					mediaPlayer.setDataSource(array.get(Constant.videocur));
					mediaPlayer.prepare();
					mediaPlayer.start();
					if ((Constant.videototo > 0)
							&& (Constant.videototo <= mediaPlayer.getDuration())) {
						mediaPlayer.seekTo(Constant.videototo * 1000);
					} else {
						Constant.change = 13;
					}
				} catch (Exception e) {
				}
				Constant.firstVideo = false;
				Constant.videocur = 0;
				Constant.videototo = 0;
			} else {
				if (!liu) {
					try {
						if (array.get(cur).endsWith(".jm")) {
							RandomAccessFile mRandomFile = new RandomAccessFile(
									array.get(cur), "r");
							FileDescriptor mFileDes = mRandomFile.getFD();
							mediaPlayer.setDataSource(mFileDes, 1734, new File(
									array.get(cur)).length() - 1734);
						} else {
							mediaPlayer.setDataSource(array.get(cur));
						}
						mediaPlayer.prepare();
						mediaPlayer.start();
						if ((toto > 0) && (toto <= mediaPlayer.getDuration())) {
							mediaPlayer.seekTo(toto * 1000);
						}

					} catch (Exception e) {
						error++;
						next();
					}
				} else {

					try {
						cur = 0;
						mediaPlayer.setDataSource(array.get(cur));
						mediaPlayer.prepareAsync();
					} catch (Exception e) {
					}
				}
			}
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			rel();
		}
	};

	public void rel() {
		if (mediaPlayer != null) {
			try {
				mediaPlayer.release();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

		mediaPlayer = null;
	}

	public void stop() {
		if (liu) {
			if (mediaPlayer != null) {
				try {
					mediaPlayer.stop();
				} catch (Exception e) {

				}
			}
		}
	}

	public void init(String files, int width, int height) {
		String temdir;
		if (Constant.offshow) {
			temdir = Constant.offDir;
		} else {
			temdir = Constant.fileDir;
		}
		if (Constant.imgtime > 0) {
			temdir = Constant.offDir + Constant.mfile + File.separator;
		}
		files = files.trim();
		if (files.length() < 1) {
			return;
		}
		array.clear();
		arrayLog.clear();
		error = 0;
		Constant.allVideoTime = 0;
		if (files.indexOf(':') == -1) {
			al = files.split("\\|");
			String strs;
			String str;
			for (int i = 0; i < al.length; i++) {
				strs = al[i].trim();
				if (strs.length() > 0) {
					arrayLog.add(strs);
					str = temdir + strs;
					if (new File(str).exists()) {
						if (str.endsWith(".jm")) {
							File file = new File(str);
							fileSize = file.length() + "";
							if (fileSize.length() >= 4) {
								fileindex = fileSize.substring(
										fileSize.length() - 4,
										fileSize.length());
								fileindex2 = strs.substring(0, 1)
										+ strs.substring(2, 3)
										+ strs.substring(4, 5)
										+ strs.substring(6, 7);
								if (fileindex.equals(fileindex2)) {
									array.add(str);
								}
							}
						} else {
							array.add(str);
							try {
								MediaMetadataRetriever retrs = new MediaMetadataRetriever();
								retrs.setDataSource(str);
								String duration = retrs
										.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
								VideoTime
										.add(Integer.parseInt(duration) / 1000);
								Constant.allVideoTime += Integer
										.parseInt(duration) / 1000;
							} catch (Exception e) {
							}
						}
					}
				}
			}

			liu = false;
		} else {
			if (files.indexOf("http") == -1) {
				liu = true;
				array.add(files);
			}
		}
		if (array.size() == 0) {
			return;
		}

		videoWidth = width;
		videoHeight = height;
		surfaceHolder = getHolder();
		surfaceHolder.addCallback(mSHCallback);
		surfaceHolder.setFixedSize(width, height);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnCompletionListener(mCompletionListener);

		if (liu) {
			mediaPlayer.setOnPreparedListener(mPreparedListener);
		}
		mediaPlayer.setOnErrorListener(mErrorListener);
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

		if (Constant.scale == 1) {
			MediaMetadataRetriever retr = new MediaMetadataRetriever();
			retr.setDataSource(array.get(cur));
			Bitmap bm = retr.getFrameAtTime();
			wVideo = bm.getWidth();
			hVideo = bm.getHeight();
			int wDiffer = videoWidth - wVideo;
			int hDiffer = videoHeight - hVideo;
			if (wDiffer > hDiffer) {
				videoWidth = wVideo + hDiffer;
				Constant.vpX = videoWidth;
			} else if (wDiffer < hDiffer) {
				videoHeight = hVideo + wDiffer;
				Constant.vpY = videoHeight;
			}
		}
	}
}
