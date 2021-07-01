package com.wyl.log;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * @author : yuelinwang
 * @time : 6/18/21
 * @desc :
 */

@RunWith(AndroidJUnit4.class)
public class LogToDiskUtilTest {

    @Test
    public void testGenerateFileName() {
        assertEquals("debug-log-2021-06-18.txt", LogToDiskUtil.generateFileName(new Date()));
    }
}