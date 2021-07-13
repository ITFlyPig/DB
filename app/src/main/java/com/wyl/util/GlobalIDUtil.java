package com.wyl.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author : yuelinwang
 * @time : 7/13/21
 * @desc : 全局自增id：每次获取到的id是不一样的  id = "appkey + 时间戳 + 计数"
 */
public class GlobalIDUtil {
    // 当前的时间
    private static AtomicLong cur = new AtomicLong(0);
    private static String appKey = "";

    /**
     * 设置appkey，也可以不设置，但是不设置可能就导致和其他的app有可能重复，但是本app不会重复
     *
     * @param appKey
     */
    public static void setAppKey(String appKey) {
        if (appKey == null) return;
        GlobalIDUtil.appKey = appKey;
    }

    /**
     * 获取全局自增的id
     *
     * @return
     */
    public static String getID() {
        // 避免溢出
        long id = cur.incrementAndGet();
        if (id == Long.MAX_VALUE) {
            // 重置
            cur.set(0);
        }
        return appKey + System.currentTimeMillis() + cur;
    }
}
