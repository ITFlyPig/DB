package com.wyl.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/10
 * 描述    : SQLite帮助类
 */
public class SQLiteHelper extends SQLiteOpenHelper {
    private ISQLLite isqlLite;

    public SQLiteHelper(@Nullable Context context, @Nullable String name, int version, @NonNull ISQLLite isqlLite) {
        super(context, name, null, version);
        this.isqlLite = isqlLite;
        if (isqlLite == null) {
            throw new IllegalArgumentException("SQLiteHelper参数ISQLLite不能为空");
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        isqlLite.onCreate(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        isqlLite.onUpgrade(db, oldVersion, newVersion);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        isqlLite.onDowngrade(db, oldVersion, newVersion);

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        isqlLite.onOpen(db);
    }

}
