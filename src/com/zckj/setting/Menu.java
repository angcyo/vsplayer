package com.zckj.setting;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;

import com.zckj.data.MenuInfo;
import com.zckj.data.SubSettingInfo;
import com.zckj.data.XmlSetting;
import com.zckj.ui.BaseSetContentFragment;
import com.zckj.ui.ExSetContentFragment;
import com.zckj.ui.MoreSetContentFragment;
import com.zckj.ui.NetSetContentFragment;
import com.zckj.ui.SettingEntry;
import com.linux.vshow.R;
import com.linux.vshow.Vsplayer;
public class Menu {
	
	public static void show(final Activity context){
		showEntry(context, getMenuEntryBundle());
	}

	public static void showEntry(final Activity context, Bundle bundle) {
		SettingEntry entry = new SettingEntry();
		entry.setArguments(bundle);
		entry.registItemClickListener(new SettingEntry.OnEntry() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(context, SettingActivity.class);
				SubSettingInfo settingInfo = createSet(position);//
				intent.putExtra(SettingActivity.ARG_SETTING_INFO, settingInfo);
				context.startActivity(intent);
			}

			@Override
			public void onPassword(String enter, String password, boolean isTrue) {
			}
		});
		entry.show(context.getFragmentManager(), "entry");
	}

	public static Bundle getMenuEntryBundle() {
		List<MenuInfo> items = new ArrayList<MenuInfo>();
		items.add(new MenuInfo(R.drawable.img_base_set, "基本设置"));
		items.add(new MenuInfo(R.drawable.img_net_set, "网络设置"));
		items.add(new MenuInfo(R.drawable.img_ex_set, "高级设置"));
		items.add(new MenuInfo(R.drawable.img_more_set, "更多设置"));

		Bundle bundle = new Bundle();
		bundle.putParcelableArrayList(SettingEntry.KEY_MENUINFO,
				(ArrayList<? extends Parcelable>) items);
		bundle.putString(SettingEntry.KEY_PASSWORD, XmlSetting.getXmlAdminPw());
		return bundle;
	}

	// 创建菜单内容
	public static SubSettingInfo createSet(int pos) {
		String title = null;// 标题
		String[] items = null;// 菜单项
		int[] icoId = null;// 菜单图标
		int[] resStyle = null;// 对应内容的样式
		Class<?> contentClass = null;// 内容Fragment载体

		switch (pos) {
		case 0:
			title = "基本设置";
			items = new String[] { "服务器IP", "终端命名" };
			icoId = new int[] { R.drawable.img_server_ip,
					R.drawable.img_terminal_name };
			resStyle = new int[] { BaseSetContentFragment.STYLE_SERVER_IP,
					BaseSetContentFragment.STYLE_TERMINAL_NAME };
			contentClass = BaseSetContentFragment.class;
			break;
		case 1:
			title = "网络设置";
			items = new String[] { "无线网设置", "以太网设置" };
			icoId = new int[] { R.drawable.img_wifi, R.drawable.img_wan };
			resStyle = new int[] { NetSetContentFragment.STYLE_NET_WIFI,
					NetSetContentFragment.STYLE_NET_WAN };
			contentClass = NetSetContentFragment.class;
			break;
		case 2:
			title = "高级设置";
			items = new String[] { "屏幕显示", "存储位置", "密码设置", "更新目录", "节目清理" };
			icoId = new int[] { R.drawable.img_display, R.drawable.img_storage,
					R.drawable.img_password, R.drawable.img_update_path,
					R.drawable.img_clear_fete };
			resStyle = new int[] { ExSetContentFragment.STYLE_EX_DISPLAY,
					ExSetContentFragment.STYLE_EX_STORAGE,
					ExSetContentFragment.STYLE_EX_PASSWORD,
					ExSetContentFragment.STYLE_EX_UPDATE_PATH,
					ExSetContentFragment.STYLE_EX_CLEAR_FETE };
			contentClass = ExSetContentFragment.class;
			break;
		case 3:
			title = "更多设置";
			items = new String[] { "启动画面", "定时音量", "定时开关机", "软件版本" };
			icoId = new int[] { R.drawable.img_run_image,
					R.drawable.img_volume, R.drawable.img_shutdown,
					R.drawable.img_about };
			resStyle = new int[] { MoreSetContentFragment.STYLE_MORE_RUN_BMP,
					MoreSetContentFragment.STYLE_MORE_TIME_VOLUME,
					MoreSetContentFragment.STYLE_MORE_TIME_COLSE,
					MoreSetContentFragment.STYLE_MORE_APK_VER };
			contentClass = MoreSetContentFragment.class;
			break;
		default:
			break;
		}
		SubSettingInfo settingInfo = new SubSettingInfo(title, items, resStyle,
				icoId, contentClass);
		return settingInfo;
	}
	
	
	public static void exitApp(final Activity context){
		final Handler handler = new Handler(Looper.getMainLooper());
		
		
		 if (ExSetContentFragment.isSetAdminPw()) {
	            final Dialog dialog = new Dialog(context, R.style.DialogNoTitle);
	            dialog.setContentView(R.layout.layout_verify_password);

	            ((EditText) dialog.findViewById(R.id.id_et_password)).addTextChangedListener(new TextWatcher() {
	                @Override
	                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	                }

	                @Override
	                public void onTextChanged(CharSequence s, int start, int before, int count) {

	                }

	                @Override
	                public void afterTextChanged(Editable s) {
	                    if (s.toString().equalsIgnoreCase(XmlSetting.getXmlAdminPw())) {
	                       
//	                        try {
//                        	context.getParent().onBackPressed();
	                    	dialog.cancel();
	                        	System.exit(0);
//						} catch (Exception e) {
//						}
	                    	 

//	                       
//	                        handler.postDelayed(new Runnable() {
//								
//								@Override
//								public void run() {
//									context.getParent().onBackPressed();
//								}
//							}, 100);
	                        
//	                        ((Vsplayer)context).getParent().onBackPressed();
	                    }
	                }
	            });
	            dialog.show();
	        } else {
	        	System.exit(0);
	        }
	}
}
// 修改于:2015年5月15日,星期五
