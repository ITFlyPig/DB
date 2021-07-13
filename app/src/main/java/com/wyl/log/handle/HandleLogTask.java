package com.wyl.log.handle;

import android.text.TextUtils;

import com.wyl.db.DB;
import com.wyl.log.persistence.LogBean;
import com.wyl.log.upload.IUpload;
import com.wyl.log.upload.IUploadPolicy;
import com.wyl.log.upload.LimitNumPolicy;
import com.wyl.log.upload.LogUploader;
import com.wyl.log.util.NumberUtil;
import com.wyl.temp.JsonUtils;
import com.wyl.util.WULogUtils;

import org.json.JSONException;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author : yuelinwang
 * @time : 6/24/21
 * @desc : 处理日志的任务
 */
public class HandleLogTask implements Runnable{
    public static final String TAG = HandleLogTask.class.getSimpleName();
    /**
     * 表示是否停止处理日志
     */
    private volatile boolean isStop;
    /**
     * 存储任务的队列
     */
    private LinkedBlockingQueue<HashMap<String, String>> mLogQueue;

    /**
     * 记录当前表中应该上传的记录条数
     */
    private ShouldUploadRecord mShouldUploadNum;
    /**
     * 触发上传的策略
     */
    private IUploadPolicy mUploadPolicy;
    /**
     * 上传器
     */
    private IUpload mUploader;
    /**
     * 每次触发删除，应该删除的条数
     */
    private static final int DELETE_NUM = 100;

    /**
     * 数据库最大存储条数，超过了，就把最久的删除了在存储
     */
    private int mMaxStoreNum = 10000;

    public HandleLogTask(LinkedBlockingQueue<HashMap<String, String>> logQueue) {
        this.mLogQueue = logQueue;
        mShouldUploadNum = new ShouldUploadRecord();
        mUploadPolicy = new LimitNumPolicy( LimitNumPolicy.DEFAULT_LIMIT, mShouldUploadNum);
        mUploader = new LogUploader(mShouldUploadNum, null);
    }

    /**
     * 可更改条数限制
     * @param maxStoreNum
     */
    public void setMaxStoreNum(int maxStoreNum) {
        this.mMaxStoreNum = maxStoreNum;
    }

    @Override
    public void run() {
        WULogUtils.d(TAG, "启动日志处理线程");
        while (!isStop) {
            HashMap<String, String> log = null;
            try {
                WULogUtils.d(TAG, "从队列中获取日志");
                log = mLogQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (log == null) {
                continue;
            }
            // 检查一下是否达到数据库存储限制
            checkMaxStoreLimit();

            int type = NumberUtil.parseInt(log.get(LogConstant.LOG_TYPE), LogType.NONE);
            // Map -> json
            String json = toJson(log);
            if (TextUtils.isEmpty(json)) {
                continue;
            }
            // json -> bean
            LogBean logBean = new LogBean(json, type, System.currentTimeMillis());
            //插入到数据库
            long id = DB.insert(logBean);
            // 如果插入发生错误，则直接丢弃日志
            if (id <0) {
                WULogUtils.d(TAG, "将日志插入到数据库失败");
                continue;
            }
            WULogUtils.d(TAG, "将日志插入到数据库成功");
            if (mShouldUploadNum.getNum() < 0) {
                // 表示启动后第一次使用，从数据库读应该上传的数量
                mShouldUploadNum.setNum(countAll());
            } else {
                // 更新内存中记录的数量
                mShouldUploadNum.add(1);
            }

            WULogUtils.d(TAG, "当前应该上传的数据量：" + mShouldUploadNum.getNum());

            // 据记录的数量检查是否应该上传
            if (!mUploadPolicy.shouldUpload()) {
                WULogUtils.d(TAG, "未达到上传条件");
                continue;
            }
            WULogUtils.d(TAG, "开始上传");
            // 开始上传
            mUploader.upload();
        }
    }

    /**
     * 检查最大存储的限制
     */
    private void checkMaxStoreLimit() {
        long num = countAll();
        if (num >= mMaxStoreNum) { // 应该删除最久的DELETE_NUM条数据
            DB.delete(LogBean.class, " where id in (select id from log_table order by id limit 0, ?)", String.valueOf(100) );
        }
    }

    /**
     * 查询应该上传的数据的条数
     * @return
     */
    private long countAll() {
        // 查询数据库中的条数
        long num = DB.count(LogBean.class, null, null);
        return num < 0 ? 0 : num;
    }

    /**
     * 停止任务的处理
     */
    public void stop() {
        isStop = true;
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
