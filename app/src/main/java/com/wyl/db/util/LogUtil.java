package com.wyl.db.util;

import android.util.Log;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/24
 * 描述    : 日志工具类
 * @author yuelinwang
 */
public class LogUtil {

    public static void w(String tag, String msg) {
        Log.w(tag,  msg);
    }

    public static void d(String tag, String msg) {
        Log.d(tag,  msg);
    }

    public static void e(String tag, String msg) {
        Log.e(tag,  msg);
    }
}
