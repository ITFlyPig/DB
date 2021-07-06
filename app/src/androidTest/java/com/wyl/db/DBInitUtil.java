package com.wyl.db;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.wyl.db.bean.User;
import com.wyl.db.converter.TypeConverters;
import com.wyl.log.persistence.LogBean;

/**
 * @author : yuelinwang
 * @time : 7/1/21
 * @desc : db初始化的工具类
 */
public class DBInitUtil {
    /**
     * 单元测试环境的数据库初始化
     */
    public static void init() {
        Context cxt = ApplicationProvider.getApplicationContext();
        DBConfiguration conf = new DBConfiguration.Builder()
                .setContext(cxt)
                .setDbName("test.db")
                .setVersion(1)
                .setConverter(new TypeConverters())
                .setEntities(User.class, LogBean.class)
                .build();

        DB.init(conf);
    }
}
