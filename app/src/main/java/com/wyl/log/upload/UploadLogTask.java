package com.wyl.log.upload;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author : yuelinwang
 * @time : 6/9/21
 * @desc : 上传日志的任务
 */
public class UploadLogTask implements Runnable{
    /**
     * 触发上传的策略
     */
    private IUploadPolicy mUploadPolicy;

    /**
     * 是否停止
     */
    private volatile boolean isStop;

    private LinkedBlockingQueue<Boolean> mBlockingQueue;

    public UploadLogTask(IUploadPolicy uploadPolicy, LinkedBlockingQueue<Boolean> blockingQueue) {
        this.mUploadPolicy = uploadPolicy;
        this.mBlockingQueue = blockingQueue;
        if (mUploadPolicy == null || blockingQueue == null) {
            throw new IllegalArgumentException("上传策略参数：uploadPolicy 或  blockingQueue 不能为null");
        }
    }

    public void setStop(boolean stop) {
        isStop = stop;
    }

    @Override
    public void run() {
        while (!isStop) {
            try {
                mBlockingQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!mUploadPolicy.shouldUpload()) {
                continue;
            }
            // 开始上传log
        }

    }
}
