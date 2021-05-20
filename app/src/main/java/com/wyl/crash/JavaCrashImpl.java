package com.wyl.crash;

import android.database.DefaultDatabaseErrorHandler;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/20
 * 描述    : Java崩溃捕获的实现
 */
public class JavaCrashImpl implements ICrash {
    // 当前设置的handler
    private Thread.UncaughtExceptionHandler defaultHandler;
    // 之前存在的handler
    private Thread.UncaughtExceptionHandler prevHandler;
    //是否已安装
    private boolean isSetup;

    @Override
    public synchronized void setup() {
        if (isSetup) return;
        prevHandler = Thread.getDefaultUncaughtExceptionHandler();
        defaultHandler = new DefaultUncaughtExceptionHandler(prevHandler, new CollectStackTraceImpl());
        Thread.setDefaultUncaughtExceptionHandler(defaultHandler);
        isSetup = true;
    }

    @Override
    public void reset() {
        Thread.setDefaultUncaughtExceptionHandler(prevHandler);
    }
}
