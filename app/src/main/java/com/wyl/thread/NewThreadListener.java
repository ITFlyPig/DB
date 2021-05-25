package com.wyl.thread;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/25
 * 描述    : 创建Thread的 Listener
 */
public interface NewThreadListener {
    /**
     * 当创建Thread的时候回调
     * @param t
     */
    void onNewThread(Thread t);
}
