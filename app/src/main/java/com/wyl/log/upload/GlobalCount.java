package com.wyl.log.upload;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author : yuelinwang
 * @time : 6/8/21
 * @desc : 全局的计数器
 */
public class GlobalCount {

    /**
     * 可以提高读性能
     */
    private static HashMap<String, AtomicLong> coutMap = new HashMap<>();

    /**
     * 增加 然后 返回
     *
     * @param key
     * @param num
     */
    public static long addAndGet(String key, long num) {
        if (TextUtils.isEmpty(key)) {
            return 0;
        }
        AtomicLong count = coutMap.get(key);
        if (count == null) {
            // 同步处理
            synchronized (GlobalCount.class) {
                count = coutMap.get(key);
                if (count == null) {
                    count = new AtomicLong(0);
                    coutMap.put(key, count);
                }
            }
        }
        // 如果溢出，则置为0
        if (count.get() < 0) {
            count.set(0);
        }

        return count.addAndGet(num);
    }

    /**
     * 获取对应的计数
     *
     * @param key
     * @return
     */
    public static long get(String key) {
        if (TextUtils.isEmpty(key)) {
            return 0;
        }
        AtomicLong count = coutMap.get(key);
        if (count == null) {
            return 0;
        }
        return count.get();
    }

    /**
     * 减法  然后返回值
     *
     * @param key
     * @param num
     * @return
     */
    public static long decrementAndGet(String key, long num) {
        if (TextUtils.isEmpty(key)) {
            return 0;
        }

        AtomicLong count = coutMap.get(key);
        if (count == null) {
            return 0;
        }

        //记录为负，返回0
        if (count.get() < 0) {
            return 0;
        }
        return count.addAndGet(-num);

    }


}
