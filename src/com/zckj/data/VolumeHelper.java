package com.zckj.data;

import java.util.List;

/**
 * Created by angcyo on 2015-03-25 025.
 */
public class VolumeHelper {

    public static void save(final int hour, final int minute, final int volume, final int period, final int state, final String remark) {
        new VolumeTaskRecord(hour, minute, volume, period, state, remark).save();
    }

    public static void delete(final long id) {
        VolumeTaskRecord.findById(VolumeTaskRecord.class, id).delete();
    }

    public static List<VolumeTaskRecord> getAll() {
        return VolumeTaskRecord.listAll(VolumeTaskRecord.class);
    }

    public static VolumeTaskRecord get(long id) {
        return VolumeTaskRecord.findById(VolumeTaskRecord.class, id);
    }
}
//修改于:2015年5月15日,星期五
