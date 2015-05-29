package com.zckj.data;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

/**
 * Created by angcyo on 2015-04-01 001.
 */
public class WifiUtil {
    private WifiManager mWifiManager;

    public WifiUtil(WifiManager mWifiManager) {
        this.mWifiManager = mWifiManager;
    }

    /**
     * PING remote host
     *
     * @return
     */
    public static boolean ping(String ip, long time) {
        boolean isReach = false;
        try {
            String cmd = "ping -c 1 " + " -w " + time + " " + ip;
            Process p = Runtime.getRuntime().exec(cmd);
            int status = p.waitFor();
            if (status == 0) {
                isReach = true;
            }
            Logger.e(">>>>>>>>>cmd:" + cmd + " result:" + status);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isReach;
    }

    //将IP转换成String
    public static String getIPString(int nIp) {
        String IP;
        if (0 == nIp) {
            return "0.0.0.0";
        } else {
            IP = (nIp & 0xFF) + "." + ((nIp >> 8) & 0xff) + "." + ((nIp >> 16) & 0xff) + "." + ((nIp >> 24) & 0xFF);
        }
        return IP;
    }

    /**
     * Convert a IPv4 address from an InetAddress to an integer
     *
     * @param inetAddr is an InetAddress corresponding to the IPv4 address
     * @return the IP address as an integer in network byte order
     */
    public static int inetAddressToInt(InetAddress inetAddr)
            throws IllegalArgumentException {
        byte[] addr = inetAddr.getAddress();
        if (addr.length != 4) {
            throw new IllegalArgumentException("Not an IPv4 address");
        }
        return ((addr[3] & 0xff) << 24) | ((addr[2] & 0xff) << 16) |
                ((addr[1] & 0xff) << 8) | (addr[0] & 0xff);
    }

    /**
     * 设置wifi,编辑静态IP
     *
     * @param SSID
     * @param pwd
     * @param ip
     * @throws Exception
     */
    public void saveStaticWifiConfig(String SSID, String pwd, String ip, int networkPrefixLength) throws Exception {
        InetAddress intetAddress = InetAddress.getByName(ip);//ip 地址
        int intIp = inetAddressToInt(intetAddress);//ip 转换成int类型
        WifiConfiguration historyWifiConfig = getHistoryWifiConfig(SSID);//根据SSID,获取保存的config
        if (historyWifiConfig == null) {
            historyWifiConfig = createComWifiConfig(SSID, pwd);
        } else {
            if (!TextUtils.isEmpty(pwd)) {
                historyWifiConfig.preSharedKey = "\"" + pwd + "\"";//设置网络的密码
            }
        }

        String dns = (intIp & 0xFF) + "." + ((intIp >> 8) & 0xFF) + "." + ((intIp >> 16) & 0xFF) + ".1";
        setIpAssignment("STATIC", historyWifiConfig); //"STATIC" or "DHCP" for dynamic setting
        setIpAddress(intetAddress, networkPrefixLength, historyWifiConfig);
        setGateway(InetAddress.getByName(dns), historyWifiConfig);
        setDNS(InetAddress.getByName(dns), historyWifiConfig);

        mWifiManager.removeNetwork(historyWifiConfig.networkId);
        int netId = mWifiManager.addNetwork(historyWifiConfig);
        mWifiManager.enableNetwork(netId, true);
        mWifiManager.updateNetwork(historyWifiConfig); //apply the setting
        mWifiManager.saveConfiguration(); //Save it//后来添加
//        SmartHomePreference.setProperty(WifiActivity.KEY_WIFI_PRIORITY, sr.SSID);

//        mWifiManager.startScan();
    }

    /**
     * Edit static wifi config.
     *
     * @param SSID         the sSID
     * @param pwd          the pwd
     * @param ip           the ip
     * @param gateway      the gateway
     * @param prefixLength the prefix length
     * @param dns         the dns
     * @throws Exception the exception
     */
    public void editStaticWifiConfig(String SSID, String pwd,
                                     String ip, String gateway, int prefixLength, String dns) throws Exception {
        WifiConfiguration historyWifiConfig = getHistoryWifiConfig(SSID);
        if (historyWifiConfig == null) {//如果不存在,创建新的配置信息
            historyWifiConfig = createComWifiConfig(SSID, pwd);//
            int netId = mWifiManager.addNetwork(historyWifiConfig);
            mWifiManager.enableNetwork(netId, true);
        }
        setIpAssignment("STATIC", historyWifiConfig); //"STATIC" or "DHCP" for dynamic setting
        setIpAddress(InetAddress.getByName(ip), prefixLength, historyWifiConfig);//设置IP
        setGateway(InetAddress.getByName(gateway), historyWifiConfig);//设置网关
        setDNS(InetAddress.getByName(dns), historyWifiConfig);//设置DNS, 为啥没有DNS1,DNS2之分?  设置方法都一样,调用2次就行
        //setDNS(InetAddress.getByName(dns2), historyWifiConfig);//只有最后一次调用的DNS才会生效
        mWifiManager.updateNetwork(historyWifiConfig); //apply the setting
        mWifiManager.saveConfiguration();//保存一下
    }

    //没有设置DNS 貌似会崩溃
    public void editStaticWifiConfig(String SSID, String pwd,
                                     String ip, String gateway, int prefixLength) throws Exception {
        WifiConfiguration historyWifiConfig = getHistoryWifiConfig(SSID);
        if (historyWifiConfig == null) {//如果不存在,创建新的配置信息
            historyWifiConfig = createComWifiConfig(SSID, pwd);//
            int netId = mWifiManager.addNetwork(historyWifiConfig);
            mWifiManager.enableNetwork(netId, true);
        }
        setIpAssignment("STATIC", historyWifiConfig); //"STATIC" or "DHCP" for dynamic setting
        setIpAddress(InetAddress.getByName(ip), prefixLength, historyWifiConfig);//设置IP
        setGateway(InetAddress.getByName(gateway), historyWifiConfig);//设置网关
        mWifiManager.updateNetwork(historyWifiConfig); //apply the setting
        mWifiManager.saveConfiguration();//保存一下
    }


    //DHCP ip设置
    public void editDhcpWifiConfig(final ScanResult sr, String pwd) throws Exception {
        WifiConfiguration historyWifiConfig = getHistoryWifiConfig(sr.SSID);//获取指定SSID的配置

        if (historyWifiConfig == null) {
            historyWifiConfig = createComWifiConfig(sr.SSID, pwd);
            int netId = mWifiManager.addNetwork(historyWifiConfig);
            mWifiManager.enableNetwork(netId, true);
        }
        setIpAssignment("DHCP", historyWifiConfig); //"STATIC" or "DHCP" for dynamic setting
        mWifiManager.updateNetwork(historyWifiConfig); //apply the setting
    }

    /**
     * 新建wifi配置项,,,只支持WPA加密类型
     *
     * @param ssid
     * @param pwd
     * @return
     */
    public WifiConfiguration createComWifiConfig(String ssid, String pwd) {
        WifiConfiguration wc = new WifiConfiguration();
        wc.SSID = "\"" + ssid + "\"";                   //配置wifi的SSID，即该热点的名称，如：TP-link_xxx
        wc.preSharedKey = "\"" + pwd + "\"";            //该热点的密码
        wc.hiddenSSID = true;
        wc.status = WifiConfiguration.Status.ENABLED;
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        return wc;
    }

    /**
     * 查找已经设置好的Wifi 配置信息
     *
     * @param ssid
     * @return
     */
    public WifiConfiguration getHistoryWifiConfig(String ssid) {
        List<WifiConfiguration> localList = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration wc : localList) {
            if (("\"" + ssid + "\"").equals(wc.SSID)) {
                return wc;
            }
            mWifiManager.disableNetwork(wc.networkId);
        }
        return null;
    }

