package com.wyl.log.handle;

import java.util.HashMap;

/**
 * @author : yuelinwang
 * @time : 2021/6/2
 * @desc : 日志接口
 */
public interface ILog {

    /**
     * 日志收集接口
     * @param key
     * @param params
     */
    void onEvent(String key, HashMap<String, String> params);
}
