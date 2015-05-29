package com.softwinner.tv;

import softwinner.tv.TVDecoder;

import android.R.integer;
import android.content.Context;
import android.media.AudioManager;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CVBS extends SurfaceView {
	private TVDecoder tv;
	private int videoWidth;
	private int videoHeight;
	private int videox;
	private int videoy;
	private SurfaceHolder surfaceHolder;
	private AudioManager audioManager;

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int tempWidth = getDefaultSize(videoWidth, widthMeasureSpec);
		int tempHeight = getDefaultSize(videoHeight, heightMeasureSpec);
		setMeasuredDimension(tempWidth, tempHeight);
	}

	public CVBS(Context ct, int width, int height,int x,int y) {
		super(ct);
		try {
			tv = new TVDecoder();
			
		} catch (Exception e) {
		}
		videoWidth = width;
		videoHeight = height;
		videox = x;
		videoy = y;
		surfaceHolder = getHolder();
		surfaceHolder.addCallback(mSHCallback);
		surfaceHolder.setFixedSize(width, height);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		audioManager = (AudioManager) ct
				.getSystemService(Context.AUDIO_SERVICE);
		try {
			audioManager.setMode(2);
		} catch (Exception e) {
		}
	}

	public void closeTV() {
		if (tv != null) {
			try {
				tv.disconnect();
			} catch (Exception e) {
			}
			tv = null;
		}
		try {
			audioManager.setMode(0);
		} catch (Exception e) {
		}

	}

	SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback() {
		public void surfaceChanged(SurfaceHolder holder, int format, int w,
				int h) {
			try {
				tv.setSize(videox, videoy, videoWidth, videoHeight);
				tv.setPreviewDisplay(holder.getSurface());
				tv.startDecoder();
			} catch (Exception e) {
			}

		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			closeTV();
		}

		public void surfaceCreated(SurfaceHolder arg0) {
			tv.connect(1, 0, 1, 0);
		}
	};
}
