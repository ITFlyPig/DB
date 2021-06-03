package com.wyl.log.handle;

/**
 * @author : yuelinwang
 * @time : 2021/6/2
 * @desc : 日志类型
 */
public interface LogType {
    /**
     * 未定义
     */
    int NONE = -1;
    /**
     * 崩溃log
     */
    int CRASH = 1;

    /**
     * sdk自身运行的log
     */
    int SDK_SELF_LOG = 2;

    /**
     * 业务的log
     */
    int BUSINESS_LOG = 3;
}
