package com.zckj.ui;

import android.app.DialogFragment;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.zckj.data.MenuInfo;
import com.zckj.data.Util;
import com.linux.vshow.R;
import java.util.List;

public class SettingEntry extends DialogFragment {

    public static String KEY_MENUINFO = "key_menu_info";
    public static String KEY_PASSWORD = "key_password";

    protected List<MenuInfo> menuItems;//显示的菜单组
    protected ViewSwitcher viewSwitcher;
    protected String password;//传递过来密码

    OnEntry itemClickListener = null;

    public SettingEntry() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setStyle(STYLE_NO_TITLE, getTheme());
        menuItems = getArguments().getParcelableArrayList(KEY_MENUINFO);
        password = getArguments().getString(KEY_PASSWORD);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Context context = getActivity();
        Resources rs = getResources();
        viewSwitcher = new ViewSwitcher(context);
        viewSwitcher.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.scale_0t1_home_list_item));
        viewSwitcher.setBackgroundColor(getResources().getColor(R.color.col_content_bg));

        //密码布局
        RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(context)
                .inflate(R.layout.layout_login_password, null);
        final EditText etPassWord = (EditText) relativeLayout.findViewById(R.id.id_et_password);
        etPassWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!Util.isEmpty(password) && s.toString().equalsIgnoreCase(password)) {
                    if (itemClickListener != null) {
                        itemClickListener.onPassword(s.toString(), password, true);
                    }

                    // 隐藏键盘
                    ((InputMethodManager) SettingEntry.this.getActivity()
                            .getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(etPassWord.getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);

                    viewSwitcher.showNext();
                }
            }
        });

        //网格布局
        GridAdapter gridAdapter = new GridAdapter();
        GridView gridView = new GridView(context);
        gridView.setBackgroundColor(getResources().getColor(R.color.col_content_bg));
        gridView.setNumColumns(computeMaxLine());
        gridView.setAdapter(gridAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(parent, view, position, id);
                }
            }
        });

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        viewSwitcher.addView(gridView, 0, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        viewSwitcher.addView(relativeLayout, 1, layoutParams);
        if (!Util.isEmpty(password)) {
            viewSwitcher.showNext();
        }
        return viewSwitcher;
    }

    // 根据菜单个数,计算需要分配的行数
    protected int computeMaxLine() {
        if (menuItems == null || menuItems.size() == 0) {
            throw new IllegalArgumentException("至少需要一个菜单项");
        }

        int count = menuItems.size();
        int maxLine = (int) Math.ceil(Math.sqrt(count));

        return maxLine;
    }

    //注册Item点击事件
    public void registItemClickListener(OnEntry listener) {
        this.itemClickListener = listener;
    }

    //接口,Item点击,密码正确的回调
    public interface OnEntry {
        void onItemClick(AdapterView<?> parent, View view, int position, long id);
        void onPassword(String enter, String password, boolean isTrue);
    }

    //适配器
    protected class GridAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (menuItems == null) {
                return 0;
            }
            return menuItems.size();
        }

        @Override
        public Object getItem(int position) {
            return menuItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            MenuInfo info = menuItems.get(position);

            if (convertView == null) {
                convertView = new TextView(getActivity());
            }

            TextView menu = (TextView) convertView;
            menu.setWidth((int) getResources().getDimension(R.dimen.ds_setting_button_size));
            menu.setHeight((int) getResources().getDimension(R.dimen.ds_setting_button_size));
            menu.setTextSize(getResources().getDimension(R.dimen.ds_text_normal_size));
            menu.setGravity(Gravity.CENTER);
            int padding = (int) getResources().getDimension(R.dimen.ds_mid);
            menu.setPadding(padding, padding, padding, padding);
            menu.setCompoundDrawablesWithIntrinsicBounds(0,
                    info.menuIco, 0, 0);
            menu.setText(info.menuTitle);
            menu.setBackgroundResource(R.drawable.normal_bt_selector);
            menu.setTextColor(getResources().getColor(R.color.col_text));

            return menu;
        }

    }
}
//修改于:2015年5月15日,星期五
