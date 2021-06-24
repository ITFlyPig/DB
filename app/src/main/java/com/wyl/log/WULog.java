package com.wyl.log;

import android.text.TextUtils;

import com.wyl.log.filter.ILogFilter;
import com.wyl.log.filter.StackTraceRepetitionFilter;
import com.wyl.log.handle.ILog;
import com.wyl.log.handle.LogConstant;
import com.wyl.log.handle.LogImpl;
import com.wyl.log.handle.LogType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author : yuelinwang
 * @time : 2021/6/2
 * @desc : 日志模块对外暴露的类
 */
public class WULog {
    private static String TAG = WULog.class.getSimpleName();

    private static ILog log;
    private static List<ILogFilter> filters;

    static {
        filters = new ArrayList<>();
        filters.add(new StackTraceRepetitionFilter());
        log = new LogImpl(1000, filters);
    }

    /**
     * 获取日志模块的tag
     *
     * @return
     */
    public static String tag() {
        return TAG;
    }

    /**
     * 收集日志
     *
     * @param type
     * @param params
     */
    private static void onLog(int type, String key, HashMap<String, String> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        // 记录日志的类型
        params.put(LogConstant.LOG_TYPE, String.valueOf(type));
        log.onEvent(key, params);
    }

    /**
     * 业务日志
     *
     * @param key
     */
    public static void onEvent(String key, HashMap<String, String> param) {
        onLog(LogType.BUSINESS_LOG, key, param);
    }

    /**
     * 业务日志
     *
     * @param key
     */
    public static void onEvent(String key) {
        onLog(LogType.BUSINESS_LOG, key, null);
    }


    /**
     * sdk自身需要统计的日志
     *
     * @param key
     * @param params
     */
    public static void sdkLog(String key, HashMap<String, String> params) {
        onLog(LogType.SDK_SELF_LOG, key, params);
    }

    /**
     * 崩溃堆栈
     *
     * @param stackTrace
     */
    public static void onCrash(String stackTrace) {
        if (TextUtils.isEmpty(stackTrace)) {
            return;
        }
        HashMap<String, String> params = new HashMap<>();
        params.put(LogConstant.CONTENT, stackTrace);
        onLog(LogType.CRASH, null, params);
    }
}
