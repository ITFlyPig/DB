package com.wyl.db;

import com.wyl.db.manager.IOperation;
import com.wyl.db.manager.WUOperationImpl;

import java.util.List;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/8
 * 描述    : 对外暴露的数据库操作，使用接口IOperation隔离具体的实现
 */
public class DB {
    // 配置，对使用者不可见，包可见，方便直接引用
    private static DBConfiguration conf;

    private static IOperation operation;

    public static void init(DBConfiguration conf) {
        DB.conf = conf;
        if (conf == null) {
            throw new IllegalArgumentException("DB.init 参数 conf 不能为空");
        }
        operation = new WUOperationImpl();
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

    /**
     * 据主键更新实体在数据库中对应的数据，注意：传入的实体必须有主键
     * @param entity
     * @param <T>
     * @return
     */
    public static  <T> long update(T entity) {
        return operation.update(entity);
    }

    /**
     * 据自定义条件更新数据库，注意：此方法不要求必须有主键
     * @param entity
     * @param whereClause
     * @param whereArgs
     * @param <T>
     * @return
     */
    public static  <T> long update(T entity, String whereClause, String ...whereArgs) {
        return operation.update(entity, whereClause, whereArgs);

    }

    public static DBConfiguration getConf() {
        return conf;
    }
}
