package com.wyl.db;

import android.content.Context;

import java.util.ArrayList;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/8
 * 描述    : 对外暴露的数据库操作
 */
public class DB {
    public static Context context;
    private static IOperation operation;

    public static void init(Context context) {
        DB.context = context;
        operation = new WUOperationImpl();
    }


    /**
     * 插入
     * @param bean
     * @param <T>
     * @return
     */
    public static <T> long insert(T bean) {
        return operation.<T>insert(bean);
    }

    /**
     * 查询
     * @param sql 查询的sql语句
     * @param <T>
     * @return
     */
    public static <T> ArrayList<T> query(String sql){
        return null;
    }


}
