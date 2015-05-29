package com.zckj.data;

import android.net.wifi.ScanResult;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by angcyo on 2015-03-19 019.
 */
public abstract class AsyncScanWifi extends AsyncTask<WifiAdminEx, Void, WifiNote> {

    @Override
    protected WifiNote doInBackground(WifiAdminEx... params) {
        WifiAdminEx wifiAdmin = params[0];
        WifiNote wifiNote = new WifiNote();
        if (wifiAdmin == null) {
            return null;
        }
        int wifiState = wifiAdmin.checkState();
        wifiNote.wifiState = wifiState;
        wifiNote.supplicantState = wifiAdmin.mWifiManager.getConnectionInfo().getSupplicantState();
        if (wifiState == 2 || wifiState == 3) {
            wifiAdmin.startScan();
            wifiNote.wifiList = wifiAdmin.getWifiList();
            wifiNote.curWifiSSID = wifiAdmin.getSSID();
        }
        return wifiNote;
    }

    @Override
    protected void onPostExecute(WifiNote wifiNote) {
        super.onPostExecute(wifiNote);
        List<ScanResult> newWifiList = new ArrayList<ScanResult>();

        if (wifiNote == null || wifiNote.wifiList == null) {
            onScanResults(null);
            return;
        }

        for (int i = 0; i < wifiNote.wifiList.size(); i++) {
            ScanResult result = wifiNote.wifiList.get(i);
            if (result.SSID != null && result.SSID.length() > 1) {
                newWifiList.add(result);
            }
        }
        wifiNote.wifiList = newWifiList;
        onScanResults(wifiNote);
    }

    public abstract void onScanResults(WifiNote note);
}
//修改于:2015年5月15日,星期五
