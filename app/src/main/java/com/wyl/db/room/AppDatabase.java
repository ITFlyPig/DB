package com.wyl.db.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/10
 * 描述    :
 */
@Database(entities = {User.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
}

