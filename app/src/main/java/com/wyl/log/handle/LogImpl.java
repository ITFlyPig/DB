package com.wyl.log.handle;

import com.wyl.db.util.LogUtil;
import com.wyl.log.WULog;
import com.wyl.log.filter.ILogFilter;
import com.wyl.temp.JsonUtils;
import com.wyl.thread.WUThreadFactoryUtil;
import com.wyl.util.WULogUtils;

import org.json.JSONException;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author : yuelinwang
 * @time : 2021/6/2
 * @desc : 日志收集实现类
 */
public class LogImpl implements ILog {
    public static final String TAG = LogImpl.class.getSimpleName();

    /**
     * 存放待处理日志的队列
     */
    private LinkedBlockingQueue<HashMap<String, String>> mLogQueue;

    /**
     * 队列的容量
     */
    private int mQueueCapacity;

    /**
     * 日志过滤集合
     */
    private List<ILogFilter> mFilters;

    /**
     * 记录日志丢弃的数量
     */
    private AtomicLong mDiscardNum;

    /**
     * 处理日志的任务
     */
    private HandleLogTask mHandleLogTask;

    public LogImpl(int queueCapacity, List<ILogFilter> filters) {
        if (queueCapacity <= 0) {
            throw new IllegalArgumentException("参数：队列容量-queueCapacity必须为正数");
        }
        mFilters = filters;
        mQueueCapacity = queueCapacity;
        mDiscardNum = new AtomicLong(0);
        mLogQueue = new LinkedBlockingQueue<>(mQueueCapacity);
        mHandleLogTask = new HandleLogTask(mLogQueue);
        startHandleLogThread();
    }

    /**
     * 开启处理日志的线程
     */
    private void startHandleLogThread() {
        WUThreadFactoryUtil.newThread(mHandleLogTask).start();
    }

    /**
     * 非阻塞式的将日志放到队列中
     *
     * @param log
     */
    private void offer(HashMap<String, String> log) {
        if (log == null) {
            return;
        }
        // offer返回true，表示将日志成功放到队列中
        if (mLogQueue.offer(log)) {
            WULogUtils.d(TAG, "成功将日志放入队列，等待上传");
            return;
        }

        // 队列满了，为了避免OOM，直接将日志丢弃，记录每天丢弃的数量
        if (mDiscardNum.get() < Long.MAX_VALUE) {
            mDiscardNum.getAndIncrement();
        }
        LogUtil.d(WULog.tag(), "队列已满，丢弃日志，当前已丢弃的日志的数量：" + mDiscardNum.get());
    }


    @Override
    public void onEvent(String key, HashMap<String, String> params) {

        if (params == null) {
            params = new HashMap<>();
        }
        // 记录事件的key
        params.put(LogConstant.LOG_KEY, key);
        // 记录时间
        params.put(LogConstant.LOG_TIME, String.valueOf(System.currentTimeMillis()));
        //  记录线程简单的信息
        params.put(LogConstant.LOG_THREAD, getThreadInfo());

        try {
            WULogUtils.d(TAG, "onEvent接收到日志：\n" + JsonUtils.toJSONString(params));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        // 尝试过滤日志
        if (tryFilter(params)) {
            WULogUtils.d(TAG, "日志被过滤");
            return;
        }

        // 将日志放到队列
        offer(params);
    }

    /**
     * 尝试过滤日志
     *
     * @param log
     * @return
     */
    private boolean tryFilter(HashMap<String, String> log) {
        if (mFilters == null) {
            return false;
        }
        for (ILogFilter filter : mFilters) {
            if (filter.filter(log)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取线程相关的信息
     *
     * @return
     */
    private String getThreadInfo() {
        Thread t = Thread.currentThread();
        return "name:" + t.getName() + " id:" + t.getId();
    }

    /**
     * 丢弃日志数量的log
     *
     * @param discardNum
     * @return
     */
    private HashMap<String, String> discardNumLog(long discardNum) {

        HashMap<String, String> params = new HashMap<>();
        // 记录日志的类型
        params.put(LogConstant.LOG_TYPE, String.valueOf(LogType.SDK_SELF_LOG));
        // 记录key
        params.put(LogConstant.LOG_KEY, LogConstant.DISCARD_KEY);
        // 记录丢弃的数量
        params.put(LogConstant.DISCARD_NUM, String.valueOf(discardNum));
        // 记录时间
        params.put(LogConstant.LOG_TIME, String.valueOf(System.currentTimeMillis()));
        //  记录线程简单的信息
        params.put(LogConstant.LOG_THREAD, getThreadInfo());
        return params;
    }
}