    public static void setIpAssignment(String assign, WifiConfiguration wifiConf)
            throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
        setEnumField(wifiConf, assign, "ipAssignment");
    }

    public static void setIpAddress(InetAddress addr, int prefixLength, WifiConfiguration wifiConf)
            throws SecurityException, IllegalArgumentException,
            NoSuchFieldException, IllegalAccessException, NoSuchMethodException, ClassNotFoundException, InstantiationException, InvocationTargetException {
        Object linkProperties = getField(wifiConf, "linkProperties");
        if (linkProperties == null)
            return;
        Class laClass = Class.forName("android.net.LinkAddress");
        Constructor laConstructor = laClass.getConstructor(new Class[]{InetAddress.class, int.class});
        Object linkAddress = laConstructor.newInstance(addr, prefixLength);
        ArrayList mLinkAddresses = (ArrayList) getDeclaredField(linkProperties, "mLinkAddresses");
        mLinkAddresses.clear();
        mLinkAddresses.add(linkAddress);
    }

    public static void setGateway(InetAddress gateway, WifiConfiguration wifiConf) throws SecurityException, IllegalArgumentException,
            NoSuchFieldException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InstantiationException, InvocationTargetException {
        Object linkProperties = getField(wifiConf, "linkProperties");
        if (linkProperties == null)
            return;
        Class routeInfoClass = Class.forName("android.net.RouteInfo");
        Constructor routeInfoConstructor = routeInfoClass.getConstructor(new Class[]{InetAddress.class});
        Object routeInfo = routeInfoConstructor.newInstance(gateway);
        ArrayList mRoutes = (ArrayList) getDeclaredField(linkProperties, "mRoutes");
        mRoutes.clear();
        mRoutes.add(routeInfo);
    }

    public static void setDNS(InetAddress dns, WifiConfiguration wifiConf)
            throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
        Object linkProperties = getField(wifiConf, "linkProperties");
        if (linkProperties == null)
            return;
        ArrayList<InetAddress> mDnses = (ArrayList<InetAddress>) getDeclaredField(linkProperties, "mDnses");
        mDnses.clear(); // or add a new dns address , here I just want to replace DNS1
        mDnses.add(dns);
    }

    public static String getNetworkPrefixLength(WifiConfiguration wifiConf) {
        String address = "";
        try {
            Object linkProperties = getField(wifiConf, "linkProperties");
            if (linkProperties == null)
                return null;

            if (linkProperties != null) {
                ArrayList mLinkAddresses = (ArrayList) getDeclaredField(linkProperties, "mLinkAddresses");
                if (mLinkAddresses != null && mLinkAddresses.size() > 0) {
                    Object linkAddressObj = mLinkAddresses.get(0);
                    address = linkAddressObj.getClass().getMethod("getNetworkPrefixLength", new Class[]{}).invoke(linkAddressObj,new Object()) + "";//最后一个参数null
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return address;
    }

    public static InetAddress getIpAddress(WifiConfiguration wifiConf) {
        InetAddress address = null;
        try {
            Object linkProperties = getField(wifiConf, "linkProperties");
            if (linkProperties == null)
                return null;

            if (linkProperties != null) {
                ArrayList mLinkAddresses = (ArrayList) getDeclaredField(linkProperties, "mLinkAddresses");
                if (mLinkAddresses != null && mLinkAddresses.size() > 0) {
                    Object linkAddressObj = mLinkAddresses.get(0);
                    address = (InetAddress) linkAddressObj.getClass().getMethod("getAddress", new Class[]{}).invoke(linkAddressObj,new Object());//null 最有一个参数
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return address;
    }

    public static InetAddress getGateway(WifiConfiguration wifiConf) {
        InetAddress address = null;
        try {
            Object linkProperties = getField(wifiConf, "linkProperties");

            if (linkProperties != null) {
                ArrayList mRoutes = (ArrayList) getDeclaredField(linkProperties, "mRoutes");
                if (mRoutes != null && mRoutes.size() > 0) {
                    Object linkAddressObj = mRoutes.get(0);
                    address = (InetAddress) linkAddressObj.getClass().getMethod("getGateway", new Class[]{}).invoke(linkAddressObj, new Object());//null
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return address;
    }

    public static InetAddress getDNS(WifiConfiguration wifiConf) {
        InetAddress address = null;
        try {
            Object linkProperties = getField(wifiConf, "linkProperties");

            if (linkProperties != null) {
                ArrayList<InetAddress> mDnses = (ArrayList<InetAddress>) getDeclaredField(linkProperties, "mDnses");
                if (mDnses != null && mDnses.size() > 0) {
                    address = (InetAddress) mDnses.get(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return address;
    }

    public static Object getField(Object obj, String name)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getField(name);
        Object out = f.get(obj);
        return out;
    }

    public static Object getDeclaredField(Object obj, String name) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getDeclaredField(name);
        f.setAccessible(true);
        Object out = f.get(obj);
        return out;
    }

    public static void setEnumField(Object obj, String value, String name) throws SecurityException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getField(name);//通过反射,获取字段
        //f.setAccessible(true);//需要设置访问权限么? 查看源码,发现属性是public类型,只不过隐藏了字段
        f.set(obj, Enum.valueOf((Class<Enum>) f.getType(), value));
    }

//    public void showEditWifi(final ScanResult sr) {
//        LayoutInflater factory = LayoutInflater.from(this);
//        final View textEntryView = factory.inflate(R.layout.dialog_wifi_setting, null);
//        new AlertDialog.Builder(this).setIconAttribute(android.R.attr.dialogIcon).setTitle(sr.SSID).setView(textEntryView).setCancelable(false)
//                .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                        try {
//                            EditText edtWifiPwd = (EditText) textEntryView.findViewById(R.id.edt_wifi_pwd);
//                            EditText edtStaticIp = (EditText) textEntryView.findViewById(R.id.edt_static_ip);
//                            EditText edtStaticGateway = (EditText) textEntryView.findViewById(R.id.edt_static_gateway);
//                            EditText edtStaticNetmask = (EditText) textEntryView.findViewById(R.id.edt_static_netmask);
//                            EditText edtStaticDns = (EditText) textEntryView.findViewById(R.id.edt_static_dns);
//
//                            String wifiPwd = edtWifiPwd.getText().toString().trim();
//                            String ip = edtStaticIp.getText().toString().trim();
//                            String gateway = edtStaticGateway.getText().toString().trim();
//                            String prefixLength = edtStaticNetmask.getText().toString().trim();
//                            String dns = edtStaticDns.getText().toString().trim();
//
//                            saveStaticWifiConfig(sr, wifiPwd, ip, Integer.parseInt(prefixLength));
//                        } catch (IllegalArgumentException e) {
//                            promptMessage(getString(R.string.system_wifi_ip_error));
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            promptMessage(getString(R.string.system_wifi_setting_error));
//                        }
//                    }
//                }).setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int whichButton) {
//                dialog.cancel();
//            }
//        }).create().show();
//
//        try {
//            WifiConfiguration historyWifiConfig = getHistoryWifiConfig(sr.SSID);
//            EditText edtWifiPwd = (EditText) textEntryView.findViewById(R.id.edt_wifi_pwd);
//            EditText edtStaticIp = (EditText) textEntryView.findViewById(R.id.edt_static_ip);
//            EditText edtStaticGateway = (EditText) textEntryView.findViewById(R.id.edt_static_gateway);
//            EditText edtStaticNetmask = (EditText) textEntryView.findViewById(R.id.edt_static_netmask);
//            EditText edtStaticDns = (EditText) textEntryView.findViewById(R.id.edt_static_dns);
//
//            if (historyWifiConfig != null) {
//                InetAddress address = getIpAddress(historyWifiConfig);
//                if (address != null) {
//                    edtStaticIp.setText(address.getHostAddress());
//                    address = null;
//                }
//                address = getGateway(historyWifiConfig);
//                if (address != null) {
//                    edtStaticGateway.setText(address.getHostAddress());
//                    address = null;
//                }
//                address = getDNS(historyWifiConfig);
//                if (address != null) {
//                    edtStaticDns.setText(address.getHostAddress());
//                    address = null;
//                }
//                edtStaticNetmask.setText(getNetworkPrefixLength(historyWifiConfig));
//
//            }
//
//            if (TextUtils.isEmpty(edtStaticIp.getText().toString().trim())) {
//                String ipString = SmartHomePreference.getStringProperty(KEY_WIFI_STATIC_IP);
//                int intIp = inetAddressToInt(InetAddress.getByName(ipString));
//                String dns = (intIp & 0xFF) + "." + ((intIp >> 8) & 0xFF) + "." + ((intIp >> 16) & 0xFF) + ".1";
//
//                edtStaticIp.setText(ipString);
//                edtStaticNetmask.setText("24");
//                edtStaticGateway.setText(dns);
//                edtStaticDns.setText(dns);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
}
//修改于:2015年5月15日,星期五
