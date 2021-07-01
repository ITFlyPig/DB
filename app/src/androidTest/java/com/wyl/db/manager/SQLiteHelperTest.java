package com.wyl.db.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.test.core.app.ApplicationProvider;

import junit.framework.TestCase;

/**
 * @author : yuelinwang
 * @time : 7/1/21
 * @desc :
 */
public class SQLiteHelperTest extends TestCase {
    public static final String TAG = SQLiteHelperTest.class.getSimpleName();

    public void setUp() throws Exception {
        super.setUp();
        Context cxt = ApplicationProvider.getApplicationContext();
        SQLiteHelper.init(cxt, "test.db", 1, new ISQLLite() {
            @Override
            public void onCreate(SQLiteDatabase db) {
                System.out.println("SQLiteHelperTest--onCreate: ");
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
    }

    public void testGetInstance() {
        SQLiteDatabase database = SQLiteHelper.getInstance().getReadableDatabase();
        assertNotNull(database);
    }
}