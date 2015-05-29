package com.zckj.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.zckj.ui.ExSetContentFragment;

import java.io.File;


public class Setting {
	static final  String SHARED_FILE_NAME = "AppSetting";

	public static  final String KEY_RUN_BMP = "run_bmp";
	public static final String KEY_ROTATE = "rotate";

	public  static SharedPreferences getSP(Context context){
		SharedPreferences sp = context.getApplicationContext().getSharedPreferences(SHARED_FILE_NAME, Context.MODE_MULTI_PROCESS);
		return  sp;
	}

	public static String getStrValue(Context context, String key){
		return getSP(context).getString(key, null);
	}

	public  static  void  setStrValue(Context context, String key, String value){
		getSP(context).edit().putString(key, value).commit();
	}

	public static String getRunBmpPath(Context context) {
		return getSP(context).getString(KEY_RUN_BMP, null);
	}

	public static void setRunBmpPath(Context context, String value) {
		getSP(context).edit().putString(KEY_RUN_BMP, value).commit();
	}

	/**
	 * 获取文件扩展名
	 *
	 * @param file
	 * @return
	 */
	public static String getFileExtension(File file) {
		String fileName = file.getName();
		if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
			return fileName.substring(fileName.lastIndexOf(".") + 1);
		} else {
			return "";
		}
	}

	public static int getRunCount(Context context) {
		return getSP(context).getInt("run", 0);
	}

	public static void setRunCount(Context context, int count) {
		getSP(context).edit().putInt("run", count).commit();
	}

	public static int getRotate(Context context) {
		return getSP(context).getInt(KEY_ROTATE, ExSetContentFragment.ROTATE_0);
	}

	public static void setRotate(Context context, int value) {
		getSP(context).edit().putInt(KEY_ROTATE, value).commit();
	}
}
//修改于:2015年5月15日,星期五
