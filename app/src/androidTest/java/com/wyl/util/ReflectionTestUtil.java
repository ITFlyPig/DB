package com.wyl.util;

import com.wyl.log.LogToDiskUtil;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.assertNotNull;

/**
 * @author : yuelinwang
 * @time : 6/30/21
 * @desc : 单元测试中的反射相关的工具
 */
public class ReflectionTestUtil {
    /**
     * 据方法的名字获取方法
     *
     * @param clz
     * @param methodName
     * @return
     */
    public static Method getMethod(Class<?> clz, String methodName) {
        if (methodName == null) {
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
     *
     * @param clz
     * @param filedName
     * @return
     */
    public static Field getField(Class<?> clz, String filedName) {
        try {
            return clz.getDeclaredField(filedName);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 设置字段的值
     *
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

    /**
     * 设置静态字段的值
     *
     * @param filedName
     * @param clz
     * @param value
     * @return
     */
    public static boolean setStaticFieldValue(String filedName, Class<?> clz, Object value) {
        if (filedName == null || clz == null) {
            throw new IllegalArgumentException("参数filedName或者obj不能为空");
        }
        Field field = getField(clz, filedName);
        if (field == null) {
            throw new IllegalStateException("据名称未获取到字段");
        }
        field.setAccessible(true);
        try {
            field.set(null, value);
            return true;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;

    }

    /**
     * 获取静态字段的值
     * @param filedName
     * @param clz
     * @return
     */
    public static Object getStaticFieldValue(String filedName, Class<?> clz) {
        if (filedName == null || clz == null) {
            throw new IllegalArgumentException("参数filedName或者obj不能为空");
        }
        Field field = getField(clz, filedName);
        if (field == null) {
            throw new IllegalStateException("据名称未获取到字段");
        }
        field.setAccessible(true);
        try {
            return field.get(null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取实例字段的值
     * @param obj
     * @param filedName
     * @return
     */
    public static Object getFieldValue(Object obj, String filedName) {
        if (filedName == null || obj == null) {
            throw new IllegalArgumentException("参数filedName或者obj不能为空");
        }
        Field field = getField(obj.getClass(), filedName);
        if (field == null) {
            throw new IllegalStateException("据名称未获取到字段");
        }
        field.setAccessible(true);
        try {
            return field.get(obj);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 反射调用静态方法
     *
     * @param staticMethodName
     * @param params
     */
    public static Object invokeStaticMethod(String staticMethodName, Object... params) {
        assertNotNull(staticMethodName);
        Method staticMethod = ReflectionTestUtil.getMethod(LogToDiskUtil.class, staticMethodName);
        staticMethod.setAccessible(true);
        try {
            if (params == null) {
                return staticMethod.invoke(params);
            } else {
                return staticMethod.invoke(null, params);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 反射调用实例方法
     * @param obj
     * @param methodName
     * @param params
     * @return
     */
    public static Object invokeMethod(Object obj, String methodName, Object... params) {
        assertNotNull(methodName);
        assertNotNull(obj);
        Method method = ReflectionTestUtil.getMethod(obj.getClass(), methodName);
        method.setAccessible(true);
        try {
            return method.invoke(obj, params);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
