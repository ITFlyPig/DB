package com.wyl.crash.caught;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.wyl.crash.Crash;
import com.wyl.crash.util.ProcessUtil;

import java.util.HashMap;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/20
 * 描述    : Java 异常捕获器
 */
public class DefaultUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    /**
     * 需要捕获异常的代码的包的前缀
     */
    private String mPackagePrefix;
    /**
     * 摘要和详情的分割
     */
    private static final String SPLIT = "at ";

    private ICollectStackTraceListener collectStackTraceListener;

    /**
     * 记录当前进程名，避免多次获取，因为有可能需要IPC调用才能获取到
     */
    private String processName;

    private HashMap<Long, Thread.UncaughtExceptionHandler> preMap;


    public DefaultUncaughtExceptionHandler(ICollectStackTraceListener collectStackTraceListene, HashMap<Long, Thread.UncaughtExceptionHandler> preMap, String packagePrefix) {
        this.mPackagePrefix = packagePrefix;
        this.collectStackTraceListener = collectStackTraceListene;
        this.preMap = preMap;
    }

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        // 使用try catch，避免在异常处理的过程中又产生新的异常
        try {
            // 回调不为空，才有收集的必要
            if (collectStackTraceListener != null) {
                // 收集崩溃堆栈
                String stackTrace = startCollect(e);
                // 判断堆栈是否是sdk产生的
                if (!isSdkStack(stackTrace)) {
                    return;
                }
                // 回调
                String[] arr = parseSummaryAndDetail(stackTrace);
                if (arr == null || arr.length != 2) {
                    return;
                }
                collectStackTraceListener.onDone(arr[0], arr[1]);
            }

            // 恢复用户设置的处理
            if (preMap != null) {
                Thread.UncaughtExceptionHandler preHandler = preMap.get(t.getId());
                if (preHandler != null) {
                    preHandler.uncaughtException(t, e);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }


    }

    /**
     * 开始收集堆栈
     *
     * @param e
     */
    private String startCollect(@NonNull Throwable e) {
        StringBuilder builder = new StringBuilder();
        StackFmtPrintStream stackFmtPrintStream = new StackFmtPrintStream(System.err);
        // 将调用栈格式化到StackFmtPrintStream
        e.printStackTrace(stackFmtPrintStream);
        builder.append(stackFmtPrintStream.getStackFmt());
        return builder.toString();
    }

    /**
     * 判断是否是属于sdk的异常
     *
     * @return
     */
    private boolean isSdkStack(String stack) {

        if (TextUtils.isEmpty(stack) || TextUtils.isEmpty(mPackagePrefix)) {
            return false;
        }
        return stack.contains(mPackagePrefix);
    }

    /**
     * 解析得到摘要和详情
     *
     * @param stackTrace
     * @return
     */
    private String[] parseSummaryAndDetail(String stackTrace) {
        if (TextUtils.isEmpty(stackTrace)) {
            return null;
        }
        int index = stackTrace.indexOf(SPLIT);
        if (index > 0 && index < stackTrace.length()) {
            String summary = stackTrace.substring(0, index);
            String detail = stackTrace.substring(index, stackTrace.length());
            return new String[]{summary, (detail == null ? detail : (getProcessInfo() + detail))};
        }
        return null;
    }

    /**
     * 获取进程相关的信息
     *
     * @return
     */
    private String getProcessInfo() {
        // 收集进程信息
        if (TextUtils.isEmpty(processName)) {
            processName = ProcessUtil.processName(Crash.getContext());
        }
        if (!TextUtils.isEmpty(processName)) {
            int processId = ProcessUtil.processID();
            return "Process: " + processName + ", PID: " + processId + "\n";
        }
        return "";
    }
}
