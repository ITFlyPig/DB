package com.wyl.db.manager;

import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.wyl.db.DB;
import com.wyl.db.util.SQLUtil;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/14
 * 描述    : 封装SQLLite的 升级、降级等操作
 */
public class SQLLiteImpl implements ISQLLite {
    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    /**
     * 反射创建表
     * @param db
     */
    private void createTables(SQLiteDatabase db) {
        Class<?>[] entitys = DB.getConf().getEntitys();
        if (entitys == null) return;
        for (Class<?> entity : entitys) {
            String sql = SQLUtil.createTableSQL(entity, DB.getConf().getConverter());
            if (!TextUtils.isEmpty(sql)) {
                db.execSQL(sql);
                Log.d(DB.getConf().getLogTag(), "创建表SQL：" + sql);
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onOpen(SQLiteDatabase db) {

    }
}
