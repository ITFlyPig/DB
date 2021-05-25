package com.wyl.crash;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.MainThread;

import com.wyl.thread.NewThreadListener;
import com.wyl.thread.WUThreadFactoryUtil;

import java.util.HashMap;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/20
 * 描述    : Java崩溃捕获的实现
 */
public class JavaCrashImpl implements ICrash {
    /**
     * 当前设置的handler
     */
    private Thread.UncaughtExceptionHandler defaultHandler;
    /**
     * 是否已安装
     */
    private boolean isSetup;

    /**
     * 之前别人设置的处理器
     */
    private HashMap<Long, Thread.UncaughtExceptionHandler> preMap;

    @Override
    public void setup(ICollectStackTraceListener collectStackTraceListener) {
        if (isSetup) {
            return;
        }
        preMap = new HashMap<>();
        defaultHandler = new DefaultUncaughtExceptionHandler(collectStackTraceListener);
        // 监听新线程的创建
        WUThreadFactoryUtil.setNewThreadListener(new NewThreadListener() {
            @Override
            public void onNewThread(Thread t) {
                if (t == null) {
                    return;
                }
                // 设置线程的异常处理器
                if (t.getUncaughtExceptionHandler() != null) {
                    preMap.put(t.getId(), t.getUncaughtExceptionHandler());
                }
                t.setUncaughtExceptionHandler(defaultHandler);
            }
        });
        // 设置主线程的异常处理器
        Looper mainLooper = Looper.getMainLooper();
        if (mainLooper != null) {
            Thread mainThread = mainLooper.getThread();
            // 记录之前的处理器
            preMap.put(mainThread.getId(), mainThread.getUncaughtExceptionHandler());
            // 设置自己的处理器
            mainThread.setUncaughtExceptionHandler(defaultHandler);
        }

        isSetup = true;
    }
}
