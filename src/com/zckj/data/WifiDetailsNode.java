package com.zckj.data;

/**
 * Created by angcyo on 2015-03-31 031.
 */
public class WifiDetailsNode {
    public int netId;//网络的ID,多用于删除网络
    public String ssid;//
    public String state;//
    public int linkSpeed;//连接速率 Mbps
    public String ip;
    public String mac;
    public String dns1;
    public String dns2;
    public String gateway;
    public int leaseDuration;
    public String netmask;
    public String serverAddress;

    public WifiDetailsNode(int netId, String ssid, String state, int linkSpeed,
                           String ip, String mac, String dns1, String dns2,
                           String gateway, int leaseDuration,
                           String netmask, String serverAddress) {
        this.netId = netId;
        this.ssid = ssid;
        this.state = state;
        this.linkSpeed = linkSpeed;
        this.ip = ip;
        this.mac = mac;
        this.dns1 = dns1;
        this.dns2 = dns2;
        this.gateway = gateway;
        this.leaseDuration = leaseDuration;
        this.netmask = netmask;
        this.serverAddress = serverAddress;
    }
}
//修改于:2015年5月15日,星期五
