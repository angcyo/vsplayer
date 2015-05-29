package com.zckj.data;

import java.io.Serializable;

/**
 * Created by angcyo on 2015-05-11 011.
 */
public class SubSettingInfo implements Serializable{
    public String menuTitle;//用于显示的菜单标题
    public String[] menuItemTitle;//菜单项
    public int[] resId;//菜单项对应的菜单布局在Fragment中的样式
    public int[] icoId;//菜单ico
    public Class contentFragment;//承载内容界面的Fragment

    public SubSettingInfo(String menuTitle, String[] menuItemTitle, int[] resId, int[] icoId, Class contentFragment) {
        this.menuTitle = menuTitle;
        this.menuItemTitle = menuItemTitle;
        this.resId = resId;
        this.icoId = icoId;
        this.contentFragment = contentFragment;
    }
}
//修改于:2015年5月15日,星期五
