package com.linux.vshow;

import java.io.FileOutputStream;

import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;

public class SeePic {

	private Camera mCamera;
	private String filename;

	public SeePic() {
		try {
			mCamera = Camera.open();
			Camera.Parameters params = mCamera.getParameters();
			params.setJpegQuality(100);
			mCamera.setParameters(params);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	// 拍照方法
	public void takePicture(String filename) {
		try {
			this.filename = filename;
			mCamera.takePicture(null, null, mPicture);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	// PictureCallback回调函数实现
	private PictureCallback mPicture = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			try {
				FileOutputStream fos = new FileOutputStream(filename);
				fos.write(data);
				fos.close();
			} catch (Exception e) {

			}
		}
	};

	// 释放Camera对象
	public void releaseCamera() {
		if (mCamera != null) {
			try {
				mCamera.release();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		mCamera = null;
	}
}
