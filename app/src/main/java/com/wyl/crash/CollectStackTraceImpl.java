package com.wyl.crash;

import android.util.Log;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/20
 * 描述    : 当崩溃堆栈收集完成之后的处理
 */
public class CollectStackTraceImpl implements ICollectStackTraceListener{
    public static final String TAG = CollectStackTraceImpl.class.getSimpleName();
    @Override
    public void onDone(String stackTrace) {
        // 简单的打印
        Log.e(TAG, "onDone: \n" + stackTrace);

    }
}
