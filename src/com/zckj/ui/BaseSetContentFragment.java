package com.zckj.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.zckj.data.Util;
import com.zckj.data.XmlSetting;
import com.linux.vshow.Constant;
import com.linux.vshow.R;
import com.linux.vshow.Tool;

public class BaseSetContentFragment extends BaseFragment {
    public static final int STYLE_SERVER_IP = 0x001001;
    public static final int STYLE_TERMINAL_NAME = 0x001002;
    IPEditText ipEditText;
    Button btSave;
    EditText etTerminalName;

    public BaseSetContentFragment() {
        default_layout_style = STYLE_SERVER_IP;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        switch (style) {
            case STYLE_SERVER_IP:
                rootView = inflater.inflate(R.layout.layout_base_server_ip, container,
                        false);
                initServerIpLayout();
                break;
            case STYLE_TERMINAL_NAME:
                rootView = inflater.inflate(R.layout.layout_base_terminal_name,
                        container, false);
                initTerminalNameLayout();
                break;
            default:
                rootView = inflater.inflate(R.layout.layout_base_server_ip, container,
                        false);
                initServerIpLayout();
                break;
        }

        return rootView;
    }

    private void initTerminalNameLayout() {
        etTerminalName = (EditText) rootView.findViewById(R.id.id_et_terminal_name);
        btSave = (Button) rootView.findViewById(R.id.id_server_ip_ok);

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String terminalName = etTerminalName.getText().toString();
                if (terminalName != null) {
                    XmlSetting.setXmlTerminalName(terminalName);
                    Util.showPostMsg("终端名 " + terminalName + " 已保存");
                    
                    Tool.saveConfig(XmlSetting.XML_TERMINAL_NAME +  "!" + terminalName, Constant.advance);
                }
            }
        });

        String terminalName = XmlSetting.getXmlTerminalName();
        if (terminalName != null && !Util.isEmpty(terminalName)) {
            etTerminalName.setText(terminalName);
        } else {
            btSave.setEnabled(false);
        }

        etTerminalName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString() == null || Util.isEmpty(s.toString())) {
                    btSave.setEnabled(false);
                }else
                    btSave.setEnabled(true);
            }
        });
    }

    private void initServerIpLayout() {
        ipEditText = (IPEditText) rootView.findViewById(R.id.id_server_edit_ip);
        btSave = (Button) rootView.findViewById(R.id.id_server_ip_ok);

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = ipEditText.getIpText();
                if (ip != null) {
                    XmlSetting.setXmlServerIp(ip);
                    Util.showPostMsg("IP " + ip + " 已保存");
                    
					Tool.saveConfig("srvip!"
							+ ip,
							Constant.config2);
					Constant.li.writeLog("0000 " + R.string.log37
							+ ip);
					//init();
                }
            }
        });

        String ip = XmlSetting.getXmlServerIp();
        if (ip != null) {
            ipEditText.setIpText(ip);
        } else {
            ipEditText.setIpText("192.168.1.102");
        }
        ipEditText.clearAllFocus();
    }

}
//修改于:2015年5月15日,星期五
