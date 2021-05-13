package com.wyl.db;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/8
 * 描述    : 对外暴露的数据库操作，使用接口IOperation隔离具体的实现
 */
public class DB {
    // 配置，对使用者不可见，包可见，方便直接引用
    static DBConfiguration conf;

    private static IOperation operation;

    public static void init(DBConfiguration conf) {
        DB.conf = conf;
        if (conf == null) {
            throw new IllegalArgumentException("DB.init 参数 conf 不能为空");
        }
        operation = new WUOperationImpl(conf.getContext());
    }


    /**
     * 插入
     *
     * @param bean
     * @param <T>
     * @return
     */
    public static <T> long insert(T bean) {
        return operation.<T>insert(bean);
    }

    public static <T> int insert(List<T> entitys) {
        return operation.insert(entitys);
    }

    /**
     * 查询
     *
     * @param sql           sql语句
     * @param selectionArgs 查询的条件
     * @param entityClz     需要查询返回的数据模型
     * @param <T>
     * @return
     */
    public static <T> List<T> query(Class<T> entityClz, String sql, String... selectionArgs) {
        return operation.<T>query(sql, selectionArgs, entityClz);
    }

    /**
     * 删除实体对应的表中的记录，注意：实体必须有主键才能使用这个方法
     *
     * @param entity
     * @param <T>
     * @return
     */
    public static <T> int delete(T entity) {
        return operation.delete(entity);
    }

    /**
     * 批量删除，只要有一个删除失败，那么此次操作就不会有任何删除
     * @param entitys
     * @param <T>
     * @return
     */
    public static <T> int delete(List<T> entitys) {
        return operation.delete(entitys);
    }




}
