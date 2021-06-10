package com.wyl.db.manager;

import android.content.ContentValues;

import java.util.List;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/8
 * 描述    : db操作接口
 *
 * @author yuelinwang
 */
public interface IOperation {
    /**
     * 将bean插入到数据库
     *
     * @param bean
     * @param <T>
     * @return
     */
    <T> long insert(T bean);

    /**
     * 将bean批量插入到数据库
     *
     * @param entitys
     * @param <T>
     * @return 0：表示完全插入成功； -1：有失败的情况存在，不做任何插入操作
     */
    <T> void insert(List<T> entitys);

    /**
     * 据sql查询
     *
     * @param sql
     * @param <T>
     * @return
     */
    <T> List<T> query(String sql, String[] selectionArgs, Class<T> entityClz);

    /**
     * 据主键查询
     *
     * @param id
     * @param <T>
     * @return
     */
    <T> T query(long id, Class<T> entityClz);

    /**
     * 删除
     *
     * @param entity
     * @param <T>
     * @return
     */
    <T> int delete(T entity);

    /**
     * 批量删除
     *
     * @param entitys
     * @param <T>
     * @return
     */
    <T> int delete(List<T> entitys);

    /**
     * 据主键更新实体在数据库中对应的数据
     *
     * @param entity
     * @param <T>
     * @return
     */
    <T> long update(T entity);

    /**
     * 据自定义的条件更新数据库中的数据
     *
     * @param entity
     * @param whereClause
     * @param whereArgs
     * @param <T>
     * @return
     */
    <T> long update(T entity, String whereClause, String[] whereArgs);

    /**
     * 统计数量
     *
     * @param entityClz
     * @param whereClause
     * @param whereArgs
     * @param <T>
     * @return -1：失败；>= 0：表示获取到的数量
     */
    <T> long count(Class<T> entityClz, String whereClause, String[] whereArgs);

    /**
     * 删除
     * @param entityClz
     * @param whereClause
     * @param whereArgs
     * @param <T>
     * @return -1：失败：>=0：表示影响的行数
     */
    <T> long delete(Class<T> entityClz, String whereClause, String[] whereArgs);

    /**
     *  更新
     * @param entityClz
     * @param values
     * @param whereClause
     * @param whereArgs
     * @param <T>
     * @return 受影响的行数
     */
    <T> long update(Class<T> entityClz, ContentValues values, String whereClause, String[] whereArgs);
}
