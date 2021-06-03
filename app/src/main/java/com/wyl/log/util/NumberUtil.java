package com.wyl.log.util;

import android.text.TextUtils;

/**
 * @author : yuelinwang
 * @time : 2021/6/3
 * @desc : 数字工具类
 */
public class NumberUtil {

    /**
     * String -> int
     * @param s
     * @param def
     * @return
     */
    public static int parseInt(String s, int def) {
        if (TextUtils.isEmpty(s)) {
            return def;
        }
        int ret = def;
        try {
            ret = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return ret;

    }
}
