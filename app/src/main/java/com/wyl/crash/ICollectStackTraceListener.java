package com.wyl.crash;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/20
 * 描述    : 堆栈收集的回调
 */
public interface ICollectStackTraceListener {
    void onDone(String stackTrace);
}
