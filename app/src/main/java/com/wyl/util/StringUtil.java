package com.wyl.util;

/**
 * @author : yuelinwang
 * @time : 6/18/21
 * @desc : String工具类
 */
public class StringUtil {
    /**
     * 安全地拼接几个String
     * @param arr
     * @return
     */
    public static String safelyAppend(String ...arr) {
        if (arr == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != null) {
                builder.append(arr[i]);
            } else {
                builder.append("null");
            }
        }
        return builder.toString();
    }
}
