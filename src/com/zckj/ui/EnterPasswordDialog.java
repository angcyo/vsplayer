package com.zckj.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.zckj.data.OnWifiConnect;
import com.zckj.data.Util;
import com.zckj.data.WifiConnectInfo;
import com.zckj.data.WifiStaticIpNode;
import com.zckj.data.XmlSetting;
import com.linux.vshow.R;

/**
 * Created by angcyo on 2015-03-31 031.
 */
public class EnterPasswordDialog extends Dialog implements
		View.OnClickListener, CompoundButton.OnCheckedChangeListener {

	WifiConnectInfo wifiInfo;
	TextView tvWifiSSID;
	EditText etWifiPassword;
	Button btCancel, btConnect, btWifiSystem;

	OnWifiConnect connect;
	Context context;
	CheckBox cbStaticIp;// 用于显示布局的开关
	IPEditText ipIp, ipGateway, ipDns1, ipDns2;
	View layoutStaticIp;// 静态IP的布局,默认隐藏

	public EnterPasswordDialog(Context context) {
		super(context);
	}

	public EnterPasswordDialog(Context context, int theme,
			OnWifiConnect listener, WifiConnectInfo info) {
		super(context, theme);
		this.context = context;
		connect = listener;
		wifiInfo = info;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setCancelable(true);
		setCanceledOnTouchOutside(false);
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);

		initDialogLayout();
	}

	private void initDialogLayout() {
		tvWifiSSID = (TextView) findViewById(R.id.id_wifi_ssid);
		etWifiPassword = (EditText) findViewById(R.id.id_et_wifi_password);
		btCancel = (Button) findViewById(R.id.id_wifi_cancel);
		btConnect = (Button) findViewById((R.id.id_wifi_connect));
		btWifiSystem = (Button) findViewById(R.id.id_wifi_system);
		cbStaticIp = (CheckBox) findViewById(R.id.id_cb_static_ip);
		ipIp = (IPEditText) findViewById(R.id.id_ipet_ip);
		ipGateway = (IPEditText) findViewById(R.id.id_ipet_gateway);
		ipDns1 = (IPEditText) findViewById(R.id.id_ipet_dns1);
		ipDns2 = (IPEditText) findViewById(R.id.id_ipet_dns2);
		layoutStaticIp = findViewById(R.id.id_layout_static_ip);

		cbStaticIp.setOnCheckedChangeListener(this);

		btCancel.setOnClickListener(this);
		btConnect.setOnClickListener(this);
		btWifiSystem.setOnClickListener(this);

		initLayoutData();
	}

	private void initLayoutData() {
		if (wifiInfo != null)
			tvWifiSSID.setText(wifiInfo.SSID == null ? "SSID" : wifiInfo.SSID);

		etWifiPassword.requestFocus();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.id_wifi_cancel) {
			this.cancel();

		} else if (id == R.id.id_wifi_connect) {
			String password = etWifiPassword.getText().toString();
			if (password == null || password.length() == 0
					|| password.length() < 8) {
				etWifiPassword.setError("密码至少8位");
				etWifiPassword.requestFocus();
			} else if (connect != null) {
				if (cbStaticIp.isChecked()) {
					WifiStaticIpNode node = getWifiStaticIpNode();
					if (!node.ip.equalsIgnoreCase("0.0.0.0")) {
						connect.onConnectStaticIp(wifiInfo.SSID, etWifiPassword
								.getText().toString(), wifiInfo.security, node);
					} else {
						connect.onConnect(wifiInfo.SSID, etWifiPassword
								.getText().toString(), wifiInfo.security);
					}
				} else {
					connect.onConnect(wifiInfo.SSID, etWifiPassword.getText()
							.toString(), wifiInfo.security);
				}
				this.cancel();
			}

		} else if (id == R.id.id_wifi_system) {
			this.cancel();
			context.startActivity(new Intent(
					android.provider.Settings.ACTION_WIFI_SETTINGS));
		}

	}

	private WifiStaticIpNode getWifiStaticIpNode() {
		String ip = ipIp.getIpText();
		String gateway = ipGateway.getIpText();
		String dns = ipDns1.getIpText();

		WifiStaticIpNode node = new WifiStaticIpNode(ip, gateway, dns, dns);

		// 保存配置
		XmlSetting.set(new String[] { XmlSetting.XML_STATIC_IP,
				XmlSetting.XML_STATIC_GATEWAY, XmlSetting.XML_STATIC_DNS }, ip,
				gateway, dns);
		return node;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView.getId() == R.id.id_cb_static_ip) {
			if (isChecked) {
				showStaticIpLayout();
			} else {
				hideStaticIpLayout();
			}
		}
	}

	void hideStaticIpLayout() {
		layoutStaticIp.setVisibility(View.GONE);
	}

	void showStaticIpLayout() {
		layoutStaticIp.setVisibility(View.VISIBLE);

		// 获取配置
		Object[] values = XmlSetting.get(new String[] {
				XmlSetting.XML_STATIC_IP, XmlSetting.XML_STATIC_GATEWAY,
				XmlSetting.XML_STATIC_DNS });

		String ip = values[0].toString();
		String gateway = values[1].toString();
		String dns = values[2].toString();

		if (Util.isEmpty(ip)) {
			ipIp.setIpText("192.168.1.128");
		} else {
			ipIp.setIpText(ip);
		}

		if (Util.isEmpty(gateway)) {
			ipGateway.setIpText("192.168.1.1");
		} else {
			ipGateway.setIpText(gateway);
		}

		if (Util.isEmpty(dns)) {
			ipDns1.setIpText("8.8.8.8");
		} else {
			ipDns1.setIpText(dns);
		}
		cbStaticIp.requestFocus();
	}

}

// 修改于:2015年5月15日,星期五
