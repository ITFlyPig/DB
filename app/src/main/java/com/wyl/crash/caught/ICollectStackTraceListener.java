package com.wyl.crash.caught;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/20
 * 描述    : 堆栈收集的回调
 */
public interface ICollectStackTraceListener {
    /**
     * 捕获到的崩溃堆栈的回调
     * @param summary 摘要
     * @param detail  详情
     */
    void onDone(String summary, String detail);
}
