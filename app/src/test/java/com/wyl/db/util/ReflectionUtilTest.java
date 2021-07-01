package com.wyl.db.util;

import android.app.Application;
import android.text.TextUtils;

import com.wyl.db.DB;
import com.wyl.db.DBConfiguration;
import com.wyl.db.bean.TestEmptyBean;
import com.wyl.db.bean.TestStuBean;
import com.wyl.db.bean.TestTeaBean;
import com.wyl.db.converter.TypeConverters;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;

import static org.junit.Assert.assertNotEquals;

/**
 * @author : yuelinwang
 * @time : 6/30/21
 * @desc :
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(TextUtils.class)
public class ReflectionUtilTest extends TestCase {
    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(TextUtils.class);
    }

    public void testParseBeans() {
    }

    public void testFillField() {
        TestTeaBean bean = new TestTeaBean();
        ReflectionUtil.fillField(ReflectionTestUtil.getField(TestTeaBean.class, "aByte"), bean, 100);
        assertEquals(100, bean.aByte);
        ReflectionUtil.fillField(ReflectionTestUtil.getField(TestTeaBean.class, "aBoolean"), bean, 1);
        assertTrue(bean.aBoolean);
        ReflectionUtil.fillField(ReflectionTestUtil.getField(TestTeaBean.class, "aBoolean"), bean, 0);
        assertFalse(bean.aBoolean);
        ReflectionUtil.fillField(ReflectionTestUtil.getField(TestTeaBean.class, "aBoolean"), bean, 11);
        assertFalse(bean.aBoolean);
        ReflectionUtil.fillField(ReflectionTestUtil.getField(TestTeaBean.class, "aDouble"), bean, 100F);
        assertEquals(100.0, bean.aDouble);

        // 反射注入值
        DBConfiguration configuration = new DBConfiguration.Builder().setConverter(new TypeConverters()).setVersion(1).setContext(new Application()).build();
        Field field = ReflectionTestUtil.getField(DB.class, "conf");
        field.setAccessible(true);
        try {
            field.set(null, configuration);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        // 测试 String -> Map
        ReflectionUtil.fillField(ReflectionTestUtil.getField(TestTeaBean.class, "ages"), bean, "");
        ArrayList<Integer> list = new ArrayList<>();
        list.add(1);
        assertEquals(list, bean.ages);

    }


    public void testGetFields() {
    }

    public void testGetTableName() {

    }

    public void testGetColumnName() {
        PowerMockito.when(TextUtils.isEmpty(null)).thenReturn(true);
        assertEquals("name", ReflectionUtil.getColumnName(ReflectionUtil.getByName(TestTeaBean.class, "name")));
        assertEquals("_ages", ReflectionUtil.getColumnName(ReflectionUtil.getByName(TestTeaBean.class, "ages")));
        PowerMockito.when(TextUtils.isEmpty("")).thenReturn(true);
        assertEquals("names", ReflectionUtil.getColumnName(ReflectionUtil.getByName(TestTeaBean.class, "names")));
    }

    public void testIsByteArr() {
        assertTrue(ReflectionUtil.isByteArr(byte[].class));
        assertTrue(ReflectionUtil.isByteArr(Byte[].class));
        assertFalse(ReflectionUtil.isByteArr(String[].class));
    }

    public void testTypeEqual() {
        assertTrue(ReflectionUtil.typeEqual(String.class, String.class));
        assertFalse(ReflectionUtil.typeEqual(String.class, Integer.class));
    }

    public void testMethodGeneric() {
        Method method = ReflectionTestUtil.getMethod(TypeConverters.class, "convertS");
        Type genericReturnType = method.getGenericReturnType();

        assertSame(genericReturnType, String.class);
        Type paramType = method.getGenericParameterTypes()[0];
        assertNotSame(paramType, ArrayList.class);

        Method convertString2List = ReflectionTestUtil.getMethod(TypeConverters.class, "convertString2List");
        Type type1 = convertString2List.getGenericReturnType();
        assertNotSame(type1, ArrayList.class);

    }


    public void testTypesEqual() {
    }

    public void testFindByParamType() {
        Field fieldNames = ReflectionTestUtil.getField(TestTeaBean.class, "names");
        Field fieldAges = ReflectionTestUtil.getField(TestTeaBean.class, "ages");
        Field fieldMapSI = ReflectionTestUtil.getField(TestTeaBean.class, "mapSI");
        Field fieldMapSS = ReflectionTestUtil.getField(TestTeaBean.class, "mapSS");

        Method convertS = ReflectionTestUtil.getMethod(TypeConverters.class, "convertS");
        Method convertI = ReflectionTestUtil.getMethod(TypeConverters.class, "convertI");
        Method mapStringString = ReflectionTestUtil.getMethod(TypeConverters.class, "mapStringString");
        Method mapStringInteger = ReflectionTestUtil.getMethod(TypeConverters.class, "mapStringInteger");

        // 验证不同的泛型参数，是否能准确找到对应的转换函数
        assertEquals(convertS, ReflectionUtil.findByParamType(TypeConverters.class, fieldNames.getGenericType()));
        assertEquals(convertI, ReflectionUtil.findByParamType(TypeConverters.class, fieldAges.getGenericType()));
        assertEquals(mapStringInteger, ReflectionUtil.findByParamType(TypeConverters.class, fieldMapSI.getGenericType()));
        assertEquals(mapStringString, ReflectionUtil.findByParamType(TypeConverters.class, fieldMapSS.getGenericType()));

    }

    public void testFindByReturnAndParamType() {
        Method convertS = ReflectionTestUtil.getMethod(TypeConverters.class, "convertS");
        Method convertRetStringList = ReflectionTestUtil.getMethod(TypeConverters.class, "convertRetStringList");
        assertNotSame(convertS, ReflectionUtil.findByReturnAndParamType(TypeConverters.class, String.class, ArrayList.class));
        Field fieldNames = ReflectionTestUtil.getField(TestTeaBean.class, "names");
        Field fieldAges = ReflectionTestUtil.getField(TestTeaBean.class, "ages");
        // 测试参数的泛型匹配
        assertEquals(convertS, ReflectionUtil.findByReturnAndParamType(TypeConverters.class, String.class, fieldNames.getGenericType()));
        assertNotEquals(convertS, ReflectionUtil.findByReturnAndParamType(TypeConverters.class, String.class, fieldAges.getGenericType()));

        // 测试返回值的泛型匹配
        assertEquals(convertRetStringList, ReflectionUtil.findByReturnAndParamType(TypeConverters.class, fieldNames.getGenericType(), String.class));
        assertNotEquals(convertRetStringList, ReflectionUtil.findByReturnAndParamType(TypeConverters.class, fieldAges.getGenericType(), String.class));
        assertNull(ReflectionUtil.findByReturnAndParamType(TypeConverters.class, fieldAges.getGenericType(), fieldAges.getGenericType()));
    }

    public void testGetByName() {
        assertEquals(ReflectionTestUtil.getField(TestTeaBean.class, "age"), ReflectionUtil.getByName(TestTeaBean.class, "age"));
        assertNull(ReflectionUtil.getByName(TestTeaBean.class, "age111"));
    }

    public void testGetPrimaryKeyField() {
        assertNull(ReflectionUtil.getPrimaryKeyField(new TestStuBean()));
        assertEquals(ReflectionTestUtil.getField(TestTeaBean.class, "age"),ReflectionUtil.getPrimaryKeyField(new TestTeaBean()));
    }

    public void testTestGetPrimaryKeyField() {
        assertNull(ReflectionUtil.getPrimaryKeyField(TestStuBean.class));
        assertEquals(ReflectionTestUtil.getField(TestTeaBean.class, "age"),ReflectionUtil.getPrimaryKeyField(TestTeaBean.class));
    }

    public void testIsFliter() {
        assertTrue(ReflectionUtil.isFliter(ReflectionTestUtil.getField(TestTeaBean.class, "map")));
        assertFalse(ReflectionUtil.isFliter(ReflectionTestUtil.getField(TestTeaBean.class, "age")));
    }

    public void testGetCountColumnName() {
        PowerMockito.when(TextUtils.isEmpty(null)).thenReturn(true);
        assertEquals("name", ReflectionUtil.getCountColumnName(TestStuBean.class));
        assertEquals("age", ReflectionUtil.getCountColumnName(TestTeaBean.class));
        assertNull(ReflectionUtil.getCountColumnName(TestEmptyBean.class));


    }
}