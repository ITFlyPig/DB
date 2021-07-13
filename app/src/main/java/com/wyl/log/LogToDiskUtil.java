package com.wyl.log;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.wyl.MyApplication;
import com.wyl.crash.caught.StackFmtPrintStream;
import com.wyl.util.FileUtils;
import com.wyl.util.StringUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author : yuelinwang
 * @time : 6/18/21
 * @desc : 将日志保存到文件的工具类
 */
public class LogToDiskUtil {
    private static String LOG_DIR;
    /**
     * 标记是否已清除上一天的日志文件
     */
    private static volatile boolean isDeleteLastDayLog = false;

    /**
     * 确认是否设置了日志的保存目录
     */
    private static boolean confirmDir() {
        if (!TextUtils.isEmpty(LOG_DIR)) {
            return true;
        }
        //获取全局的Context
        Context cxt = MyApplication.getInstance();
        if (cxt == null) {
            return false;
        }
        // 存放debug log的目录
        LOG_DIR = cxt.getCacheDir() + File.separator + "debug_log";
        return true;
    }

    /**
     * 目录不存在，则创建目录
     *
     * @return true：目录存在 false：目录不存在
     */
    private static boolean makeDirIfNotExist() {
        if (TextUtils.isEmpty(LOG_DIR)) {
            return false;
        }
        File dir = new File(LOG_DIR);
        if (dir.exists()) {
            return true;
        }
        return dir.mkdirs();
    }

    /**
     * 如果文件不存在，则创建文件
     *
     * @param fileName
     * @return 对应的文件
     */
    private static File makeFileIfNotExist(@NonNull String fileName) {
        if (TextUtils.isEmpty(LOG_DIR)) {
            return null;
        }
        File f = new File(LOG_DIR + File.separator + fileName);
        if (f.exists()) {
            return f;
        }
        try {
            if (f.createNewFile()) {
                return f;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 生成文件名
     *
     * @return
     */
    public static String generateFileName(@NonNull Date date) {
        String strDateFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat, Locale.CHINA);
        return "debug-log-" + sdf.format(date) + ".txt";
    }

    /**
     * 删除今天之前保存log的文件
     */
    private static void deletePreLog() {
        File logDir = new File(LOG_DIR);
        File[] logFiles = logDir.listFiles();
        if (logFiles == null) {
            return;
        }
        String todayLogFileName = generateFileName(now());
        for (File file : logFiles) {
            String preLogFileName = file.getName();
            if (file.exists() && !TextUtils.equals(preLogFileName, todayLogFileName)) {
                file.delete();
            }
        }
    }

    /**
     * 保存日志
     *
     * @param log
     */
    public static void onLog(String log) {
        if (TextUtils.isEmpty(log)) {
            return;
        }
        // 检查是否设置了日志保存的目录
        if (!confirmDir()) {
            return;
        }
        //检查磁盘上目录是否存在
        if (!makeDirIfNotExist()) {
            return;
        }
        // 检查上一天的日志是否删除了
        if (!isDeleteLastDayLog) {
            deletePreLog();
            isDeleteLastDayLog = true;
        }
        // 将今天的debug log写到文件

        //确保记录日志的文件存在
        File logFile = makeFileIfNotExist(generateFileName(now()));
        if (logFile == null) {
            return;
        }
        try {
            FileUtils.stringToFile(logFile, log);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 当前时间
     *
     * @return
     */
    private static Date now() {
        return new Date();
    }

    /**
     * 捕获到崩溃堆栈时保存
     *
     * @param stackTrace
     */
    public static void onCrash(String stackTrace) {
        if (TextUtils.isEmpty(stackTrace)) {
            return;
        }
        String log = StringUtil.safelyAppend("-------------crash start-------------\n", stackTrace, "\n-------------crash start-------------");
        onLog(log);
    }


    /**
     * 记录info日志
     *
     * @param tag
     * @param msg
     * @param tr
     */
    public static void info(String tag, String msg, Throwable tr) {
        String log = format(tag, msg, tr, "-------------info start-------------", "-------------info end-------------");
        onLog(log);
    }

    /**
     * 记录debug日志
     *
     * @param tag
     * @param msg
     * @param tr
     */
    public static void debug(String tag, String msg, Throwable tr) {
        String log = format(tag, msg, tr, "-------------debug start-------------", "-------------debug end-------------");
        onLog(log);
    }

    /**
     * 记录error日志
     *
     * @param tag
     * @param msg
     * @param tr
     */
    public static void error(String tag, String msg, Throwable tr) {
        String log = format(tag, msg, tr, "-------------error start-------------", "-------------error end-------------");
        onLog(log);
    }

    /**
     * 格式化日志
     *
     * @param tag
     * @param msg
     * @param tr
     * @return
     */
    private static String format(String tag, String msg, Throwable tr, String startStr, String endStr) {
        return StringUtil.safelyAppend(startStr, "\n",
                "tag:", tag, "\n",
                "msg:", msg, "\n",
                "throwable:", getThrowableFmt(tr), "\n",
                endStr, "\n");
    }

    /**
     * 获得格式化的调用栈
     *
     * @param tr
     * @return
     */
    private static String getThrowableFmt(Throwable tr) {
        if (tr == null) {
            return null;
        }
        StackFmtPrintStream stackFmtPrintStream = new StackFmtPrintStream(System.err);
        tr.printStackTrace(stackFmtPrintStream);
        return stackFmtPrintStream.getStackFmt().toString();
    }

}
