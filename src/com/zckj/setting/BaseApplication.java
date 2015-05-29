package com.zckj.setting;

import com.orm.SugarApp;

/**
 * Created by angcyo on 2015-03-23 023.
 */
public class BaseApplication extends SugarApp {
    public static BaseApplication App;

    public BaseApplication() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        App = this;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

}
//修改于:2015年5月15日,星期五
