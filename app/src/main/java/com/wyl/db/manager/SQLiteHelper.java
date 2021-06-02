package com.wyl.db.manager;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/10
 * 描述    : SQLite帮助类
 * @author yuelinwang
 */
public class SQLiteHelper extends SQLiteOpenHelper {

    /**
     * 将数据库的升级、降级等操作抽离
     */
    private static ISQLLite isqlLite;

    private static volatile SQLiteHelper sqLiteHelper;

    private static Context context;
    private static String dbName;
    private static int version;


    private SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    private SQLiteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version, @Nullable DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    private SQLiteHelper(@Nullable Context context, @Nullable String name, int version, @NonNull SQLiteDatabase.OpenParams openParams) {
        super(context, name, version, openParams);
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

    /**
     * 初始化
     * @param context
     * @param dbName
     * @param version
     */
    public static synchronized void init(Context context, String dbName, int version, ISQLLite isqlLite) {
        if (context == null || version <= 0) {
            throw new IllegalArgumentException("SQLiteHelper初始化失败：参数不合法");
        }
        SQLiteHelper.context = context;
        SQLiteHelper.dbName = dbName;
        SQLiteHelper.version = version;
        SQLiteHelper.isqlLite = isqlLite;
    }

    /**
     * 获取数据库实例
     * @return
     */
    public static SQLiteHelper getInstance() {
        if (sqLiteHelper == null) {
            synchronized (SQLiteOpenHelper.class) {
                if (sqLiteHelper == null) {
                    sqLiteHelper = new SQLiteHelper(context, dbName, null, version);
                }
            }
        }
        return sqLiteHelper;
    }

}
