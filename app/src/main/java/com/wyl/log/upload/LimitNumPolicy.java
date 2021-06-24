package com.wyl.log.upload;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wyl.db.DB;
import com.wyl.db.util.ReflectionUtil;
import com.wyl.log.handle.ShouldUploadRecord;

/**
 * @author : yuelinwang
 * @time : 6/8/21
 * @desc : 基于存储数量限制的上传策略
 */
public class LimitNumPolicy implements IUploadPolicy {
    public static final int DEFAULT_LIMIT = 1000;
    /**
     * 记录当前表中应该上传的记录条数
     */
    private final ShouldUploadRecord mShouldUploadRecord;
    /**
     * 数据库触发上传的条数
     */
    private int mLimitNum;


    public LimitNumPolicy(int limitNum, @NonNull ShouldUploadRecord shouldUploadRecord) {
        this.mLimitNum = limitNum;
        mShouldUploadRecord = shouldUploadRecord;

    }

    /**
     * 更改限制的条数
     *
     * @param limitNum
     */
    public void setLimitNum(int limitNum) {
        this.mLimitNum = limitNum;
    }

    @Override
    public boolean shouldUpload() {
        if (mLimitNum <= 0) {
            return true;
        }
        return mShouldUploadRecord.getNum() >= mLimitNum;
    }
}
