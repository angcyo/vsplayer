package com.zckj.data;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by angcyo on 2015-03-19 019.
 */
public class ApkUtil {
    /**
     * 返回app的版本名称.
     *
     * @param context
     * @return
     */
    public static String getAppVersionName(Context context) {
        // 获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(),
                    0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = packInfo.versionName;
        // Log.i("版本名称:", version);
        return version;
    }

    /**
     * 返回app的版本代码.
     *
     * @param context
     * @return
     */
    public static int getAppVersionCode(Context context) {
        // 获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(),
                    0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int code = packInfo.versionCode;
        // Log.i("版本名称:", version);
        return code;
    }
}
//修改于:2015年5月15日,星期五
