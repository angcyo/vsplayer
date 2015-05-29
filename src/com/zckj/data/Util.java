package com.zckj.data;

import android.content.Context;
import android.widget.Toast;

import com.zckj.setting.BaseApplication;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by angcyo on 2015-03-25 025.
 */
public class Util {
    /**
     * 显示Toast消息
     *
     * @param msg 需要显示的内容
     */
    public static void showPostMsg(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static void showPostMsg(String msg) {
        Toast.makeText(BaseApplication.App, msg, Toast.LENGTH_LONG).show();
    }

    /**
     * 去除字符串左右的字符
     */
    public static String trimMarks(String des) {
        return trimMarks(des, 1);
    }

    /**
     * 去除字符串左右指定个数的字符
     */
    public static String trimMarks(String des, int count) {
        if (des == null || count < 0 || des.length() < count + 1) {
            return des;
        }
        return des.substring(count, des.length() - count);
    }

    /**
     * 判断字符串是否为空
     */
    public static boolean isEmpty(String str) {
        if (str == null || str.length() == 0)
            return true;
        return false;
    }

    /**
     * 返回现在的时间,不包含日期
     */
    public static String getNowTime() {
        return getNowTime("HH:mm:ss");
    }

    public static String getNowTime(String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(new Date());
    }

}

//修改于:2015年5月15日,星期五
