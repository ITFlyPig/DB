package com.wyl.log;

import android.content.Context;
import android.text.TextUtils;


import androidx.test.core.app.ApplicationProvider;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;

import com.wyl.MyApplication;
import com.wyl.util.FileUtils;
import com.wyl.util.ReflectionTestUtil;

import junit.framework.TestCase;


import org.hamcrest.integration.EasyMock2Adapter;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;


/**
 * @author : yuelinwang
 * @time : 6/18/21
 * @desc : 测试日志文件工具类
 */

public class LogToDiskUtilTest extends TestCase {
    @Override
    protected void setUp() throws Exception {
        Context cxt = ApplicationProvider.getApplicationContext();
        // 将其注入到的MyApplication
        ReflectionTestUtil.setStaticFieldValue("application", MyApplication.class, cxt);
        assertEquals(cxt, MyApplication.getInstance());
        boolean existDir = (boolean) ReflectionTestUtil.invokeStaticMethod("confirmDir");
        assertTrue(existDir);
    }

    public void testGenerateFileName() {
        assertEquals("debug-log-2021-07-08.txt", LogToDiskUtil.generateFileName(new Date()));
    }

    public void testLogWrite() {
        // 验证日志的确写到文件上了
        // 删除已有的日志文件
        File logFile = (File) ReflectionTestUtil.invokeStaticMethod("makeFileIfNotExist", LogToDiskUtil.generateFileName(new Date()));
        logFile.deleteOnExit();
        // 往日志文件写入log
        String log = "test";
        LogToDiskUtil.onLog(log);
        // 反射获取日志文件
        logFile = invokeMakeFileIfNotExistFile();
        assertNotNull(logFile);
        // 读取文件内容
        try {
            String readLog = FileUtils.readTextFile(logFile, (log + "\n").length(), null);
            assertEquals(log + "\n", readLog);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试删除上一天的日志文件
     */
    public void testDeleteLastLogFile() {
        // 删除已有的日志文件
        File logDir = clearAllLogFile();
        assertEquals(0, logDir.listFiles().length);

        // 创建一份昨天的日志文件
        File lastDayLogFile = new File(logDir.getAbsolutePath() + File.separator + LogToDiskUtil.generateFileName(lastDay()));
        try {
            assertTrue(lastDayLogFile.createNewFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(LogToDiskUtil.generateFileName(lastDay()), lastDayLogFile.getName());

        // 写入文件：先删除昨天的log文件，在写入到今天的log文件
        LogToDiskUtil.onLog(null);
        LogToDiskUtil.onLog("test_today");

        // 只有一份今天的log文件
        assertEquals(1, logDir.listFiles().length);
        assertEquals(LogToDiskUtil.generateFileName(new Date()), logDir.listFiles()[0].getName());
    }


    /**
     * 清除所有的日志文件，并返回日志文件的目录
     * @return
     */
    private File clearAllLogFile() {
        String logDirPath = (String) ReflectionTestUtil.getStaticFieldValue("LOG_DIR", LogToDiskUtil.class);
        File logDir = new File(logDirPath);
        for (File file : logDir.listFiles()) {
            if (file.exists()) {
                file.delete();
            }
        }
        return logDir;
    }


    /**
     * 前一天
     * @return
     */
    private Date lastDay() {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DATE, -1);
        Date lastDay = calendar.getTime();
        return lastDay;
    }


    /**
     * 反射获取、并调用makeFileIfNotExist方法
     *
     * @return
     */
    private File invokeMakeFileIfNotExistFile() {
        Method makeFileIfNotExist = ReflectionTestUtil.getMethod(LogToDiskUtil.class, "makeFileIfNotExist");
        makeFileIfNotExist.setAccessible(true);
        File logFile = null;
        try {
            logFile = (File) makeFileIfNotExist.invoke(null, LogToDiskUtil.generateFileName(new Date()));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return logFile;
    }



    public void testOnCrash() {

    }

    public void testInfo() {

    }

    public void testDebug() {

    }

    public void testError() throws IOException {
        // 清除已存在的日志文件
        clearAllLogFile();
        String tag = "tag";
        String msg = "错误消息";
        Throwable throwable = null;
        String startStr = "-------------error start-------------";
        String endStr = "-------------error end-------------";
        String errorLog = (String) ReflectionTestUtil.invokeStaticMethod("format", tag, msg, throwable, startStr, endStr);
        // 将日志写入到文件
        LogToDiskUtil.error(tag, msg, throwable);

        // 获取日志文件
        File logFile = (File) ReflectionTestUtil.invokeStaticMethod("makeFileIfNotExist", LogToDiskUtil.generateFileName(new Date()));
        // 读取文件内容

        String content = FileUtils.readTextFile(logFile, (int) logFile.length(), null);
        assertEquals(errorLog, content);

        // 测试参数为空的情况
        clearAllLogFile();
        LogToDiskUtil.error(null, null, null);
        assertTrue(FileUtils.readTextFile(logFile, (int) logFile.length(), null).length() > 0);
        clearAllLogFile();
        LogToDiskUtil.error(tag, null, null);
        assertTrue(FileUtils.readTextFile(logFile, (int) logFile.length(), null).length() > 0);
        clearAllLogFile();
        LogToDiskUtil.error(tag, msg, null);
        assertTrue(FileUtils.readTextFile(logFile, (int) logFile.length(), null).length() > 0);


    }
}