package com.wyl.log.upload;

import com.wyl.db.DB;
import com.wyl.db.util.ReflectionUtil;

/**
 * @author : yuelinwang
 * @time : 6/8/21
 * @desc : 基于存储数量限制的上传策略
 */
public class LimitNumPolicy implements IUploadPolicy{
    public static final int DEFAULT_LIMIT = 5000;
    private Class<?> mTableClz;
    private String mTableName;
    /**
     * 数据库触发上传的条数
     */
    private int mLimitNum;

    public LimitNumPolicy(Class<?> tableClz, int limitNum) {
        this.mLimitNum = limitNum;
        mTableClz = tableClz;
        mTableName = ReflectionUtil.getTableName(tableClz);

    }

    /**
     * 更改限制的条数
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
        // 尝试从内存获取记录的数据条数
        long curNum = GlobalCount.get(mTableName);
        if (curNum == 0) {
            // 从数据库获取最新的条数
            long count = DB.count(mTableClz, null, null);
            if (count < 0) {
                // 查询错误，直接触发上传
                return true;
            } else {
                curNum = count;
                GlobalCount.addAndGet(mTableName, curNum);
            }

        }
        return curNum >= mLimitNum;
    }
}
