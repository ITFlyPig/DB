package com.wyl.db;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.wyl.db.bean.User;
import com.wyl.db.converter.TypeConverters;
import com.wyl.db.manager.SQLiteHelper;

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
        user.custom = "这是更新后的额鹅鹅鹅";
        user.id = 1;
        user.aDouble = 100000000.0;
        user.anInt = 88888;
        user.arrayList = new ArrayList<>();
        user.arrayList.add("wang");
        user.arrayList.add("yue");
        user.arrayList.add("lin");


        // 数据模型
//        DB.insert(user);


        DBConfiguration conf = new DBConfiguration.Builder()
                .setContext(this)
                .setDbName("test.db")
                .setVersion(1)
                .setConverter(new TypeConverters())
                .setEntities(User.class)
                .build();

        DB.init(conf);


//        List<User> users = DB.query(User.class,"select * from User", null);
//        System.out.println(users);
//
//        long start  = System.currentTimeMillis();
//
//        insertW(20000, SQLiteHelper.getInstance().getWritableDatabase());
//
//        Log.e(TAG, "框架方式插入2w耗时：" + (System.currentTimeMillis() - start));

        Log.e(TAG, "queryById: " + DB.queryById(100, User.class));
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
            user.aBytes = new byte[]{1,2,1};
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
}