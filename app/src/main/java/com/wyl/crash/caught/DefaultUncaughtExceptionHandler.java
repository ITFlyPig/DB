package com.wyl.crash.caught;

import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.wyl.crash.Crash;
import com.wyl.crash.util.ProcessUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Set;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/20
 * 描述    : Java 异常捕获器
 */
public class DefaultUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    private static final String PACKAGE_PREFIX = "com.wyl";

    private static final String SUPPRESSED_CAPTION = "Suppressed: ";

    private static final String CAUSE_CAPTION = "Caused by: ";


    private ICollectStackTraceListener collectStackTraceListener;

    /**
     * 记录当前进程名，避免多次获取，因为有可能需要IPC调用才能获取到
     */
    private String processName;

    private HashMap<Long, Thread.UncaughtExceptionHandler> preMap;


    public DefaultUncaughtExceptionHandler(ICollectStackTraceListener collectStackTraceListene, HashMap<Long, Thread.UncaughtExceptionHandler> preMap) {
        this.collectStackTraceListener = collectStackTraceListene;
        this.preMap = preMap;
    }

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        // 使用try catch，避免在异常处理的过程中又产生新的异常
        try {
            // 判断是否是sdk产生的异常
            if (!isSdkStack(e)) {
                return;
            }
            // 回调不为空，才有收集的必要
            if (collectStackTraceListener != null) {
                // 收集崩溃堆栈
                String stackTrace = startCollect(e);
                // 回调
                collectStackTraceListener.onDone(stackTrace);
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

        // 收集进程信息
        if (TextUtils.isEmpty(processName)) {
            processName = ProcessUtil.processName(Crash.getContext());
        }
        if (!TextUtils.isEmpty(processName)) {
            int processId = ProcessUtil.processID();
            builder.append("Process: ").append(processName).append(", PID: ").append(processId).append("\n");
        }
        StackFmtPrintStream stackFmtPrintStream = new StackFmtPrintStream(System.err);
        // 将调用栈格式化到StackFmtPrintStream

        try {
            e.printStackTrace(stackFmtPrintStream);
        } catch (Exception ee) {
            ee.printStackTrace();
        }
        builder.append(stackFmtPrintStream.getStackFmt());
        return builder.toString();
    }

    private void collectEnclosedStackTrace(Throwable e, StackTraceElement[] enclosingTrace, String caption, String prefix, Set<Throwable> dejaVu, StringBuilder builder) {
        if (e == null) {
            return;
        }
        if (dejaVu.contains(e)) {
            builder.append("\t[CIRCULAR REFERENCE:").append(e).append("]");
        } else {
            dejaVu.add(e);
            StackTraceElement[] trace = e.getStackTrace();
            int m = trace.length - 1;
            int n = enclosingTrace.length - 1;
            while (m >= 0 && n >= 0 && trace[m].equals(enclosingTrace[n])) {
                m--;
                n--;
            }
            int framesInCommon = trace.length - 1 - m;

            // 添加e的堆栈
            builder.append("\n ").append(prefix).append(caption).append(e);
            for (int i = 0; i <= m; i++) {
                builder.append(prefix).append("\n at ").append(trace[i]);
            }

            // 相同的堆栈
            if (framesInCommon != 0) {
                builder.append(prefix).append("\n ... ").append(framesInCommon).append(" more");
            }

            // 添加suppressed exceptions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                for (Throwable se : e.getSuppressed()) {
                    collectEnclosedStackTrace(se, trace, SUPPRESSED_CAPTION, prefix + "\t", dejaVu, builder);
                }
            }

            Throwable cause = e.getCause();
            if (cause != null) {
                collectEnclosedStackTrace(cause, trace, CAUSE_CAPTION, prefix, dejaVu, builder);
            }
        }
    }

    /**
     * 判断是否是属于sdk的异常
     *
     * @return
     */
    private boolean isSdkStack(Throwable e) {
        if (e == null) {
            return false;
        }
        String message = e.getLocalizedMessage();
        if (TextUtils.isEmpty(message)) {
            return false;
        }
        return message.contains(PACKAGE_PREFIX);

    }
}
