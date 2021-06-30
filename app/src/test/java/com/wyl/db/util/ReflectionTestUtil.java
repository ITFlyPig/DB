package com.wyl.db.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author : yuelinwang
 * @time : 6/30/21
 * @desc : 单元测试中的反射相关的工具
 */
public class ReflectionTestUtil {
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

    public static Field getField(Class<?> clz, String filedName) {
        try {
            return  clz.getDeclaredField(filedName);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }
}
