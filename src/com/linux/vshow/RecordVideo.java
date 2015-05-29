package com.linux.vshow;

import java.io.IOException;
import android.content.Context;
import android.media.MediaRecorder;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class RecordVideo extends SurfaceView {

	private MediaRecorder recorder;
	public SurfaceHolder surfaceHolder;

	public RecordVideo(Context context) {
		super(context);
		surfaceHolder = getHolder();
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public void startRecorder(String filename) {
		try {
			if (recorder == null) {
				recorder = new MediaRecorder();
			}
			recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA); // 从照相机采集视频
			recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			recorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);// 设置视频编码方式
			recorder.setVideoSize(640, 480);
			recorder.setVideoFrameRate(4);
			recorder.setPreviewDisplay(surfaceHolder.getSurface());
			recorder.setOutputFile(filename);// 保存路径
			recorder.prepare();
			recorder.start();
		} catch (IOException e) {

		}
	}

	public void stopRecorder() {
		if (recorder != null) {
			try {
				recorder.stop();
				recorder.reset();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		recorder = null;
	}

	public void rel() {
		if (recorder != null) {
			try {
				recorder.release();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}
}