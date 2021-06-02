package com.wyl.db.manager;

import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.wyl.db.DB;
import com.wyl.db.manager.migration.Migration;
import com.wyl.db.manager.migration.SQLiteDatabaseWrapper;
import com.wyl.db.util.LogUtil;
import com.wyl.db.util.SQLUtil;

import java.util.List;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/14
 * 描述    : 封装SQLLite的 升级、降级等操作
 * @author yuelinwang
 */
public class SQLLiteImpl implements ISQLLite {
    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    /**
     * 反射创建表
     *
     * @param db
     */
    private void createTables(SQLiteDatabase db) {
        Class<?>[] entitys = DB.getConf().getEntitys();
        if (entitys == null) {
            return;
        }
        for (Class<?> entity : entitys) {
            String sql = SQLUtil.createTableSQL(entity, DB.getConf().getConverter());
            if (!TextUtils.isEmpty(sql)) {
                db.execSQL(sql);
                LogUtil.d(DB.tag(), "创建表SQL：" + sql);
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        doMigration(oldVersion, newVersion, db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        doMigration(oldVersion, newVersion, db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
    }

    /**
     * 迁移
     * @param oldVersion
     * @param newVersion
     * @param db
     */
    private void doMigration(int oldVersion, int newVersion, SQLiteDatabase db) {
        if (DB.getConf() == null || DB.getConf().getMigrationContainer() == null) {
            return;
        }
        boolean migrated = false;
        List<Migration> migrations = DB.getConf().getMigrationContainer().findMigrationPath(oldVersion, newVersion);
        if (migrations != null) {
            SQLiteDatabaseWrapper databaseWrapper = new SQLiteDatabaseWrapper(db);
            for (Migration migration : migrations) {
                migration.migrate(databaseWrapper);
            }
            migrated = true;
        }

        if (!migrated) {
            throw new IllegalStateException("A migration from " + oldVersion + " to "
                    + newVersion + " was required but not found. Please provide the "
                    + "necessary Migration path via "
                    + "new DBConfiguration.Builder().addMigration(Migration ...)");
        }


    }

}
