package com.wyl;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.wyl.crash.Crash;
import com.wyl.crash.caught.ICollectStackTraceListener;
import com.wyl.db.DB;
import com.wyl.db.DBConfiguration;
import com.wyl.db.R;
import com.wyl.db.bean.BaseBean;
import com.wyl.db.bean.Stu;
import com.wyl.db.bean.User;
import com.wyl.db.converter.TypeConverters;
import com.wyl.db.manager.migration.Migration;
import com.wyl.db.manager.migration.SQLiteDatabaseWrapper;
import com.wyl.log.LogToDiskUtil;
import com.wyl.log.WULog;
import com.wyl.log.persistence.LogBean;
import com.wyl.log.persistence.LogStatus;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SQLiteDatabaseWrapper databaseWrapper) {
            databaseWrapper.execSQL("ALTER TABLE User"
                    + " ADD COLUMN appId TEXT");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        User user = new User();
        user.custom = "额鹅鹅鹅";
        user.aByte = 0;
        user.aShort = 0;
        user.anInt = 0;
        user.aLong = 0;
        user.aFloat = 0;
        user.aDouble = 0;
        user.custom = "这是更新后的额鹅鹅鹅";
        user.id = 1;
        user.aDouble = 100000000.0;
        user.anInt = 88888;
        user.arrayList = new ArrayList<>();
        user.arrayList.add("wang");
        user.arrayList.add("yue");
        user.arrayList.add("lin");

        Stu stu = new Stu();
        stu.name = "wangyuelin";
        stu.age = 888;
        stu.id = 3;


        DBConfiguration conf = new DBConfiguration.Builder()
                .setContext(this)
                .setDbName("test.db")
                .setVersion(1)
                .setConverter(new TypeConverters())
                .setEntities(User.class, Stu.class, LogBean.class)
//                .addMigrations(MIGRATION_1_2)
                .build();

        DB.init(conf);


        ArrayList<BaseBean> baseBeans = new ArrayList<>();
        baseBeans.add(user);
        baseBeans.add(stu);

        // 插入
//        DB.insert(baseBeans);

//        Log.d(TAG, "MainActivity--onCreate: 查询到的数量：" + DB.count(user.getClass(), null, null));
//
//        List<Stu> stus = DB.query(Stu.class, "select * from Stu", null);
//        Log.e(TAG, "onCreate: " + stus);


        // 查询
//        List<User> users = DB.query(User.class,"select * from User", null);
//        DB.delete(users);


//        System.out.println(users);
//
//        long start  = System.currentTimeMillis();
//
//        insertW(20000, SQLiteHelper.getInstance().getWritableDatabase());
//
//        Log.e(TAG, "框架方式插入2w耗时：" + (System.currentTimeMillis() - start));

//        Log.e(TAG, "queryById: " + DB.queryById(1, User.class));


//        Thread.currentThread().setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
//            @Override
//            public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
//                Log.e(TAG, "主线程的uncaughtException: " + e.getLocalizedMessage());
//            }
//        });

//        testCrash();

//        testDiskLog();

//        WULog.onEvent("test");

        findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                for (int i = 0; i < 800; i++) {
//                    HashMap<String, String> param = new HashMap<>();
//                    param.put("name", "wang");
//                    param.put("age", "10");
//                    WULog.onEvent("key_test", param);
//                }
                long start = System.currentTimeMillis();
                long num = testCount();
                Log.e(TAG, "onClick: 一次数据库查询所消耗的时间：" + (System.currentTimeMillis() - start) + " 查询到的数量：" + num);
            }
        });

    }

    private void testDiskLog() {
        for (int i = 0; i < 10; i++) {
            LogToDiskUtil.error(TAG, "test消息" + i, null);
//            LogToDiskUtil.info(TAG, "test消息" + i, new IllegalArgumentException("info参数不合法"));
//            LogToDiskUtil.debug(TAG, "test消息" + i, new IllegalArgumentException("debug参数不合法"));
        }
    }

    private long testCount() {
        return DB.count(LogBean.class, "status = ?", String.valueOf(LogStatus.INIT));

    }


    private void insertW(int total, SQLiteDatabase sqLiteDatabase) {
        ArrayList<User> users = new ArrayList<>(total);
        //原始的插入方式
        for (int i = 0; i < total; i++) {
            User user = new User();
            user.custom = "额鹅鹅鹅" + i;
            user.aByte = 0;
            user.aShort = 0;
            user.anInt = 0;
            user.aLong = 0;
            user.aFloat = 0;
            user.aDouble = 0;
            user.arrayList = new ArrayList<>();
            user.arrayList.add("wang");
            user.arrayList.add("yue");
            user.arrayList.add("lin");
            user.aBytes = new byte[]{1, 2, 1};
            users.add(user);
//            originalInsert(user, sqLiteDatabase);
//            DB.insert(user);
        }
        DB.insert(users);
    }


    private void originalInsert(User user, SQLiteDatabase sqLiteDatabase) {
        ContentValues values = new ContentValues();
        values.put("_custom", user.custom);
        values.put("anInt", user.anInt);
        values.put("aShort", user.aShort);
        values.put("aByte", user.aByte);
        values.put("aLong", user.aLong);
        values.put("aDouble", user.aDouble);
        values.put("aFloat", user.aFloat);
        values.put("aaLong", user.aaLong);
        values.put("arrayList", new Gson().toJson(user.arrayList));
        values.put("_map", new Gson().toJson(user.hashMap));
        sqLiteDatabase.insert("User", null, values);
    }

    private void test() {
        ArrayList<User> users = new ArrayList<>();
        Cursor cursor = null;
        while (cursor.moveToNext()) {
            User user = new User();
            user.id = cursor.getInt(0);
            user.custom = cursor.getString(1);
            user.anInt = cursor.getInt(2);
            user.aShort = cursor.getShort(3);
            user.aByte = (byte) cursor.getInt(4);
            user.aLong = cursor.getLong(5);
            user.aFloat = cursor.getFloat(6);
            user.aBytes = cursor.getBlob(7);
            users.add(user);
        }
    }

    private void testCrash() {

        Crash.setup(getApplicationContext(), "com.wyl", new ICollectStackTraceListener() {
            @Override
            public void onDone(String summary, String detail) {
                Log.e(TAG, "onDone: 捕获到的崩溃日志：\n 摘要：" + summary + " \n 详情：\n" +  detail);
//                LogToDiskUtil.onCrash("摘要：" + summary + " \n 详情：\n" +  detail);
            }

        });

//
//        WUThreadFactoryUtil.newThread(new Runnable() {
//            @Override
//            public void run() {
//                crash();
//            }
//        }).start();
        crash();
//        Executors.defaultThreadFactory();
    }

    private void crash() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        User user = null;
        System.out.println(user.aaLong);
    }
}