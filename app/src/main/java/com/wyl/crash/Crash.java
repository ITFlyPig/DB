package com.wyl.crash;

import android.content.Context;

import com.wyl.crash.caught.ICollectStackTraceListener;
import com.wyl.crash.caught.ICrash;
import com.wyl.crash.caught.JavaCrashImpl;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/20
 * 描述    : 崩溃捕获对外暴露的类，可以动态的控制需要捕获和不需要捕获
 */
public class Crash {
    /**
     * 崩溃堆栈捕获的实现
     */
    private static final ICrash crash;
    private static Context context;
    private static String needCatchPackageName;

    public static Context getContext() {
        return context;
    }

    public static String getNeedCatchPackageName() {
        return needCatchPackageName;
    }

    static {
        crash = new JavaCrashImpl();
    }

    /**
     * 安装堆栈捕获器
     *
     * @param needCatchPackageName 需要捕获调用栈的代码的包
     * @param context 上下文，因为会被强引用，所以建议传Application
     * @param collectStackTraceListener 收集到调用栈之后的回调
     */
    public static void setup(Context context, String needCatchPackageName, ICollectStackTraceListener collectStackTraceListener) {
        //都没有回调接口，就没必要处理了
        if (collectStackTraceListener == null) {
            return;
        }
        Crash.needCatchPackageName = needCatchPackageName;
        Crash.context = context;
        crash.setup(collectStackTraceListener);
    }


}
