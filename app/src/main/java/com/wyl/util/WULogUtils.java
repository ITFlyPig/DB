package com.wyl.util;

import android.os.Build;
import android.util.Log;

import com.wyl.crash.caught.StackFmtPrintStream;
import com.wyl.db.BuildConfig;
import com.wyl.log.LogToDiskUtil;

/**
 * @author : wuchao5
 * @date : 2020/11/27 17:36
 * @desciption :
 */
public class WULogUtils {
  private static final String TAG = "WULog";
  private static boolean enableLog = true;

  public static void d(String tag, String msg) {
    dInfo(TAG + "_" + tag, msg, null);
  }

  public static void d(String tag, String msg, Throwable tr) {
    dInfo(TAG + "_" + tag, msg, tr);
  }

  public static void e(String tag, String msg) {
    eInfo(TAG + "_" + tag, msg, null);
  }

  public static void e(String tag, String msg, Throwable tr) {
    eInfo(TAG + "_" + tag, msg, tr);
  }

  public static void i(String tag, String msg) {
    info(TAG + "_" + tag, msg, null);
  }

  public static void i(String tag, Throwable tr) {
    info(TAG + "_" + tag, "", tr);
  }

  public static void i(String tag, String msg, Throwable tr) {
    info(TAG + "_" + tag, msg, tr);
  }

  public static void info(String tag, String msg, Throwable tr) {
    try {
      if (enableLog) {
        Log.i(tag, msg, tr);
          LogToDiskUtil.info(tag, msg, tr);
      }
    } catch (Exception e) {
      printStackTrace(e);
    }
  }

  public static void dInfo(String tag, String msg, Throwable tr) {
    try {
      if (enableLog) {
        Log.d(tag, msg, tr);
          LogToDiskUtil.debug(tag, msg, tr);
      }
    } catch (Exception e) {
      printStackTrace(e);
    }
  }

  public static void eInfo(String tag, String msg, Throwable tr) {
    try {
      if (enableLog) {
        Log.e(tag, msg, tr);

       LogToDiskUtil.error(tag, msg, tr);
      }
    } catch (Exception e) {
      printStackTrace(e);
    }
  }

  /**
   * 插件配置 disableLog 会修改此方法
   *
   * @param e Exception
   */
  public static void printStackTrace(Exception e) {
    if (enableLog && e != null) {
      e.printStackTrace();
    }
  }

  /**
   * 设置是否打印 Log
   *
   * @param isEnableLog Log 状态
   */
  public static void setEnableLog(boolean isEnableLog) {
    enableLog = isEnableLog;
  }

  public static boolean isLogEnabled() {
    return enableLog;
  }
}
