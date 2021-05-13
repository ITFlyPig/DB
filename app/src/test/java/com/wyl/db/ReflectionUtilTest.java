package com.wyl.db;

import com.wyl.db.bean.User;

import junit.framework.TestCase;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/11
 * 描述    :
 */
public class ReflectionUtilTest extends TestCase {

    public void testTypeEqual() {
        TypeConvertersTest converter = new TypeConvertersTest();
        Class<?> clz = converter.getClass();
        Method[] methods = clz.getDeclaredMethods();
        // 测试 参数是 String、int、byte[]、T、T extends User、ArrayList<User>、HashMap<String, String>、HashMap<String, User>、HashMap<String, ArrayList<String>>
        //HashMap<String, ? extends User>、ArrayList<? extends User>
        assert (ReflectionUtil.typesEqual(methods[0].getGenericParameterTypes(), methods[1].getGenericParameterTypes()));



    }

    public void testFillField() {

        User user = new User();
        Field field = ReflectionUtil.getByName(user.getClass(), "custom");
        Field arrayList = ReflectionUtil.getByName(user.getClass(), "arrayList");
        Field hashMap = ReflectionUtil.getByName(user.getClass(), "hashMap");
        Field aShort = ReflectionUtil.getByName(user.getClass(), "aShort");
        Field aDouble = ReflectionUtil.getByName(user.getClass(), "aDouble");
        // 测试int -> short
        ReflectionUtil.fillField(aShort, user, 100);
        // 测试 float -> double
        ReflectionUtil.fillField(aDouble, user, 100F);

        ReflectionUtil.fillField(field, user, "你好啊");
        ReflectionUtil.fillField(arrayList, user, "String转List测试");
        ReflectionUtil.fillField(hashMap, user, "String转HashMap测试");
        System.out.println(user);

    }
}