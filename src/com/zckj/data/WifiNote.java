package com.zckj.data;

import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;

import java.util.List;

/**
 * Created by angcyo on 2015-03-20 020.
 */
public class WifiNote {
    public int wifiState;//wifi 的状态
    public List<ScanResult> wifiList;//wifi 列表
    public String curWifiSSID;// 已经连接上的Wifi SSID
    public SupplicantState supplicantState;//wifi 连接状态,如:正在连接,已连接,等待分配ip地址,等

    public WifiNote() {
//        this.wifiState = 1;
//        this.wifiList = new ArrayList<>();
    }
}
//修改于:2015年5月15日,星期五
