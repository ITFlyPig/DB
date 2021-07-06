package com.wyl.log.upload;

import android.content.ContentValues;

import com.wyl.db.DB;
import com.wyl.db.DBInitUtil;
import com.wyl.log.handle.LogType;
import com.wyl.log.handle.ShouldUploadRecord;
import com.wyl.log.persistence.LogBean;
import com.wyl.log.persistence.LogStatus;

import junit.framework.TestCase;

import java.util.List;

/**
 * @author : yuelinwang
 * @time : 7/6/21
 * @desc : 测试上传日志的逻辑
 */
public class LogUploaderTest extends TestCase {


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        DBInitUtil.init();

    }

    public void testUpload() {

    }

    /**
     * 测试非首次上传
     */
    public void testNoFirstUpload() {
        // 清空已有的数据
        long rows = DB.delete(LogBean.class, null, null);
        if (rows < 0) {
            throw new RuntimeException("清空数据失败");
        }

        ShouldUploadRecord shouldUploadRecord = new ShouldUploadRecord();
        IUpload iupload = new LogUploader(shouldUploadRecord, new UploadListener() {
            @Override
            public void onSuccess(long version) {
                long num = DB.count(LogBean.class, "status != ?", String.valueOf(LogStatus.INIT));
                assertEquals(1, num);
            }

            @Override
            public void onFail(long version) {
                // 对于非首次上传，如果传输失败，那么只会将失败对应的version的数据置为INIT状态
                // 数据库已有的非INIT数据不会改变
                List<LogBean> logs = DB.query(LogBean.class, "select * from log_table where status = ?", String.valueOf(LogStatus.INIT));
                assertEquals(1, logs.size());

            }
        });

        iupload.upload();


        LogBean logBean1 = newLogBean("bean1");
        LogBean logBean2 = newLogBean("bean2");
        long id = DB.insert(logBean1);
        if (id <= 0) {
            throw new RuntimeException("插入数据库失败");
        }
        id = DB.insert(logBean2);
        if (id <= 0) {
            throw new RuntimeException("插入数据库失败");
        }
        // 将logBean2的状态修改为正在上传的状态
        ContentValues values = new ContentValues();
        values.put("status", LogStatus.INIT + 1);
        rows = DB.update(LogBean.class, values, "id = ?", String.valueOf(id));
        if (rows <= 0) {
            throw new RuntimeException("更新log的状态失败");
        }
        iupload.upload();

    }


    /**
     * 测试初次上传
     */
    public void testFirstUpload() {
        // 清空已有的数据
        long rows = DB.delete(LogBean.class, null, null);
        if (rows < 0) {
            throw new RuntimeException("清空数据失败");
        }
        LogBean logBean1 = newLogBean(System.currentTimeMillis() + "");
        LogBean logBean2 = newLogBean(System.currentTimeMillis() + "");
        long id = DB.insert(logBean1);
        if (id <= 0) {
            throw new RuntimeException("插入数据库失败");
        }
        id = DB.insert(logBean2);
        if (id <= 0) {
            throw new RuntimeException("插入数据库失败");
        }
        // 将logBean2的状态修改为正在上传的状态
        ContentValues values = new ContentValues();
        values.put("status", LogStatus.INIT + 1);
        rows = DB.update(LogBean.class, values, "id = ?", String.valueOf(id));
        if (rows <= 0) {
            throw new RuntimeException("更新log的状态失败");
        }

        ShouldUploadRecord shouldUploadRecord = new ShouldUploadRecord();
        IUpload iupload = new LogUploader(shouldUploadRecord, new UploadListener() {
            @Override
            public void onSuccess(long version) {
                long num = DB.count(LogBean.class, null, null);
                assertEquals(0, num);
            }

            @Override
            public void onFail(long version) {
                List<LogBean> logs = DB.query(LogBean.class, "select * from log_table where status = ?", String.valueOf(LogStatus.INIT));
                assertEquals(2, logs.size());

            }
        });

        iupload.upload();

    }

    /**
     * 便捷创建logbean实例
     *
     * @return
     */
    private LogBean newLogBean(String content) {
        long cur = System.currentTimeMillis();
        return new LogBean(content, LogType.CRASH, cur);
    }
}