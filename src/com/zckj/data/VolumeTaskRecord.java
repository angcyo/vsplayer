package com.zckj.data;

import com.orm.SugarRecord;

/**
 * Created by angcyo on 2015-03-24 024.
 */
public class VolumeTaskRecord extends SugarRecord<VolumeTaskRecord> {
    public int hour;//时
    public int minute;//分
    public int volume;//音量
    public int period;//周期
    public int state;//任务状态,禁用,或者开启
    public String remark;//备注

    public VolumeTaskRecord() {
    }

    public VolumeTaskRecord(int hour, int minute, int volume, int period, int state, String remark) {
        this.hour = hour;
        this.minute = minute;
        this.volume = volume;
        this.period = period;
        this.state = state;
        this.remark = remark;
    }
}
//修改于:2015年5月15日,星期五
