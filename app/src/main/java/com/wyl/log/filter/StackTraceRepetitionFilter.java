package com.wyl.log.filter;

import android.text.TextUtils;

import com.wyl.log.handle.LogConstant;
import com.wyl.log.handle.LogType;
import com.wyl.log.util.NumberUtil;

import java.util.HashMap;

/**
 * @author : yuelinwang
 * @time : 2021/6/2
 * @desc : 崩溃栈重复log过滤
 */
public class StackTraceRepetitionFilter implements ILogFilter {
    private final int max = 1000;
    private HashMap<Integer, Long> mTimeMap = new HashMap<>(max);

    /**
     * 被认为是重复日志的间隔10ms
     */
    private final long mInterval = 10;

    @Override
    public boolean filter(HashMap<String, String> log) {
        if (mTimeMap.size() >= max) {
            mTimeMap.clear();
        }
        String typeStr = log.get(LogConstant.LOG_TYPE);
        if (TextUtils.isEmpty(typeStr)) {
            return false;
        }

        int type = NumberUtil.parseInt(typeStr, -1);
        if (type == LogType.CRASH) {
            String stackTrace = log.get(LogConstant.CONTENT);
            if (stackTrace != null) {
                int hashCode = stackTrace.hashCode();
                Long lastTime = mTimeMap.get(hashCode);
                mTimeMap.put(hashCode, System.currentTimeMillis());
                if (lastTime != null && System.currentTimeMillis() - lastTime <= mInterval) {
                    return true;
                }
            }
        }
        return false;
    }
}
