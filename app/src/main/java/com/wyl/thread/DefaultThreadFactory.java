package com.wyl.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 默认的线程工厂
 */
public class DefaultThreadFactory implements ThreadFactory {
    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;
    private NewThreadListener newThreadListener;

    public void setNewThreadListener(NewThreadListener newThreadListener) {
        this.newThreadListener = newThreadListener;
    }

   public DefaultThreadFactory() {
        group = Thread.currentThread().getThreadGroup();
        namePrefix = "WU pool-" + poolNumber.getAndIncrement() + "-thread-";
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r,
                namePrefix + threadNumber.getAndIncrement(),
                0);
        if (t.isDaemon()) {
            t.setDaemon(false);
        }
        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        // 回调
        if (newThreadListener != null) {
            newThreadListener.onNewThread(t);
        }
        return t;
    }
}