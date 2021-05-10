package com.wyl.db;

import android.database.sqlite.SQLiteDatabase;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/10
 * 描述    : 获取接口
 */
public interface AcquireCallBack {
    SQLiteDatabase acquire();
}
