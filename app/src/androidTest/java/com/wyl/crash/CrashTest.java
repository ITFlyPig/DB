package com.wyl.crash;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.wyl.crash.caught.ICollectStackTraceListener;
import com.wyl.thread.WUThreadFactoryUtil;

import junit.framework.TestCase;

import org.junit.Assert.*;

/**
 * @author : yuelinwang
 * @time : 7/2/21
 * @desc :
 */
public class CrashTest extends TestCase {
    private Context cxt;
    private String needCatchPackageName;
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        cxt = ApplicationProvider.getApplicationContext();
        needCatchPackageName = "com.wyl.crash";
    }

    public void testSetup() {
        // 正常使用
        Crash.setup(cxt, needCatchPackageName, new ICollectStackTraceListener() {
            @Override
            public void onDone(String summary, String detail) {
                assertNotNull(summary);
                assertNotNull(detail);
                assertTrue(summary.contains(needCatchPackageName));
                assertTrue(detail.contains(needCatchPackageName));
            }
        });
        Thread t = WUThreadFactoryUtil.newThread(new Runnable() {
            @Override
            public void run() {
                throw new RuntimeException("测试");
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 测试同一线程多次初始化
    public void testMutilInit() {

        for (int i = 0; i < 10; i++) {
            Crash.setup(cxt, needCatchPackageName, new ICollectStackTraceListener() {
                @Override
                public void onDone(String summary, String detail) {
                    assertNotNull(summary);
                    assertNotNull(detail);
                    assertFalse(summary.contains(needCatchPackageName));
                    assertTrue(detail.contains(needCatchPackageName));
                }
            });
        }
        Thread  t = WUThreadFactoryUtil.newThread(new Runnable() {
            @Override
            public void run() {
                throw new RuntimeException("测试");
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}