package com.linux.vshow;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android_serialport_api.SerialPort;

public class Console {

	protected SerialPort mSerialPort;
	protected OutputStream mOutputStream;
	private InputStream mInputStream;
	private ReadThread mReadThread;
	public static String msgString = "";

	public Console(String device, int baud) {
		try {
			mSerialPort = new SerialPort(new File(device), baud, 0);
			mOutputStream = mSerialPort.getOutputStream();
			mInputStream = mSerialPort.getInputStream();
		} catch (Exception e) {
		}
	}

	public void send(String text) {
		try {
			mOutputStream.write(text.getBytes("GB2312"));
		} catch (IOException e) {
		}
	}

	public void send(byte[] bytes, int begin, int end) {
		try {
			mOutputStream.write(bytes, begin, end);
		} catch (IOException e) {
		}
	}

	public void receive() {
		mReadThread = new ReadThread();
		mReadThread.start();

	}

	public void closeSerialPort() {
		if (mSerialPort != null) {
			mSerialPort.close();
			mSerialPort = null;
		}
		if (mReadThread != null)
			mReadThread.interrupt();
		mSerialPort = null;
	}

	private class ReadThread extends Thread {
		@Override
		public void run() {
			super.run();
			while (!isInterrupted()) {
				int size;
				try {
					byte[] buffer = new byte[64];
					if (mInputStream == null) {
						return;
					}
					size = mInputStream.read(buffer);
					if (size > 0) {
						msgString = new String(buffer, 0, size);
					}
				} catch (IOException e) {
					return;
				}
			}
		}
	}

}
