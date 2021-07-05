package com.wyl.crash.util;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import junit.framework.TestCase;

import org.junit.Assert.*;

/**
 * @author : yuelinwang
 * @time : 7/2/21
 * @desc :
 */
public class ProcessUtilTest extends TestCase {

    public void testProcessName() {
        Context cxt = ApplicationProvider.getApplicationContext();
        String name = ProcessUtil.processName(cxt);
        assertEquals("com.wyl.db", name);

        // 参数为空的情况
        name = ProcessUtil.processName(null);
        assertNull(name);

    }
}