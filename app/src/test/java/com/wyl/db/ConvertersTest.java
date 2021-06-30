package com.wyl.db;

import com.wyl.db.converter.ITypeConverter;
import com.wyl.db.converter.TypeConverters;

import junit.framework.TestCase;

import org.junit.runner.RunWith;

import java.lang.reflect.Method;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/10
 * 描述    :
 */
public class ConvertersTest extends TestCase {

    public void testList2String() {
        ITypeConverter iConverter = new TypeConverters();
        Class<?> clz = iConverter.getClass();
        Method[] methods = clz.getDeclaredMethods();
        for (Method method : methods) {
            // 方法的参数类型
            Class<?>[] parameterTypes = method.getParameterTypes();
            Class<?> returnType = method.getReturnType();
            System.out.println("returnType:" + returnType);
        }


    }
}