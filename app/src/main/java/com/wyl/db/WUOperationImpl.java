package com.wyl.db;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/8
 * 描述    : db操作的实现
 */
class WUOperationImpl implements IOperation {
    private DBManager dbManager;

    public WUOperationImpl() {
        SQLiteHelper sqLiteHelper = new SQLiteHelper(DB.context, "test.db", 1, new ISQLLite() {
            @Override
            public void onCreate(SQLiteDatabase db) {
                String createTbStr = "create table if not exists " +
                        "user" + "(id integer primary key autoincrement, custom TEXT)";
                try {
                    db.execSQL(createTbStr);
                } catch (SQLException e) {
                    e.printStackTrace();
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
    public <T> List<T> query(String sql) {
        return null;
    }

    @Override
    public <T> List<T> query(long id) {
        return null;
    }


}
