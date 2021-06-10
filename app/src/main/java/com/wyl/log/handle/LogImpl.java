package com.wyl.log.handle;

import com.wyl.db.DB;
import com.wyl.db.util.LogUtil;
import com.wyl.db.util.ReflectionUtil;
import com.wyl.log.Log;
import com.wyl.log.filter.ILogFilter;
import com.wyl.log.persistence.LogBean;
import com.wyl.log.upload.GlobalCount;
import com.wyl.log.upload.UploadPolicyUtil;
import com.wyl.log.util.NumberUtil;
import com.wyl.temp.JsonUtils;
import com.wyl.thread.WUThreadFactoryUtil;

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
    /**
     * 存放待处理日志的队列
     */
    private LinkedBlockingQueue<HashMap<String, String>> mLogQueue;

    /**
     * 队列的容量
     */
    private int mQueueCapacity;

    /**
     * 表示是否停止处理日志
     */
    private volatile boolean isStop;

    /**
     * 日志过滤集合
     */
    private List<ILogFilter> mFilters;

    /**
     * 记录日志丢弃的数量
     */
    private AtomicLong mDiscardNum;

    public LogImpl(int queueCapacity, List<ILogFilter> filters) {
        if (queueCapacity <= 0) {
            throw new IllegalArgumentException("参数：队列容量-queueCapacity必须为正数");
        }
        mFilters = filters;
        mQueueCapacity = queueCapacity;
        mDiscardNum = new AtomicLong(0);
        mLogQueue = new LinkedBlockingQueue<>(mQueueCapacity);
        startHandleLogThread();
    }

    /**
     * 开启处理日志的线程
     */
    private void startHandleLogThread() {
        WUThreadFactoryUtil.newThread(new Runnable() {
            @Override
            public void run() {
                while (!isStop) {
                    HashMap<String, String> log = null;
                    try {
                        log = mLogQueue.take();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (log == null) {
                        continue;
                    }

                    int type = NumberUtil.parseInt(log.get(LogConstant.LOG_TYPE), LogType.NONE);
                    LogBean logBean = new LogBean(toJson(log), type, System.currentTimeMillis());
                    //插入到数据库
                    long id = DB.insert(logBean);

                    if (id > 0) {
                        String tableName = ReflectionUtil.getTableName(LogBean.class);
                        GlobalCount.addAndGet(tableName, 1);
                        // 检查是否应该上传
                        UploadPolicyUtil.checkUpload();
                    }


                    // 持久化已丢弃的日志数量
                    if (mDiscardNum.get() > 0) {
                        long discardNum = mDiscardNum.getAndSet(0);
                        HashMap<String, String> discardMap = discardNumLog(discardNum);
                        LogBean discardLog = new LogBean(toJson(discardMap), LogType.SDK_SELF_LOG, System.currentTimeMillis());
                        DB.insert(discardLog);
                    }

                }
            }
        }).start();
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
            LogUtil.d(Log.tag(), "成功将日志放入队列");
            return;
        }

        // 队列满了，为了避免OOM，直接将日志丢弃，记录每天丢弃的数量

        if (mDiscardNum.get() < Long.MAX_VALUE) {
            mDiscardNum.getAndIncrement();
        }
        LogUtil.d(Log.tag(), "队列已满，丢弃日志，当前已丢弃的日志的数量：" + mDiscardNum.get());
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

        // 尝试过滤日志
        if (tryFilter(params)) {
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

    /**
     * 对象 -> json
     *
     * @param obj
     * @return
     */
    private String toJson(Object obj) {
        if (obj == null) {
            return "";
        }
        String json = null;
        try {
            json = JsonUtils.toJSONString(obj);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return json;
    }
}
