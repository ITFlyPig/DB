package com.wyl.thread;

import java.util.concurrent.ThreadFactory;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/25
 * 描述    : 线程工厂相关的工具
 */
public class WUThreadFactoryUtil {
    private static ThreadFactory threadFactory;

    static {
        threadFactory = new DefaultThreadFactory();
    }

    /**
     * 通过工厂创建新的Thread
     *
     * @param r
     * @return
     */
    public static Thread newThread(Runnable r) {
        return threadFactory.newThread(r);
    }

    /**
     * 获取创建线程的工厂
     *
     * @return
     */
    public static ThreadFactory getThreadFactory() {
        return threadFactory;
    }

    /**
     * 设置创建线程的监听器
     * @param newThreadListener
     */
    public static void setNewThreadListener(NewThreadListener newThreadListener) {
        if (newThreadListener == null) {
            return;
        }
        if (threadFactory instanceof DefaultThreadFactory) {
            ((DefaultThreadFactory) threadFactory).setNewThreadListener(newThreadListener);
        }

    }
}
