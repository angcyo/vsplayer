package com.zckj.data;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.linux.vshow.R;


/**
 * Created by angcyo on 2015-03-21 021.
 */
public class NetWifiListAdapter extends BaseAdapter {
    Context context;
    WifiNote wifiNote;

    public NetWifiListAdapter(Context context, WifiNote wifiNote) {
        this.context = context;
        this.wifiNote = wifiNote;
    }

    public void setDataChanged(WifiNote wifiNote) {
        this.wifiNote = wifiNote;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return wifiNote.wifiList.size();
    }

    @Override
    public Object getItem(int position) {
        return wifiNote.wifiList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_net_wifi_item, null);
            holder = new ViewHolder();
            holder.ssid = (TextView) convertView.findViewById(R.id.id_net_wifi_ssid);
            holder.isConnect = (TextView) convertView.findViewById(R.id.id_net_wifi_state);
            holder.wifiLevelImg = (ImageView) convertView.findViewById(R.id.id_net_wifi_level_img);
            holder.wifiIsLock = (ImageView) convertView.findViewById(R.id.id_net_wifi_lock_img);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ScanResult scanResult = wifiNote.wifiList.get(position);
        holder.ssid.setText(scanResult.SSID);
//        Logger.e("当前连接的Wifi:::" + wifiNote.curWifiSSID.substring(1, wifiNote.curWifiSSID.length() - 1));
        //如果WIFI状态是已连接,并且SSID相同,就显示已连接View
        if (wifiNote.wifiState == WifiManager.WIFI_STATE_ENABLED &&
                scanResult.SSID.equalsIgnoreCase(Util.trimMarks(new WifiAdminEx(context).getSSID()))) {

            if (wifiNote.supplicantState == SupplicantState.COMPLETED) {
                holder.isConnect.setVisibility(View.VISIBLE);
                holder.isConnect.setText("已连接");
            } else {
                holder.isConnect.setVisibility(View.VISIBLE);
                holder.isConnect.setText("正在连接");
            }
        } else {
            holder.isConnect.setVisibility(View.INVISIBLE);
        }
        int wifiLevel = getWifiLevel(scanResult.level);//根据WIFI强度,加载不同的图片
        switch (wifiLevel) {
            case 0:
                holder.wifiLevelImg.setImageResource(R.drawable.img_wifi_level_0);
                break;
            case 1:
                holder.wifiLevelImg.setImageResource(R.drawable.img_wifi_level_1);
                break;
            case 2:
                holder.wifiLevelImg.setImageResource(R.drawable.img_wifi_level_2);
                break;
            case 3:
                holder.wifiLevelImg.setImageResource(R.drawable.img_wifi_level_3);
                break;
            case 4:
                holder.wifiLevelImg.setImageResource(R.drawable.img_wifi_level_4);
                break;
            default:
                holder.wifiLevelImg.setImageResource(R.drawable.img_wifi_level_0);
                break;
        }

        if (getSecurity(scanResult.capabilities)) {
            holder.wifiIsLock.setVisibility(View.VISIBLE);
        } else {
            holder.wifiIsLock.setVisibility(View.INVISIBLE);
        }

//        convertView.setFocusable(true);
        return convertView;
    }

    static class ViewHolder {
        TextView ssid; //wifi 的ssid
        TextView isConnect;//是否连接了当前的wifi
        ImageView wifiLevelImg;// wifi 信号强度等级
        ImageView wifiIsLock;//wifi 是否加密了
    }

    /**
     * WIFI 的5个等级:
     * 4:最强
     * 3:强
     * 2:一般
     * 1:弱
     * 0:无信号
     *
     * @param level the level
     * @return the int
     */
    static int getWifiLevel(int level) {
        int result = 0;
        int value = Math.abs(level);

        if (value <= 50) {
            result = 4;
        } else if (value > 50 && value <= 65) {
            result = 3;
        } else if (value > 65 && value <= 80) {
            result = 2;
        } else if (value > 80) {
            result = 1;
        } else {
            result = 0;
        }

        return result;
    }

    /**
     * 判断WIFI 是否是 没有加密的
     *
     * @param capabilities the capabilities
     * @return the wifi is iBSS
     */
    static boolean getWifiIsIBSS(String capabilities) {
        if (capabilities.indexOf("IBSS") != -1) {
            return true;
        }
        return false;
    }

    public static boolean getSecurity(String capabilities) {
        if (capabilities.contains("WEP")) {
            return true;
        } else if (capabilities.contains("PSK")) {
            return true;
        } else if (capabilities.contains("EAP")) {
            return true;
        }
        return false;
    }


}
//修改于:2015年5月15日,星期五
