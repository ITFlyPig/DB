package com.wyl.log.upload;

import android.content.ContentValues;

import com.wyl.constant.Urls;
import com.wyl.db.DB;
import com.wyl.db.util.ReflectionUtil;
import com.wyl.http.HttpUtils;
import com.wyl.http.IJsonDataListener;
import com.wyl.log.persistence.LogBean;
import com.wyl.log.persistence.LogStatus;
import com.wyl.log.util.ContentValuesBuilder;

import java.util.HashMap;
import java.util.List;

/**
 * @author : yuelinwang
 * @time : 6/9/21
 * @desc : 上传器
 */
public class LogUploader implements IUpload {
    private long mVersion = 0;

    @Override
    public void upload(String data) {

        List<LogBean> logs = null;
        long curStatusVersion = LogStatus.INIT;
        synchronized (LogUploader.class) {
            // 版本增加1
            if (mVersion + 1 >= Long.MAX_VALUE) {
                mVersion = 1;
            }
            mVersion++;
            // 从数据库获取处于init状态的，需要上传的数据
            logs = DB.query(LogBean.class, "select * from log_table where status = ?", String.valueOf(LogStatus.INIT));
            int len = logs == null ? 0 : logs.size();
            if (len > 0) {
                // 修改获取到的log的装填：UPLOAD + 版本
                curStatusVersion = LogStatus.UPLOADING + mVersion;
                long num = DB.update(LogBean.class, "update log_table set status = ? where status = ?", String.valueOf(curStatusVersion), String.valueOf(LogStatus.INIT));
                // 状态更新失败，不上传
                if (num <= 0) {
                    logs = null;
                }
            }
        }

        // 开始上传
        if (logs != null) {
            long finalCurStatusVersion = curStatusVersion;
            HttpUtils.<HashMap<String, String>, Object>senRequest(Urls.UPLOAD_LOG, null, Object.class, new IJsonDataListener<Object>() {
                @Override
                public void onSuccess(Object o) {
                    // 上传成功，从数据库中删除数据
                    long num = DB.delete(LogBean.class, "status = ?", String.valueOf(finalCurStatusVersion));
                    if (num > 0) {
                        String tableName = ReflectionUtil.getTableName(LogBean.class);
                        GlobalCount.decrementAndGet(tableName, num);
                    }

                    // 判断是否还需要触发上传
                    UploadPolicyUtil.checkUpload();
                }

                @Override
                public void onFailed(Object o) {
                    // 上传失败，重置log的状态
                    ContentValues values = new ContentValuesBuilder().put("status", LogStatus.INIT).build();
                    DB.update(LogBean.class, values, "status = ?", String.valueOf(finalCurStatusVersion));
                }
            });
        }
    }
}
