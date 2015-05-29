package com.zckj.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.widget.Button;
import android.widget.CheckBox;

import com.zckj.data.OnTaskAdd;

/**
 * Created by angcyo on 2015-03-25 025.
 */
public class BaseTaskDialog extends Dialog {

    public static final int WEEK1 = 0x00000001;
    public static final int WEEK2 = 0x00000010;
    public static final int WEEK3 = 0x00000100;
    public static final int WEEK4 = 0x00001000;
    public static final int WEEK5 = 0x00010000;
    public static final int WEEK6 = 0x00100000;
    public static final int WEEK7 = 0x01000000;
    public static final int WEEK_ALL = 0x01111111;
    public static final int STATE_ENABLED = 1;//状态,启用
    public static final int STATE_DISABLE = 0;//禁用

    public static final int TASK_TYPE_START = 1;//开机任务类型
    public static final int TASK_TYPE_CLOSE = 0;//关机任务类型

    protected OnTaskAdd taskAddListener;//点击OK,或者OK继续之后的回调

    protected Button btHourAdd, btHourMinus;//时 加减
    protected Button btMinuteAdd, btMinuteMinus;//分 加减
    protected Button btVolumeAdd, btVolumeMinus;//音量 加减
    protected Button btOkAndCon, btOk, btCancel;//添加继续, 添加, 取消 按钮
    protected CheckBox cb1, cb2, cb3, cb4, cb5, cb6, cb7;//周期-至日

    protected TimePicker timePicker;//
    protected DiscreteSeekBar discreteSeekBar;//

    protected int period;//周期

    protected Handler handler = new Handler();

    public BaseTaskDialog(Context context) {
        super(context);
    }

    public BaseTaskDialog(Context context, int theme) {
        super(context, theme);
    }

    protected BaseTaskDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    protected int getHour() {
        return timePicker.getH();
    }

    protected int getMinute() {
        return timePicker.getM();
    }

    protected int getVolumeValue() {
        return discreteSeekBar.getProgress();
    }

    //获取周期
    protected int getPeriod() {
        period = 0;
        if (cb1.isChecked()) {
            period |= WEEK1;
        }
        if (cb2.isChecked()) {
            period |= WEEK2;
        }
        if (cb3.isChecked()) {
            period |= WEEK3;
        }
        if (cb4.isChecked()) {
            period |= WEEK4;
        }
        if (cb5.isChecked()) {
            period |= WEEK5;
        }
        if (cb6.isChecked()) {
            period |= WEEK6;
        }
        if (cb7.isChecked()) {
            period |= WEEK7;
        }
        return period;
    }
}
//修改于:2015年5月15日,星期五
