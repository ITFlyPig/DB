package com.wyl.db.manager.migration;

import android.database.sqlite.SQLiteDatabase;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/24
 * 描述    : 隔离用户和数据库，避免用户直接操作数据库，不小心将数据库给关闭了
 */
public class SQLiteDatabaseWrapper {
    private SQLiteDatabase sqLiteDatabase;

    public SQLiteDatabaseWrapper(SQLiteDatabase sqLiteDatabase) {
        this.sqLiteDatabase = sqLiteDatabase;
    }

    public void execSQL(String sql) {
        if (sqLiteDatabase == null) {
            return;
        }
        sqLiteDatabase.execSQL(sql);
    }
}
