package com.wyl.db;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.wyl.db.bean.User;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

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
        user.arrayList = new ArrayList<>();
        user.arrayList.add("wang");
        user.arrayList.add("yue");
        user.arrayList.add("lin");

        DBConfiguration conf = new DBConfiguration.Builder()
                .setContext(this)
                .setDbName("test.db")
                .setVersion(1)
                .setConverter(new TypeConverters())
                .setEntities(User.class)
                .build();
        DB.init(conf);


//        DB.insert(user);

        user.custom = "这是更新后的额鹅鹅鹅";
        user.id = 1;
        user.aDouble = 100000000.0;
        user.anInt = 88888;
//        DB.update(user);
        DB.update(user, "_id = ?", String.valueOf(user.id));


        List<User> users = DB.query(User.class,"select * from User", null);
        System.out.println(users);




        int total = 10000;
        long start = System.currentTimeMillis();
//        for (int i = 0; i < total; i++) {
//            User user = new User();
//            user.custom = "额鹅鹅鹅" + i;
//            user.aByte = 0;
//            user.aShort = 0;
//            user.anInt = 0;
//            user.aLong = 0;
//            user.aFloat = 0;
//            user.aDouble = 0;
//            user.arrayList = new ArrayList<>();
//            user.arrayList.add("wang");
//            user.arrayList.add("yue");
//            user.arrayList.add("lin");
//            DB.insert(user);
//        }
//        Log.e(TAG, "onCreate: 插入1万条使用的时间" + (System.currentTimeMillis() - start));

//        start = System.currentTimeMillis();
//
//        ArrayList<User> users = new ArrayList<>(total);
//        for (int i = 0; i < total; i++) {
//            User user = new User();
//            user.custom = "额鹅鹅鹅" + i;
//            user.aByte = 0;
//            user.aShort = 0;
//            user.anInt = 0;
//            user.aLong = 0;
//            user.aFloat = 0;
//            user.aDouble = 0;
//            user.arrayList = new ArrayList<>();
//            user.arrayList.add("wang");
//            user.arrayList.add("yue");
//            user.arrayList.add("lin");
//            users.add(user);
//        }
//        DB.insert(users);
//        Log.e(TAG, "onCreate: 批量插入1万条使用的时间" + (System.currentTimeMillis() - start));

//        DB.insert(user);

//        List<User> users = DB.query(User.class,"select * from User", null);

//        DB.delete(users.get(0));


//        start = System.currentTimeMillis();
//        insert1W(total);
//        Log.e(TAG, "onCreate: 原始的插入1万条使用的时间" + (System.currentTimeMillis() - start));

//        SQLiteDatabase sqLiteDatabase1 = DBPoolImpl.sqLiteOpenHelper.getWritableDatabase();
//        SQLiteDatabase sqLiteDatabase2 = DBPoolImpl.sqLiteOpenHelper.getWritableDatabase();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                long start = System.currentTimeMillis();
//                insert1W(20000, sqLiteDatabase1);
//                Log.e(TAG, "线程1 原始的插入1万条使用的时间" + (System.currentTimeMillis() - start));
//
//            }
//        }).start();


//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                long start = System.currentTimeMillis();
//                insert1W(total, sqLiteDatabase1);
//                Log.e(TAG, "线程2 原始的插入1万条使用的时间" + (System.currentTimeMillis() - start));
//
//            }
//        }).start();
    }

    private void insert1W(int total, SQLiteDatabase sqLiteDatabase) {
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
            originalInsert(user, sqLiteDatabase);
        }
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
}