package com.wyl.db.util;

import android.text.TextUtils;

import com.wyl.db.bean.TestStuBean;
import com.wyl.db.converter.TypeConverters;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author : yuelinwang
 * @time : 6/30/21
 * @desc :
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(TextUtils.class)
public class SQLUtilTest extends TestCase {

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(TextUtils.class);
    }

    public void testCreateTableSQL() {
        PowerMockito.when(TextUtils.isEmpty(null)).thenReturn(true);
        String sql = SQLUtil.createTableSQL(TestStuBean.class, new TypeConverters());
        assertEquals("create table if not exists TestStuBean (`name` TEXT,`age` INTEGER NOT NULL,`map` TEXT)", sql);

    }

    /**
     * 测试静态私有方法
     */
    public void testColumnCreateSQL() {
        PowerMockito.when(TextUtils.isEmpty(null)).thenReturn(true);
        TypeConverters typeConverters = new TypeConverters();
        Method method = ReflectionTestUtil.getMethod(SQLUtil.class, "columnCreateSQL");

        try {
            String nameCreateSql = (String) method.invoke(null, ReflectionTestUtil.getField(TestStuBean.class, "name"), typeConverters);
            assertEquals("`name` TEXT", nameCreateSql);
            String ageCreateSql = (String) method.invoke(null, ReflectionTestUtil.getField(TestStuBean.class, "age"), typeConverters);
            assertEquals("`age` INTEGER NOT NULL", ageCreateSql);
            String mapCreateSql = (String) method.invoke(null, ReflectionTestUtil.getField(TestStuBean.class, "map"), typeConverters);
            assertEquals("`map` TEXT", mapCreateSql);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }


    }

    public void testIsSQLLiteSupport() {
        assertTrue(SQLUtil.isSQLLiteSupport(Byte[].class));
        assertTrue(SQLUtil.isSQLLiteSupport(byte[].class));
        assertTrue(SQLUtil.isSQLLiteSupport(Byte.class));
        assertTrue(SQLUtil.isSQLLiteSupport(byte.class));
    }

    public void testGetSQLLiteType() {
        assertEquals("INTEGER NOT NULL", SQLUtil.getSQLLiteType(byte.class));
        assertEquals("INTEGER NOT NULL", SQLUtil.getSQLLiteType(short.class));
        assertEquals("INTEGER NOT NULL", SQLUtil.getSQLLiteType(int.class));
        assertEquals("INTEGER NOT NULL", SQLUtil.getSQLLiteType(long.class));
        assertEquals("INTEGER NOT NULL", SQLUtil.getSQLLiteType(boolean.class));
        assertEquals("INTEGER", SQLUtil.getSQLLiteType(Byte.class));
        assertEquals("INTEGER", SQLUtil.getSQLLiteType(Short.class));
        assertEquals("INTEGER", SQLUtil.getSQLLiteType(Integer.class));
        assertEquals("INTEGER", SQLUtil.getSQLLiteType(Long.class));
        assertEquals("INTEGER", SQLUtil.getSQLLiteType(Boolean.class));
        assertEquals("REAL NOT NULL", SQLUtil.getSQLLiteType(float.class));
        assertEquals("REAL NOT NULL", SQLUtil.getSQLLiteType(double.class));
        assertEquals("REAL", SQLUtil.getSQLLiteType(Double.class));
        assertEquals("REAL", SQLUtil.getSQLLiteType(Float.class));
        assertEquals("TEXT", SQLUtil.getSQLLiteType(String.class));
        assertEquals("BLOB", SQLUtil.getSQLLiteType(byte[].class));
        assertEquals("BLOB", SQLUtil.getSQLLiteType(Byte[].class));
    }
}