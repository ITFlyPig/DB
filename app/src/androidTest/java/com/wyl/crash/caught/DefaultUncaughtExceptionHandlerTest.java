package com.wyl.crash.caught;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author : yuelinwang
 * @time : 6/10/21
 * @desc :
 */

public class DefaultUncaughtExceptionHandlerTest extends TestCase{
    private DefaultUncaughtExceptionHandler handler;

    @Override
    protected void setUp() throws Exception {
        super.setUp();


    }

    public void testUncaughtException() {
        Thread  t = Thread.currentThread();
        // 参数为空
        handler = new DefaultUncaughtExceptionHandler(new ICollectStackTraceListener() {
            @Override
            public void onDone(String summary, String detail) {
                assertNull(summary);
                assertNull(detail);
            }
        }, null, "com.wyl.db");
        handler.uncaughtException(null, null);

        // 包名前缀不对
        handler = new DefaultUncaughtExceptionHandler(new ICollectStackTraceListener() {
            @Override
            public void onDone(String summary, String detail) {
                assertNull(summary);
                assertNull(detail);
            }
        }, null, "com.wyl.db");
        handler.uncaughtException(t, new IllegalArgumentException("测试参数不合法"));

        // 包名前缀正确
        handler = new DefaultUncaughtExceptionHandler(new ICollectStackTraceListener() {
            @Override
            public void onDone(String summary, String detail) {
                assertNotNull(summary);
                assertNotNull(detail);
            }
        }, null, "com.wyl.crash");
        handler.uncaughtException(t, new IllegalArgumentException("测试参数不合法"));

        // 测试处理异常的过程中发生异常
        handler = new DefaultUncaughtExceptionHandler(new ICollectStackTraceListener() {
            @Override
            public void onDone(String summary, String detail) {
                assertNotNull(summary);
                assertNotNull(detail);
                throw new RuntimeException("测试处理异常时发生异常");
            }
        }, null, "com.wyl.crash");
        handler.uncaughtException(t, new IllegalArgumentException("测试参数不合法"));


    }
}