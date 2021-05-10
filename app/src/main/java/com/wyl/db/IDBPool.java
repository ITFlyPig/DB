package com.wyl.db;

import android.database.sqlite.SQLiteDatabase;

import java.util.concurrent.TimeUnit;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/8
 * 描述    : 数据库连接池接口
 */
public interface IDBPool {

    /**
     * 获取数据库连接
     * @return
     */
     SQLiteDatabase borrowSQLiteDatabase();

    /**
     * 归还数据库连接
     * @param database
     * @return
     */
     void returnSQLiteDatabase(SQLiteDatabase database);

    /**
     * 具有超时机制的获取数据库连接
     * @param timeout
     * @param unit
     * @return
     */
     SQLiteDatabase borrowSQLiteDatabase(long timeout, TimeUnit unit);

    /**
     * 关闭连接池
     * @return
     */
    void close();

    /**
     * 是佛已关闭
     * @return
     */
    boolean isClosed();



}
