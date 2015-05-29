package com.zckj.data;

import android.os.AsyncTask;

/**
 * Created by angcyo on 2015-03-25 025.
 */
public abstract class AsyncRun extends AsyncTask {
    @Override
    protected Object doInBackground(Object[] params) {
        doBack();
        return null;
    }
    public abstract void doBack();

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        doPost();
    }
    public abstract void doPost();
}
//修改于:2015年5月15日,星期五
