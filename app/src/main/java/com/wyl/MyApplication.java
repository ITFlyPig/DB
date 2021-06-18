package com.wyl;

import android.app.Application;

/**
 * @author : yuelinwang
 * @time : 6/18/21
 * @desc :
 */
public class MyApplication extends Application {
    private static MyApplication application;
    public static MyApplication getInstance() {
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }
}
