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
     * 据sql查询
     *
     * @param sql
     * @param <T>
     * @return
     */
    <T> List<T> query(String sql);

    /**
     * 据主键查询
     *
     * @param id
     * @param <T>
     * @return
     */
    <T> List<T> query(long id);
}
