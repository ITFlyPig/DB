package com.wyl.log.filter;

import java.util.HashMap;

/**
 * @author : yuelinwang
 * @time : 2021/6/2
 * @desc : 日志过滤接口
 */
public interface ILogFilter {
    /**
     * 决定是否过滤log
     * @param log
     * @return false：不过滤；true：过滤
     */
    boolean filter(HashMap<String, String> log);
}
