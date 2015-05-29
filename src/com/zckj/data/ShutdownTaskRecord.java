package com.zckj.data;

import com.orm.SugarRecord;

/**
 * Created by angcyo on 2015-03-25 025.
 */
public class ShutdownTaskRecord extends SugarRecord {
    public int hour;//时
    public  int minute;//分
    public  int taskType;//类型,关机还是开机
    public  int period;//周期 0x01111111
    public  int state;//任务状态,禁用,或者开启
    public  String remark;//备注

    public ShutdownTaskRecord() {
    }

    public ShutdownTaskRecord(int hour, int minute, int taskType, int period, int state, String remark) {
        this.hour = hour;
        this.minute = minute;
        this.taskType = taskType;
        this.period = period;
        this.state = state;
        this.remark = remark;
    }
}
//修改于:2015年5月15日,星期五
