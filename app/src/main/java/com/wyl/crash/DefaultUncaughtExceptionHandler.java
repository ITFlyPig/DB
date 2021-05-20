package com.wyl.crash;

import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/20
 * 描述    : Java 异常捕获器
 */
public class DefaultUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final String SUPPRESSED_CAPTION = "Suppressed: ";

    private static final String CAUSE_CAPTION = "Caused by: ";

    private Thread.UncaughtExceptionHandler prev;

    private ICollectStackTraceListener collectStackTraceListener;

    // 记录当前进程名，避免多次获取，因为有可能需要IPC调用才能获取到
    private String processName;


    public DefaultUncaughtExceptionHandler(Thread.UncaughtExceptionHandler prev, ICollectStackTraceListener collectStackTraceListener) {
        this.prev = prev;
        this.collectStackTraceListener = collectStackTraceListener;
    }

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        // 回调不为空，才有收集的必要
        if (collectStackTraceListener != null) {
            String stackTrace = startCollect(e);
            collectStackTraceListener.onDone(stackTrace);
        }
        // 还原之前存在的异常处理
        if (prev != null) {
            prev.uncaughtException(t, e);
        }
    }

    /**
     * 开始收集堆栈
     *
     * @param e
     */
    private String startCollect(@NonNull Throwable e) {
        StringBuilder builder = new StringBuilder();

        if (TextUtils.isEmpty(processName)) {
            processName = ProcessUtil.processName(Crash.context);
        }
        if (!TextUtils.isEmpty(processName)) {
            int processID = ProcessUtil.processID();
            builder.append("Process: ").append(processName).append(", PID: ").append(processID).append("\n");
        }

        StackTraceElement[] trace = e.getStackTrace();


        Set<Throwable> dejaVu = Collections.newSetFromMap(new IdentityHashMap<Throwable, Boolean>());
        dejaVu.add(e);

        builder.append(e);

        for (StackTraceElement traceElement : trace)
            builder.append("\nat ").append(traceElement);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            for (Throwable se : e.getSuppressed())
                collectEnclosedStackTrace(se, trace, SUPPRESSED_CAPTION, "\n", dejaVu, builder);
        }
        Throwable cause = e.getCause();
        if (cause != null)
            collectEnclosedStackTrace(cause, trace, CAUSE_CAPTION, "", dejaVu, builder);
        return builder.toString();
    }

    private void collectEnclosedStackTrace(Throwable e, StackTraceElement[] enclosingTrace, String caption, String prefix, Set<Throwable> dejaVu, StringBuilder builder) {
        if (e == null) return;
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
            builder.append(prefix).append(caption).append(e);
            for (int i = 0; i <= m; i++)
                builder.append(prefix).append("\nat ").append(trace[i]);

            // 相同的堆栈
            if (framesInCommon != 0)
                builder.append(prefix).append("\t... ").append(framesInCommon).append(" more");

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
}
