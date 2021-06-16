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

@RunWith(AndroidJUnit4.class)
public class DefaultUncaughtExceptionHandlerTest{

    @Test
    public void testIsSdkStack() {
//        String msg = "java.lang.NullPointerException: Attempt to read from field 'java.lang.Long com.wyl.db.c.c.i' on a null object reference";
//        String msg2 = "java.lang.NullPointerException: Attempt to read from field 'java.lang.Long a.aa.db.c.c.i' on a null object reference";
//        DefaultUncaughtExceptionHandler handler = new DefaultUncaughtExceptionHandler(null, null);
//        assert(handler.isSdkStack(new Throwable(msg)));
//        assert(!handler.isSdkStack(new Throwable(msg2)));
    }

    @Test
    public void testStartCollect() {
//        DefaultUncaughtExceptionHandler handler = new DefaultUncaughtExceptionHandler(null, null);
//        Throwable e = new IllegalArgumentException("参数不合法");
//        String stack = handler.startCollect(e);
//        System.out.println("获取到的堆栈：\n" + stack);
    }
}