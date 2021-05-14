package com.wyl.db.manager;

import android.database.sqlite.SQLiteDatabase;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/10
 * 描述    : SQLLite 相关行为的回调
 */
public interface ISQLLite {
    /**
     * 创建
     *
     * @param db
     */
    void onCreate(SQLiteDatabase db);

    /**
     * 升级
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);

    /**
     * 降级
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion);

    /**
     * 打开
     * @param db
     */
    void onOpen(SQLiteDatabase db);
}
