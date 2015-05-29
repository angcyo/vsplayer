package com.zckj.data;


/**
 * Created by angcyo on 2015-03-31 031.
 */
public interface OnWifiConnect {
    void onConnect(String SSID, String password, int security);
    void onConnectStaticIp(String SSID, String password, int security, WifiStaticIpNode node);
}
//修改于:2015年5月15日,星期五
