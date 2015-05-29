package com.zckj.data;

/**
 * Created by angcyo on 2015-04-02 002.
 */
public class WifiStaticIpNode {
    public String ip;
    public String gateway;
    public String dns1;
    public String dns2;

    public WifiStaticIpNode(String ip, String gateway, String dns1, String dns2) {
        this.ip = ip;
        this.gateway = gateway;
        this.dns1 = dns1;
        this.dns2 = dns2;
    }
}
//修改于:2015年5月15日,星期五
