package com.wyl.log.handle;

import com.wyl.db.DB;
import com.wyl.db.DBInitUtil;
import com.wyl.log.persistence.LogBean;
import com.wyl.util.ReflectionTestUtil;

import junit.framework.TestCase;

/**
 * @author : yuelinwang
 * @time : 7/9/21
 * @desc : 日志处理测试
 */
public class HandleLogTaskTest extends TestCase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        DBInitUtil.init();
    }

    /**
     * 测试存储限制
     */
    public void testCheckMaxStoreLimit() {
        // 清空数据库
        DB.delete(LogBean.class, null, null);
        assertEquals(0, DB.count(LogBean.class, null));
        // 插入2w数据
        int num = 12000;
        for (int i = 0; i < num; i++) {
            LogBean bean = new LogBean("test", LogType.BUSINESS_LOG, System.currentTimeMillis());
            DB.insert(bean);
        }
        assertEquals(num, DB.count(LogBean.class, null));

        HandleLogTask handleLogTask = new HandleLogTask(null);
        // 调用检测方法
        ReflectionTestUtil.invokeMethod(handleLogTask, "checkMaxStoreLimit");
        int deleteNum = (int) ReflectionTestUtil.getStaticFieldValue("DELETE_NUM", HandleLogTask.class);
        assertEquals(num - deleteNum, DB.count(LogBean.class, null));
    }

}