package com.wyl.db.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author : yuelinwang
 * @time : 6/30/21
 * @desc : 单元测试中的反射相关的工具
 */
public class ReflectionTestUtil {
    /**
     * 据方法的名字获取方法
     * @param clz
     * @param methodName
     * @return
     */
    public static Method getMethod(Class<?> clz, String methodName) {
        if (methodName == null)  {
            return null;
        }
        Method[] methods = clz.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                method.setAccessible(true);
                return method;
            }
        }
        return null;
    }

    /**
     * 据字段的名字获取字段
     * @param clz
     * @param filedName
     * @return
     */
    public static Field getField(Class<?> clz, String filedName) {
        try {
            return  clz.getDeclaredField(filedName);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 设置字段的值
     * @param filedName
     * @param obj
     * @param value
     * @return
     */
    public static boolean setFieldValue(String filedName, Object obj, Object value) {
        if (filedName == null || obj == null) {
            throw new IllegalArgumentException("参数filedName或者obj不能为空");
        }
        Field field = getField(obj.getClass(), filedName);
        if (field == null) {
            throw new IllegalStateException("据名称未获取到字段");
        }
        field.setAccessible(true);
        try {
            field.set(obj, value);
            return true;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;

    }
 }
