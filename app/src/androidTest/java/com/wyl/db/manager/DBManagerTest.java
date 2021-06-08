package com.wyl.db.manager;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.wyl.db.util.LogUtil;
import com.wyl.log.Log;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * @author : yuelinwang
 * @time : 6/8/21
 * @desc :
 */

@RunWith(AndroidJUnit4.class)
public class DBManagerTest  {
    public static final String TAG = DBManagerTest.class.getSimpleName();

    @Test
    public void testGetCountStr() {
        DBManager dbManager = new DBManager();
        String sql = dbManager.getCountStr("name=? and age=?", "id", "stu");
        LogUtil.e(TAG, sql);
        assertEquals("select count(id) from stu where name=? and age=?", sql);
    }
}