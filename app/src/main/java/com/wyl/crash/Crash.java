package com.wyl.crash;

import android.content.Context;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/20
 * 描述    : 崩溃捕获对外暴露的类，可以动态的控制需要捕获和不需要捕获
 */
public class Crash {
    // 崩溃堆栈捕获的实现
    private static final ICrash crash;
    protected static Context context;

    static {
        crash = new JavaCrashImpl();
    }

    /**
     * 安装堆栈捕获器
     *
     * @param context 上下文，因为会被强引用，所以建议传Application
     */
    public static void setup(Context context) {
        crash.setup();
        Crash.context = context;
    }

    /**
     * 重置堆栈捕获器
     */
    public static void reset() {
        crash.reset();

    }

}
