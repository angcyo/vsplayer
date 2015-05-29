package com.zckj.setting;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.SingleLineTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.linux.vshow.R;
import com.zckj.data.Logger;
import com.zckj.data.OnMenuItemChanged;
import com.zckj.data.SubSettingInfo;
import com.zckj.ui.BaseFragment;

/**
 * Created by angcyo on 2015-05-11 011.
 */
public class SettingActivity extends BaseFragmentActivity implements OnMenuItemChanged{

    public static String ARG_SETTING_INFO = "arg_setting_info";
    SubSettingInfo settingInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.setting_main);

        settingInfo = (SubSettingInfo) getIntent().getSerializableExtra(ARG_SETTING_INFO);
        initMenu();
        setContent(settingInfo.resId[0]);
    }

    private void setContent(int style) {
        try {
            Fragment contentFragment = (Fragment) settingInfo.contentFragment.newInstance();
            Bundle arg = new Bundle();
            arg.putInt(BaseFragment.LAYOUT_STYLE, style);
            contentFragment.setArguments(arg);
            replaceFragment(R.id.layout_content, contentFragment, true);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void initMenu() {
        MenuFragment menuFragment = new MenuFragment();
        Bundle args =new Bundle();
        args.putString(MenuFragment.MENU_TITLE, settingInfo.menuTitle);
        args.putStringArray(MenuFragment.MENU_ITEMS, settingInfo.menuItemTitle);
        args.putIntArray(MenuFragment.MENU_ICOS, settingInfo.icoId);
        menuFragment.setArguments(args);

        replaceFragment(R.id.layout_menu, menuFragment, false);
    }

    public void replaceFragment(int resId, Fragment fragment, boolean anim) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
//        if (anim)
//            ft.setCustomAnimations(R.anim.tran_ttb_enter_frag,
//                    R.anim.tran_ttb_exit_frag);
        ft.replace(resId, fragment).commit();
    }

    public void replaceFragment(Fragment menuFragment,
                                Fragment contentFragment) {
        replaceFragment(R.id.layout_menu, menuFragment, false);
        replaceFragment(R.id.layout_content, contentFragment, true);
    }

    @Override
    public void onItemChanged(int itemIndex) {
//        Logger.e("点击菜单:" + itemIndex);
        setContent(settingInfo.resId[itemIndex]);
    }

    public static class MenuFragment extends Fragment {
        public static String MENU_TITLE = "menu_title";
        public static String MENU_ITEMS = "menu_items";
        public static String MENU_ICOS = "menu_icos";

        View rootView;
        String title;
        String[] items;
        int[] icos;
        OnMenuItemChanged listener;

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            listener = (OnMenuItemChanged) activity;
        }

        @Override
        public void onCreate( Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            title = getArguments().getString(MENU_TITLE);
            items = getArguments().getStringArray(MENU_ITEMS);
            icos = getArguments().getIntArray(MENU_ICOS);
        }

       
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.fragment_menu, container, false);
            ((TextView) rootView.findViewById(R.id.id_tx_menu_title)).setText(title);

            RadioGroup radioGroup = (RadioGroup) rootView.findViewById(R.id.id_rb_group);
            radioGroup.removeAllViews();
            RadioButton radioButton = null;
            for (int i = 0; i < items.length; i++) {
                radioButton = getItem(items[i], icos[i]);
                RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                radioGroup.addView(radioButton, params);
            }
            View firstView = radioGroup.getChildAt(0);
            View lastView = radioGroup.getChildAt(items.length-1);
            firstView.setNextFocusUpId(lastView.getId());
            lastView.setNextFocusDownId(firstView.getId());

            radioGroup.check(firstView.getId());

            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    listener.onItemChanged(group.indexOfChild(group.findViewById(checkedId)));
                }
            });

            return rootView;
        }

        private RadioButton getItem(String title, int icoId) {
            RadioButton radioButton = new RadioButton(getActivity());
            Resources rs = getResources();

            radioButton.setBackgroundResource(R.drawable.menu_radio_selector);
            radioButton.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
            radioButton.setTextSize(rs.getDimension(R.dimen.ds_menu_item_tx_size));
            radioButton.setTextColor(rs.getColor(R.color.col_text));
            radioButton.setHeight((int) rs.getDimension(R.dimen.ds_setting_menu_height));
            radioButton.setTransformationMethod(SingleLineTransformationMethod.getInstance());
            radioButton.setEllipsize(TextUtils.TruncateAt.END);
            radioButton.setPadding(20, 5, 0, 5);
            radioButton.setCompoundDrawablePadding(20);
            radioButton.setChecked(false);

            radioButton.setText(title);
            radioButton.setCompoundDrawablesWithIntrinsicBounds(icoId, 0, 0, 0);

            return radioButton;
        }

    }

}
//修改于:2015年5月15日,星期五
