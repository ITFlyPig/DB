package com.wyl.db.bean;

import com.wyl.db.annotations.ColumnInfo;

/**
 * @author : yuelinwang
 * @time : 6/8/21
 * @desc :
 */
public class TestCountBean {
    @ColumnInfo(name = "_id")
    public int id;
    @ColumnInfo(name = "_name")
    public String name;
}
