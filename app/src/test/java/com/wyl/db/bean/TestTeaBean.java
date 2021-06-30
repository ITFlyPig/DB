package com.wyl.db.bean;

import com.wyl.db.annotations.ColumnInfo;
import com.wyl.db.annotations.Ignore;
import com.wyl.db.annotations.PrimaryKey;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author : yuelinwang
 * @time : 6/30/21
 * @desc :
 */
public class TestTeaBean {
    public String name;
    @PrimaryKey
    public int age;
    @Ignore
    public HashMap<String, Integer> mapSI;
    public HashMap<String, String> mapSS;
    @ColumnInfo(name = "")
    public ArrayList<String> names;
    @ColumnInfo(name = "_ages")
    public ArrayList<Integer> ages;
    public byte aByte;
    public boolean aBoolean;
    public double aDouble;
}
