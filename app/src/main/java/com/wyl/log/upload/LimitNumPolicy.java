package com.wyl.log.upload;

import com.wyl.db.DB;
import com.wyl.db.util.ReflectionUtil;
import com.wyl.db.util.SQLUtil;
import com.wyl.log.persistence.LogBean;

/**
 * @author : yuelinwang
 * @time : 6/8/21
 * @desc : 基于存储数量限制的上传策略
 */
public class LimitNumPolicy implements IUploadPolicy{
    /**
     * 数据库到达5000条触发上传
     */
    private int mLimitNum = 5000;

    /**
     * 设置限制的条数
     * @param limitNum
     */
    public void setLimitNum(int limitNum) {
        this.mLimitNum = limitNum;
    }

    @Override
    public boolean shouldUpload() {

//        return DB.query(LogBean.class, "select count(" + "dd" +") from" + ReflectionUtil.getTableName(LogBean.class), null);
        return false;
    }
}
