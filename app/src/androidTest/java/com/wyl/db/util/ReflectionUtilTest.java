package com.wyl.db.util;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.wyl.db.bean.Stu;
import com.wyl.db.bean.Tea;
import com.wyl.db.bean.TestCountBean;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author : yuelinwang
 * @time : 6/8/21
 * @desc :
 */
@RunWith(AndroidJUnit4.class)
public class ReflectionUtilTest {

    @Test
    public void testGetCountColumnName() {
        assertNull(ReflectionUtil.getCountColumnName(null));
        assertEquals("id", ReflectionUtil.getCountColumnName(Stu.class));
        assertEquals("id", ReflectionUtil.getCountColumnName(Tea.class));
        assertEquals("_id", ReflectionUtil.getCountColumnName(TestCountBean.class));
    }
}