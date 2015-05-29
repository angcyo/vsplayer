package com.linux.vshow;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.widget.ImageView;

public class ImageSee extends ImageView {

	private String[] imgs;
	private int slptime = 0;
	private int gotime = 0;
	private int cur = 0;
	private int ttal = 0;
	private Bitmap bitmap = null;

	public ImageSee(Context arg0) {
		super(arg0);
		setBackgroundColor(0);
		if (Constant.scale == 0) {
			setScaleType(ImageView.ScaleType.FIT_XY);
		} else {
			setScaleType(ImageView.ScaleType.FIT_CENTER);
		}
	}

	public void init(String im, int slptime) {
		this.imgs = im.split("\\|");
		ttal = imgs.length;
		if (slptime <= 0) {
			slptime = 5;
		}
		this.slptime = slptime;
		tos();
	}

	public void fre() {
		gotime++;
		if (gotime >= slptime) {
			gotime = 0;
			cur++;
			if (cur >= ttal) {
				cur = 0;
				if (Constant.avl.size() > 1) {
					Constant.avlbian = true;
					return;
				}
			}
			tos();
		}
	}

	public void recy() {
		if (bitmap != null) {
			try {
				if (!bitmap.isRecycled()) {
					bitmap.recycle();
				}

			} catch (Exception e) {
				// TODO: handle exception
			}
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
		url = temdir + imgs[cur].trim();
		recy();
		try {
			// fis = new FileInputStream(url);
			// bitmap = BitmapFactory.decodeStream(fis);
			bitmap = BitmapTool.getBitmap(url, Constant.width, Constant.height);
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
			bm = BitmapTool.getBitmap(url, Constant.width, Constant.height);
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
}
