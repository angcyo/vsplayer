package com.zckj.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentException;

import com.zckj.setting.BaseApplication;

/**
 * Created by angcyo on 2015-03-31 031.
 */
public class XmlSetting {
    public static final String XML_SERVER_IP = "server_ip";//服务器IP
    public static final String XML_TERMINAL_NAME = "terminal_name";//终端名
    public static final String XML_WIFI_SSID = "wifi_ssid";//最后连接的WIFI SSID
    public static final String XML_WIFI_PW = "wifi_pw";//最后连接的WIFI 密码
    public static final String XML_ROTATE_ANGLE = "rotate_angle";//最后旋转的角度
    public static final String XML_STORAGE_PATH = "storage_path";//存储路径
    public static final String XML_ADMIN_PW = "admin_pw";//管理员密码
    public static final String XML_UPDATE_PATH = "update_path";//升级路径
    public static final String XML_RUN_IMAGE = "run_image";//启动画面的路径

    public static final String XML_STATIC_IP = "static_ip";//最后设置的静态IP
    public static final String XML_STATIC_GATEWAY = "static_gateway";//最后设置的静态网关
    public static final String XML_STATIC_DNS = "static_dns";//最后设置的静态DNS

    static {
//        XmlUtil.xmlFilePath = FileUtil.getAppRootPath(BaseApplication.App) + File.separator + "XmlSetting.xml";
        
//        XmlUtil.xmlFilePath = "/mnt/extsd";
    }


    /**
     * 用可变参数,设置对应key的值
     *
     * @param key   the key
     * @param value the value
     */
    public static void set(String[] key, String... value) {
        for (int i = 0; i < Math.min(key.length, value.length); i++) {
            handSetValue(key[i], value[i]);
        }
    }


