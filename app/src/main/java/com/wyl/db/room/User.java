package com.wyl.db.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.migration.Migration;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/10
 * 描述    :
 */
@Entity
public class User {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @Ignore
    @ColumnInfo(name = "first_name")
    public String firstName;

    @ColumnInfo(name = "last_name")
    public String lastName;

    public int anInt;
    public byte aByte;
    public short aShort;
    public long aLong;
    public Long bLong;
    public float aFloat;
    public double aDouble;
    public boolean aBoolean;
    public byte[] bytes;


}

