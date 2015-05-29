package com.zckj.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.zckj.data.AsyncScanWifi;
import com.zckj.data.Logger;
import com.zckj.data.NetWifiListAdapter;
import com.zckj.data.OnWifi;
import com.zckj.data.OnWifiConnect;
import com.zckj.data.Util;
import com.zckj.data.WifiAdminEx;
import com.zckj.data.WifiConnectInfo;
import com.zckj.data.WifiDetailsNode;
import com.zckj.data.WifiNote;
import com.zckj.data.WifiStaticIpNode;
import com.zckj.data.WifiUtil;
import com.zckj.data.XmlSetting;
import com.linux.vshow.R;

public class NetSetContentFragment extends BaseFragment implements OnWifiConnect, OnWifi {

    public static final int STYLE_NET_WIFI = 0x002001;
    public static final int STYLE_NET_WAN = 0x002002;

    public static final int MSG_UPDATE_WIFI_LIST = 0x010001;//更新wifi列表数据的消息

    //    public WifiAdminEx wifiAdmin;
    ListView listWifiInfo;
    ProgressBar wifiLoadBar;
    NetWifiListAdapter wifiListAdapter;
    Dialog dialog;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case MSG_UPDATE_WIFI_LIST:
                    startWifiScan();
                    break;
                default:
                    break;
            }
        }
    };

    public NetSetContentFragment() {
        default_layout_style = STYLE_NET_WIFI;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        switch (style) {
            case STYLE_NET_WIFI:
                rootView = inflater.inflate(R.layout.layout_net_wifi, container, false);
                initWifiLayout();
                break;
            case STYLE_NET_WAN:
                rootView = inflater.inflate(R.layout.layout_net_wan, container, false);
                initWanLayout();
                break;
            default:
                style = STYLE_NET_WIFI;
                rootView = inflater.inflate(R.layout.layout_net_wifi, container, false);
                initWifiLayout();
                break;
        }
        return rootView;
    }

    /**
     * 初始化 wan 布局
     */
    void initWanLayout() {

    }

    /**
     * 初始化 wifi 布局
     */
    void initWifiLayout() {
        listWifiInfo = (ListView) rootView.findViewById(R.id.id_net_wifi_list);
        wifiLoadBar = (ProgressBar) rootView.findViewById(R.id.id_net_wifi_load_bar);
        wifiLoadBar.setVisibility(View.INVISIBLE);

        listWifiInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ScanResult result = (ScanResult) parent.getAdapter().getItem(position);
                connectWifi(result.SSID, WifiAdminEx.getSecurity(result));
//				Logger.e("SSid-" + result.SSID + " key-" + WifiAdminEx.getSecurity(result));
            }
        });

        initWifiLayoutData();
    }

    /**
     * 连接指定的WIFI, 如果wifi已连接,则显示详细信息
     */
    private void connectWifi(String ssid, int security) {
        WifiAdminEx wifiAdmin = new WifiAdminEx(context);
        Dialog dialog = null;
//        Logger.e("1  ssid-" + ssid + "  -" + wifiAdmin.getSSID());
        if (ssid.equalsIgnoreCase(Util.trimMarks(wifiAdmin.getSSID()))) {//如果WIFI已连接,显示详细信息
            WifiDetailsNode node = createWifiDetailsNode(wifiAdmin);
            dialog = new WifiDetailsDialog(context, R.style.CustomDialog, node, this);
            dialog.setContentView(R.layout.dialog_wifi_details);
            dialog.show();
        } else if (security == 0) {//如果WIFI是开放的
            onConnect(ssid, "", 0);//直接连接
        } else {
            WifiConnectInfo info = new WifiConnectInfo(ssid, security);
            dialog = new EnterPasswordDialog(context, R.style.CustomDialog, this, info);
            dialog.setContentView(R.layout.dialog_enter_password);
            dialog.show();
        }
    }

    private WifiDetailsNode createWifiDetailsNode(WifiAdminEx wifiAdmin) {
        WifiDetailsNode node = new WifiDetailsNode(wifiAdmin.getNetworkId(), Util.trimMarks(wifiAdmin.getSSID()), "已连接",
                wifiAdmin.getLinkSpeed(), wifiAdmin.getIPString(),
                wifiAdmin.getMacAddress(), wifiAdmin.getIPString(wifiAdmin.getDns1()),
                wifiAdmin.getIPString(wifiAdmin.getDns1()), wifiAdmin.getIPString(wifiAdmin.getGateway()),
                wifiAdmin.getLeaseDuration(), wifiAdmin.getIPString(wifiAdmin.getNetmask()),
                wifiAdmin.getIPString(wifiAdmin.getServerAddress())
        );
        return node;
    }

    void initWifiLayoutData() {
//		if (wifiAdmin.checkState() == 1)//如果wifi禁用了,就启用wifi
//			wifiAdmin.openWifi();

//		txWifiStateTip.setText(wifiAdmin.checkState() + "");
//		startWifiScan();
//		handler.sendEmptyMessageDelayed(MSG_UPDATE_WIFI_LIST, 1000);//延时发送
    }

    //开启任务,扫描wifi
    void startWifiScan() {
        final WifiAdminEx wifiAdmin = new WifiAdminEx(context);

        wifiLoadBar.setVisibility(View.VISIBLE);
        int state = wifiAdmin.checkState();
        if (state == 0 || state == 1) {
            if (dialog != null && dialog.isShowing())
                return;
            dialog = new AlertDialog.Builder(context)
                    .setTitle("警告")
                    .setMessage("WIFI已关闭,是否开启?\n开启需要几秒钟!")
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            cancelWifiScan();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cancelWifiScan();
                        }
                    })
                    .setPositiveButton("开启", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            wifiAdmin.openWifi();
                            handler.sendEmptyMessageDelayed(MSG_UPDATE_WIFI_LIST, 3000);//延时3秒发送消息,等待WIFI的开启需要一定时间
                        }
                    }).create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(true);
            dialog.show();
        } else {
            new AsyncScanWifi() {
                @Override
                public void onScanResults(WifiNote note) {
                    if (note != null && note.wifiList != null) {
                        if (wifiListAdapter == null) {
                            wifiListAdapter = new NetWifiListAdapter(context, note);
                            listWifiInfo.setAdapter(wifiListAdapter);
                        } else {
                            wifiListAdapter.setDataChanged(note);
                        }
                    } else {
                        listWifiInfo.setAdapter(null);
                        wifiListAdapter = null;
                    }
                    wifiLoadBar.setVisibility(View.INVISIBLE);
                    handler.sendEmptyMessageDelayed(MSG_UPDATE_WIFI_LIST, 3000);//每3秒更新一次

//                    Logger.e("IP:" + wifiAdmin.getIPString() + ":::MAC:::" + wifiAdmin.getBSSID());
                }
            }.execute(new WifiAdminEx(context));//尽量使用心得WifiAdminEx,避免出现BUG
        }
    }

    void cancelWifiScan() {
        handler.removeMessages(MSG_UPDATE_WIFI_LIST);
        wifiLoadBar.setVisibility(View.INVISIBLE);
        listWifiInfo.setAdapter(null);
        wifiListAdapter = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (style == STYLE_NET_WIFI) {
            handler.sendEmptyMessageDelayed(MSG_UPDATE_WIFI_LIST, 1000);//延时发送消息,,更新wifi列表
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (style == STYLE_NET_WIFI) {
            handler.removeMessages(MSG_UPDATE_WIFI_LIST);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onConnect(String SSID, String password, int security) {
        WifiAdminEx wifiAdmin = new WifiAdminEx(context);
        Util.showPostMsg("正在连接 " + SSID);
        WifiConfiguration configuration = wifiAdmin.createWifiInfo(SSID, password, security);
        wifiAdmin.addNetwork(configuration);

        XmlSetting.setXmlWifiPw(password);
        XmlSetting.setXmlWifiSsid(SSID);
    }

    @Override
    public void onConnectStaticIp(String SSID, String password, int security, WifiStaticIpNode node) {
        WifiAdminEx wifiAdmin = new WifiAdminEx(context);
        Util.showPostMsg("正在使用IP " + node.ip + " 连接到 " + SSID);
        WifiConfiguration configuration = wifiAdmin.createWifiInfo(SSID, password, security);
        wifiAdmin.addNetwork(configuration);

        WifiUtil wifiUtil = new WifiUtil((WifiManager) context.getSystemService(Context.WIFI_SERVICE));
        if (node.dns1.equalsIgnoreCase("0.0.0.0")) {
            try {
                wifiUtil.editStaticWifiConfig(SSID, password, node.ip,node.gateway, 24, "8.8.8.8");
            } catch (Exception e) {
                Logger.e("设置静态IP失败...");
                e.printStackTrace();
            }
        } else {
            try {
                wifiUtil.editStaticWifiConfig(SSID, password, node.ip,node.gateway, 24, node.dns1);
            } catch (Exception e) {
                Logger.e("设置静态IP 2失败...");
                e.printStackTrace();
            }
        }

        XmlSetting.setXmlWifiPw(password);
        XmlSetting.setXmlWifiSsid(SSID);
    }

    @Override
    public void onClearWifi(int netId) {
        WifiAdminEx wifiAdmin = new WifiAdminEx(context);
        wifiAdmin.removeNetwork(netId);
        Util.showPostMsg("完成");
    }

    @Override
    public void onDisconnect(int netId) {
        WifiAdminEx wifiAdmin = new WifiAdminEx(context);
        wifiAdmin.disconnectWifi(netId);
        Util.showPostMsg("已断开");
    }
}
//修改于:2015年5月15日,星期五