    /**
     * 通过可变参数,获取对应值
     *
     * @param key the key
     * @return the string [ ]
     */
    public static Object[] get(String... key) {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < key.length; i++) {
            list.add(handGetValue(key[i]));
        }
        return list.toArray();
    }


    /**
     * 用数组的方式,设置对应的值
     *
     * @param key   the key
     * @param value the value
     */
    public static void setUseArray(String[] key, String[] value) {
        for (int i = 0; i < Math.min(key.length, value.length); i++) {
            handSetValue(key[i], value[i]);
        }
    }

    /**
     * 用数组作为参数,获取对应值
     *
     * @param key the key
     * @return the string [ ]
     */
    public static String[] getUseArrray(String[] key) {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < key.length; i++) {
            list.add(handGetValue(key[i]));
            //            Logger.e("getKey------" + key[i]);
            //            Logger.e("getValue------" + handGetValue(key[i]));
        }
        return (String[]) list.toArray();
    }

    private static void handSetValue(String key, String value) {
		if (key.equalsIgnoreCase(XML_SERVER_IP)) {
			setXmlServerIp(value);
		}else
		if (key.equalsIgnoreCase(XML_TERMINAL_NAME)) {
			 setXmlTerminalName(value);
		}else
		if (key.equalsIgnoreCase(XML_WIFI_SSID)) {
			setXmlWifiSsid(value);
		}else
		if (key.equalsIgnoreCase(XML_WIFI_PW)) {
			setXmlWifiPw(value);
		}else
		if (key.equalsIgnoreCase(XML_ROTATE_ANGLE)) {
			setXmlRotateAngle(value);
		}else
		if (key.equalsIgnoreCase(XML_STORAGE_PATH)) {
			setXmlStoragePath(value);
		}else
		if (key.equalsIgnoreCase(XML_UPDATE_PATH)) {
			setXxmlUpdatePath(value);
		}else
		if (key.equalsIgnoreCase(XML_RUN_IMAGE)) {
			 setXmlRunImage(value);
		}else
		if (key.equalsIgnoreCase(XML_STATIC_IP)) {
			 setXmlStaticIp(value);
		}else
		if (key.equalsIgnoreCase(XML_STATIC_GATEWAY)) {
			setXmlStaticGateway(value);
		}else
		if (key.equalsIgnoreCase(XML_STATIC_DNS)) {
			   setXmlStaticDns(value);
		}else
		if (key.equalsIgnoreCase(XML_ADMIN_PW)) {
			setXmlAdminPw(value);
		}
    }

    private static String handGetValue(String key) {
        String value = "";
    	if (key.equalsIgnoreCase(XML_SERVER_IP)) {
    		value = getXmlServerIp();
		}else
		if (key.equalsIgnoreCase(XML_TERMINAL_NAME)) {
			value = getXmlTerminalName();
		}else
		if (key.equalsIgnoreCase(XML_WIFI_SSID)) {
			value = getXmlWifiSsid();
		}else
		if (key.equalsIgnoreCase(XML_WIFI_PW)) {
			value = getXmlWifiPw();
		}else
		if (key.equalsIgnoreCase(XML_ROTATE_ANGLE)) {
			 value = getXmlRotateAngle();
		}else
		if (key.equalsIgnoreCase(XML_STORAGE_PATH)) {
			 value = getXmlStoragePath();
		}else
		if (key.equalsIgnoreCase(XML_UPDATE_PATH)) {
			value = getXxmlUpdatePath();
		}else
		if (key.equalsIgnoreCase(XML_RUN_IMAGE)) {
			 value = getXmlRunImage();
		}else
		if (key.equalsIgnoreCase(XML_STATIC_IP)) {
			value = getXmlStaticIp();
		}else
		if (key.equalsIgnoreCase(XML_STATIC_GATEWAY)) {
			 value = getXmlStaticGateway();
		}else
		if (key.equalsIgnoreCase(XML_STATIC_DNS)) {
			value = getXmlStaticDns();
		}else
		if (key.equalsIgnoreCase(XML_ADMIN_PW)) {
			value = getXmlAdminPw();
		}
     
        return value;
    }

    public static String getXmlServerIp() {
        try {
            return XmlUtil.getValue(XML_SERVER_IP);
        } catch (DocumentException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void setXmlServerIp(String value) {
        try {
            XmlUtil.putValue(XML_SERVER_IP, value);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    public static String getXmlTerminalName() {
        try {
            return XmlUtil.getValue(XML_TERMINAL_NAME);
        } catch (DocumentException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void setXmlTerminalName(String value) {
        try {
            XmlUtil.putValue(XML_TERMINAL_NAME, value);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    public static String getXmlWifiSsid() {
        try {
            return XmlUtil.getValue(XML_WIFI_SSID);
        } catch (DocumentException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void setXmlWifiSsid(String value) {
        try {
            XmlUtil.putValue(XML_WIFI_SSID, value);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    public static String getXmlWifiPw() {
        try {
            return XmlUtil.getValue(XML_WIFI_PW);
        } catch (DocumentException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void setXmlWifiPw(String value) {
        try {
            XmlUtil.putValue(XML_WIFI_PW, value);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    public static String getXmlRotateAngle() {
        try {
            return XmlUtil.getValue(XML_ROTATE_ANGLE);
        } catch (DocumentException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void setXmlRotateAngle(String value) {
        try {
            XmlUtil.putValue(XML_ROTATE_ANGLE, value);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    public static String getXmlStoragePath() {
        try {
            return XmlUtil.getValue(XML_STORAGE_PATH);
        } catch (DocumentException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void setXmlStoragePath(String value) {
        try {
            XmlUtil.putValue(XML_STORAGE_PATH, value);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    public static String getXmlAdminPw() {
        try {
            return XmlUtil.getValue(XML_ADMIN_PW);
        } catch (DocumentException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void setXmlAdminPw(String value) {
        try {
            XmlUtil.putValue(XML_ADMIN_PW, value);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    public static String getXxmlUpdatePath() {
        try {
            return XmlUtil.getValue(XML_UPDATE_PATH);
        } catch (DocumentException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void setXxmlUpdatePath(String value) {
        try {
            XmlUtil.putValue(XML_UPDATE_PATH, value);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    public static String getXmlRunImage() {
        try {
            return XmlUtil.getValue(XML_RUN_IMAGE);
        } catch (DocumentException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void setXmlRunImage(String value) {
        try {
            XmlUtil.putValue(XML_RUN_IMAGE, value);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    public static String getXmlStaticIp() {
        try {
            return XmlUtil.getValue(XML_STATIC_IP);
        } catch (DocumentException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void setXmlStaticIp(String value) {
        try {
            XmlUtil.putValue(XML_STATIC_IP, value);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    public static String getXmlStaticGateway() {
        try {
            return XmlUtil.getValue(XML_STATIC_GATEWAY);
        } catch (DocumentException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void setXmlStaticGateway(String value) {
        try {
            XmlUtil.putValue(XML_STATIC_GATEWAY, value);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    public static String getXmlStaticDns() {
        try {
            return XmlUtil.getValue(XML_STATIC_DNS);
        } catch (DocumentException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void setXmlStaticDns(String value) {
        try {
            XmlUtil.putValue(XML_STATIC_DNS, value);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }
}
