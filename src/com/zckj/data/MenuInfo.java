package com.zckj.data;


import android.os.Parcel;
import android.os.Parcelable;

public class MenuInfo implements Parcelable{
	public int menuIco;
	public String menuTitle;

	
	public MenuInfo(int menuIco, String menuTitle) {
		this.menuIco = menuIco;
		this.menuTitle = menuTitle;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

	}
}
//修改于:2015年5月15日,星期五
