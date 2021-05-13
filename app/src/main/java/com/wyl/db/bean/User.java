package com.wyl.db.bean;

import com.wyl.db.ColumnInfo;
import com.wyl.db.PrimaryKey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/10
 * 描述    :
 */
public class User {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    public int id;
    @ColumnInfo(name = "_custom")
    public String custom = "";

    public int anInt;

    public short aShort;
    public byte aByte;
    public long aLong;
    public double aDouble;
    public float aFloat;
    public byte[] aBytes;
    public Long aaLong = 0L;

    public ArrayList<String> arrayList = new ArrayList<>();

    @ColumnInfo(name = "_map")
    public HashMap<String, String> hashMap = new HashMap<>();


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", custom='" + custom + '\'' +
                ", anInt=" + anInt +
                ", aShort=" + aShort +
                ", aByte=" + aByte +
                ", aLong=" + aLong +
                ", aDouble=" + aDouble +
                ", aFloat=" + aFloat +
                ", aBytes=" + Arrays.toString(aBytes) +
                ", aaLong=" + aaLong +
                ", arrayList=" + arrayList +
                ", hashMap=" + hashMap +
                '}';
    }
}
