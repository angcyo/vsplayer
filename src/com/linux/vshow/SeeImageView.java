package com.linux.vshow;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SeeImageView extends ImageView {



	private String[] imgs;
	private int slptime = 0;
	public int gotime = 0;
	public int cur = 0;
	public int ttal = 0;
	private Bitmap bitmap = null;
	private String link = "";
	private Context ct = null;
	private int effects = 0;
	private int count = 0;

	private int imageTime = 0;
	private int indexPlay = 0;

	private int nowTime = 0;
	int iw = 1;
	int ih = 1;

	public SeeImageView(Context arg0) {
		super(arg0);
		this.ct = arg0;
		setBackgroundColor(0);
		if (Constant.scale == 0) {
			setScaleType(ImageView.ScaleType.FIT_XY);
		} else {
			setScaleType(ImageView.ScaleType.FIT_CENTER);
		}
		
	}


	public String getlink() {
		return link;
	}

	public void init(String im, int slptime, String link, int effect,int w,int h) {
		this.link = link;
		this.imgs = im.split("\\|");
		ttal = imgs.length;
		if (slptime <= 0) {
			slptime = 5;
		}
		this.slptime = slptime;
		this.effects = effect;
		this.iw = w;
		this.ih = h;
		tos();
	}

	public void tonext() {
		if (ttal > 1) {
			gotime++;
			if (gotime >= slptime) {
				gotime = 0;
				cur++;
				if (cur >= ttal) {
					cur = 0;
				}
				tos();
			}
		}
	}

	public void recy() {
		try {
			if (bitmap != null) {
				if (!bitmap.isRecycled()) {
					bitmap.recycle();
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		bitmap = null;
	}

	public void tos() {
		String url;
		// FileInputStream fis = null;
		String temdir;
		if (Constant.offshow) {
			temdir = Constant.offDir + Constant.mfile + File.separator;
		} else {
			temdir = Constant.fileDir + Constant.mfile + File.separator;
		}
		// if (Constant.changePlaysImage) {
		// Constant.changePlaysImage = false;
		// nowTime = Constant.nowTime;
		// int i = 0;
		// for (i = 0; nowTime > 0; i++) {
		// nowTime -= slptime;
		// }
		//
		// imageTime = nowTime;
		// try {
		// indexPlay = i % imgs.length;
		// } catch (Exception e) {
		// }
		//
		// if (imageTime < 0) {
		// indexPlay = indexPlay - 1;
		// if (indexPlay < 0) {
		// indexPlay = imgs.length - 1;
		// }
		// imageTime = imageTime + slptime;
		// }
		// cur = indexPlay;
		// gotime = imageTime;
		// }

		url = temdir + imgs[cur].trim();
		recy();
		try {
			if (effects < 5) {
				Animation animation = AnimationUtils.loadAnimation(ct,
						Constant.effects[effects]);
				startAnimation(animation);

			} else if (effects == 5) {
				count++;
				Animation animation = AnimationUtils.loadAnimation(ct,
						Constant.effects[count % Constant.effects.length]);
				startAnimation(animation);
			}
			// fis = new FileInputStream(url);
			// bitmap = BitmapFactory.decodeStream(fis);
			bitmap = BitmapTool.getBitmap(url, iw, ih);
			setImageBitmap(bitmap);
		} catch (Exception e) {

		}
	}

	public Bitmap getBitmap() {
		Bitmap bm = null;
		String url;
		String temdir;
		if (Constant.offshow) {
			temdir = Constant.offDir + Constant.mfile + File.separator;
		} else {
			temdir = Constant.fileDir + Constant.mfile + File.separator;
		}
		url = temdir + imgs[cur].trim();
		try {
			bm = BitmapTool.getBitmap(url, iw, ih);
		} catch (Exception e) {
		}

		return bm;
	}

	@Override
	protected void onDraw(Canvas arg0) {
		try {
			super.onDraw(arg0);
		} catch (Exception e) {
			tos();
		}

	}

	public int getAllImgTime() {
		int count = 0;
		int imgTime = imgs.length * slptime;
		count = Constant.syncTime % imgTime;
		return count;

	}

	public void initGotime() {

		nowTime = getAllImgTime();
		int i = 0;
		for (i = 0; nowTime > slptime; i++) {
			nowTime -= slptime;
		}

		imageTime = nowTime;
		try {
			indexPlay = i % imgs.length;
		} catch (Exception e) {
		}

		// if (imageTime < 0) {
		// indexPlay = indexPlay - 1;
		// if (indexPlay < 0) {
		// indexPlay = imgs.length - 1;
		// }
		// imageTime = imageTime + slptime;
		// }
		if (gotime != imageTime) {
			gotime = imageTime;
			if (cur != indexPlay) {
				cur = indexPlay;
				tos();
			}
		}

	}

}
