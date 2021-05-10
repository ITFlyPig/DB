package com.wyl.db.bean;

import com.wyl.db.ColumnInfo;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/10
 * 描述    :
 */
public class User {
    @ColumnInfo(isPrimaryKey = true)
    public int id;
    public String custom;
}
