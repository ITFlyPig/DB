package com.wyl.db.bean;

import com.wyl.db.annotations.PrimaryKey;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/24
 * 描述    :
 */
public class Stu extends BaseBean{
    @PrimaryKey
    public int id;
    public String name;
    public int age;
}
