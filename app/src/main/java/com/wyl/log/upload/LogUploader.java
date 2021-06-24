package com.wyl.log.upload;

import android.content.ContentValues;

import androidx.annotation.NonNull;

import com.wyl.constant.Urls;
import com.wyl.db.DB;
import com.wyl.db.util.ReflectionUtil;
import com.wyl.http.HttpUtils;
import com.wyl.http.IJsonDataListener;
import com.wyl.log.handle.ShouldUploadRecord;
import com.wyl.log.persistence.LogBean;
import com.wyl.log.persistence.LogStatus;
import com.wyl.log.util.ContentValuesBuilder;
import com.wyl.util.WULogUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * @author : yuelinwang
 * @time : 6/9/21
 * @desc : 上传器
 */
public class LogUploader implements IUpload {
    public static final String TAG = LogUploader.class.getSimpleName();
    /**
     * 标记是否是第一次上传
     */
    private volatile boolean isFirst = true;
    /**
     * 记录本次上传所属的版本
     */
    private long mVersion = 0;
    /**
     * 记录当前表中应该上传的记录条数
     */
    private ShouldUploadRecord mShouldUploadNum;

    public LogUploader(@NonNull ShouldUploadRecord shouldUploadNum) {
        this.mShouldUploadNum = shouldUploadNum;
    }

    @Override
    public void upload() {

        List<LogBean> logs = null;
        long curStatusVersion = LogStatus.INIT;
        // 版本增加1
        if (mVersion + 1 >= Long.MAX_VALUE) {
            mVersion = 1;
        }
        mVersion++;

        String updateWhereClause = null;
        if (isFirst) {
            isFirst = false;
            // 从数据库获取处于init状态的数据或则处于上传状态（之前上传失败且状态修改失败的数据）的数据
            logs = DB.query(LogBean.class, "select * from log_table where status >= ?", String.valueOf(LogStatus.INIT));
            WULogUtils.d(TAG, "第一次上传，获取状态>=INIT的状态的数据");
            updateWhereClause = "status >= ?";
        } else {
            // 从数据库获取处于init状态的数据
            logs = DB.query(LogBean.class, "select * from log_table where status = ?", String.valueOf(LogStatus.INIT));
            WULogUtils.d(TAG, "非第一次上传，获取状态=INIT的状态的数据");
            updateWhereClause = "status = ?";
        }

        int len = logs == null ? 0 : logs.size();
        if (len <= 0) {
            return;
        }

        // 修改获取到的log的状态：UPLOAD + 版本
        curStatusVersion = LogStatus.UPLOADING + mVersion;
        // 将数据库中的数据状态更新为正在上传
        ContentValues values = new ContentValuesBuilder()
                .put("status", String.valueOf(curStatusVersion))
                .build();
        long num = DB.update(LogBean.class, values, updateWhereClause, String.valueOf(LogStatus.INIT));
        WULogUtils.d(TAG, "将已获取的数据，在数据库中的状态修改为：" + curStatusVersion + " 修改的条数：" + num);

        // 状态更新失败，不上传
        if (num <= 0) {
            return;
        }

        // 减少需要上传的记录数
        mShouldUploadNum.decrement(num);
        WULogUtils.d(TAG, "从mShouldUploadNum中删除条数：" + num + " mShouldUploadNum中目前条数： " + mShouldUploadNum.getNum());

        //开始上传
        long finalCurStatusVersion = curStatusVersion;
        IJsonDataListener listener = new IJsonDataListener<Object>() {
            @Override
            public void onSuccess(Object o) {

                // 上传成功，从数据库中删除数据
                long num = DB.delete(LogBean.class, "status = ?", String.valueOf(finalCurStatusVersion));
                mShouldUploadNum.setNum(0);
                WULogUtils.d(TAG, "上传成功，删除数据库中的数据和内存中的记录");
            }

            @Override
            public void onFailed(Object o) {

                // 上传失败，重置log的状态
                ContentValues values = new ContentValuesBuilder().put("status", LogStatus.INIT).build();
                long num = DB.update(LogBean.class, values, "status = ?", String.valueOf(finalCurStatusVersion));
                WULogUtils.d(TAG, "联网上传失败，将所有状态为：" + finalCurStatusVersion + " 的数据状态修改为INIT，修改的条数：" + num);

                // 上传失败
                if (num > 0) {
                    mShouldUploadNum.decrement(num);
                }
            }
        };
//            HttpUtils.<HashMap<String, String>, Object>senRequest(Urls.UPLOAD_LOG, null, Object.class, listener);

        // 测试模拟
        try {
            WULogUtils.d(TAG, "开始联网上传");
            Thread.sleep(random(500, 1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (random(1, 2) % 2 == 0) {
            listener.onSuccess(null);
        } else {
            listener.onFailed(null);
        }
    }

    /**
     * 在[min - max]之间产生随机数
     *
     * @param min
     * @param max
     * @return
     */
    private int random(int min, int max) {
        Random rand = new Random();
        return rand.nextInt(max - min + 1) + min;
    }

}
