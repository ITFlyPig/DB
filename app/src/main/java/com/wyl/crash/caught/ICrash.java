package com.wyl.crash.caught;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/20
 * 描述    : 崩溃捕获的接口，实现分为Java、Native
 */
public interface ICrash {
    /**
     * 安装崩溃捕获器
     */
    void setup(ICollectStackTraceListener collectStackTraceListener);

}
