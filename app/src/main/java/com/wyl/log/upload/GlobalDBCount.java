package com.wyl.log.upload;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author : yuelinwang
 * @time : 6/9/21
 * @desc : 全局的db条目计数器
 */
public class GlobalDBCount {
    private static long count;

    public static synchronized long addAndGet(long num) {
        count += num;
        return count;
    }

    public static synchronized long decrementAndGet(int num) {
        count -= num;
        if (count < 0) {
            count = 0;
        }
        return count;
    }
}
