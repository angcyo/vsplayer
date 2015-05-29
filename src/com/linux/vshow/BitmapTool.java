package com.linux.vshow;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Rect;


public class BitmapTool {

	public static final int UNCONSTRAINED = -1;

	public static Options getOptions(String path) {
		Options options = new Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		return options;
	}

	private static Bitmap getBitmapByPath(String path, Options options,
			int screenWidth, int screenHeight) throws FileNotFoundException {
		File file = new File(path);
		if (!file.exists()) {
			throw new FileNotFoundException();
		}
		FileInputStream inputStream = null;
		inputStream = new FileInputStream(file);
		if (options != null) {
			options.inPreferredConfig = Bitmap.Config.RGB_565;  
			options.inPurgeable = true; 
			options.inInputShareable = true; 
			Rect r = getScreenRegion(screenWidth, screenHeight);
			int w = r.width();
			int h = r.height();
			int maxSize = w > h ? w : h;
			int inSimpleSize = computeSampleSize(options, maxSize, w * h);
			options.inSampleSize = inSimpleSize;
			options.inJustDecodeBounds = false;
		}
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(inputStream, null, options);
		    } catch (OutOfMemoryError err) {
		    }

		try {
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	private static Rect getScreenRegion(int width, int height) {
		return new Rect(0, 0, width, height);
	}

	private static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);
		return initialSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == UNCONSTRAINED) ? 1 : (int) Math
				.ceil(Math.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == UNCONSTRAINED) ? 128 : (int) Math
				.min(Math.floor(w / minSideLength),
						Math.floor(h / minSideLength));
		if (upperBound < lowerBound) {
			return lowerBound;
		}
		if ((maxNumOfPixels == UNCONSTRAINED)
				&& (minSideLength == UNCONSTRAINED)) {
			return 1;
		} else if (minSideLength == UNCONSTRAINED) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	public static Bitmap getBitmap(String path, int screenWidth,
			int screenHeight) throws FileNotFoundException {
		return BitmapTool.getBitmapByPath(path, BitmapTool.getOptions(path),
				screenWidth, screenHeight);
	}

}
