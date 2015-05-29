package com.linux.vshow;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;

public class ShowCamera extends SurfaceView {

	public SurfaceHolder surfaceHolder;

	private Camera mCamera;

	public ShowCamera(Context context) {
		super(context);
		surfaceHolder = getHolder();
		surfaceHolder.addCallback(surfaceHolderCallback);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public void stop() {
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}

	}

	Callback surfaceHolderCallback = new Callback() {

		public void surfaceDestroyed(SurfaceHolder arg0) {
			if (mCamera != null) {
				try {
					mCamera.stopPreview();
					mCamera.release();
					mCamera = null;
				} catch (Exception e) {
				}
			}
		}

		public void surfaceCreated(SurfaceHolder arg0) {
			surfaceHolder = arg0;
			try {
				mCamera = Camera.open();
				mCamera.setPreviewDisplay(surfaceHolder);
			} catch (Exception exception) {
				try {
					mCamera.release();
					mCamera = null;
				} catch (Exception e) {
				}

			}
		}

		public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2,
				int arg3) {
			surfaceHolder = arg0;
			if (mCamera != null) {
				try {
					Camera.Parameters parameters = mCamera.getParameters();
					mCamera.setParameters(parameters);
					mCamera.stopPreview();
					mCamera.startPreview();
				} catch (Exception e) {
				}

			}
		}
	};
}