package com.wyl;

import android.app.Application;
import android.content.Context;

/**
 * @author : yuelinwang
 * @time : 6/18/21
 * @desc :
 */
public class MyApplication extends Application {
    private static Context application;
    public static Context getInstance() {
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }
}
