package com.wyl.db;

import java.util.List;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/8
 * 描述    : db操作接口
 */
interface IOperation {
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
     * @param entitys
     * @param <T>
     * @return 0：表示完全插入成功； -1：有失败的情况存在，不做任何插入操作
     */
    <T> int insert(List<T> entitys);

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
    <T> List<T> query(long id);

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
}
