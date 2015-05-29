package com.zckj.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zckj.data.Logger;
import com.zckj.data.OnMenuItemChanged;

public class BaseFragment extends Fragment {

    public static final String LAYOUT_STYLE = "layout_style";//根据布局的样式,填充对应布局,key
    protected int default_layout_style = -1;//缺省的设置
    protected int style = -1;//布局样式

    protected View rootView;
    protected Context context;

    protected OnMenuItemChanged itemChangedListener;

    public static boolean showDubg = false;

    protected Handler handler = new Handler();

    public BaseFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        context = activity;
        itemChangedListener = (OnMenuItemChanged) activity;
    }

    protected Bundle createArgs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createArgs = getArguments();

        if (createArgs != null) {
            if (default_layout_style != -1) {
                style = createArgs.getInt(LAYOUT_STYLE, default_layout_style);
            } else {
                style = createArgs.getInt(LAYOUT_STYLE, default_layout_style);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (showDubg)
            Logger.e("------onPause---------");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (showDubg)
            Logger.e("------onResume---------");
    }

    @Override
    public void onStart() {
        super.onStart();
        if (showDubg)
            Logger.e("------onStart---------");
    }

    @Override
    public void onStop() {
        super.onStop();
        if (showDubg)
            Logger.e("------onStop---------");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (showDubg)
            Logger.e("------onDetach---------");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (showDubg)
            Logger.e("------onDestroyView---------");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (showDubg)
            Logger.e("------onDestroy---------");
    }

}
//修改于:2015年5月15日,星期五
