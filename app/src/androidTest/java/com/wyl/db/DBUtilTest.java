package com.wyl.db;

import android.util.Log;

import com.wyl.db.bean.User;

import junit.framework.TestCase;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/9
 * 描述    :
 */
public class DBUtilTest extends TestCase {

    public void testCheckDBSupport() {

    }

    public void testField2Column() {
        assertEquals("stu_name", DBUtil.field2Column("stuName"));
    }

    public void testColumn2Field() {
        assertEquals("stuName", DBUtil.column2Field("stu_name"));
    }
}