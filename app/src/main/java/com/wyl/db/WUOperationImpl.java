package com.wyl.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.internal.$Gson$Preconditions;
import com.wyl.db.bean.User;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/8
 * 描述    : db操作的实现
 */
class WUOperationImpl implements IOperation {
    private DBManager dbManager;

    public WUOperationImpl(Context context) {
        SQLiteHelper sqLiteHelper = new SQLiteHelper(context, "test.db", 1, new ISQLLite() {
            @Override
            public void onCreate(SQLiteDatabase db) {
               Class<?>[] entitys = DB.conf.getEntitys();
               if (entitys == null) return;
                for (Class<?> entity : entitys) {
                    String sql = SQLUtil.createTableSQL(entity, DB.conf.getConverter());
                    if (!TextUtils.isEmpty(sql)) {
                        db.execSQL(sql);
                        Log.d(DB.conf.getLogTag(), "创建表SQL：" + sql);
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
        });
        dbManager = new DBManager(new DBPoolImpl(sqLiteHelper));
    }

    @Override
    public <T> long insert(T bean) {
        if (bean == null) return -1;
        return dbManager.insert(bean);
    }

    @Override
    public <T> int insert(List<T> entitys) {
        return dbManager.insert(entitys);
    }

    @Override
    public <T> List<T> query(String sql, String[] selectionArgs, Class<T> entityClz) {
        if (TextUtils.isEmpty(sql) || entityClz == null) {
            return new ArrayList<>(0);
        }
        return dbManager.query(sql, selectionArgs, entityClz);
    }

    @Override
    public <T> List<T> query(long id) {
        return null;
    }

    @Override
    public <T> int delete(T entity) {
        return dbManager.delete(entity);
    }

    @Override
    public <T> int delete(List<T> entitys) {
        return dbManager.delete(entitys);
    }

    @Override
    public <T> long update(T entity) {
        return dbManager.update(entity);
    }

    @Override
    public <T> long update(T entity, String whereClause, String[] whereArgs) {
        return dbManager.update(entity, whereClause, whereArgs);
    }


}
