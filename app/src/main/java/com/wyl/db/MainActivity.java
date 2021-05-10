package com.wyl.db;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.os.Bundle;

import com.wyl.db.bean.User;
import com.wyl.db.room.AppDatabase;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        User user = new User();
//        user.custom = "test";
//        DB.init(this);
//        DB.<User>insert(user);

        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "database-name").build();

        new Thread(new Runnable() {
            @Override
            public void run() {
                db.userDao().getAll();
            }
        }
        ).start();

    }
}