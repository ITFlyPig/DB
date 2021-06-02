package com.wyl.db.converter;

import com.wyl.db.DB;

import java.lang.reflect.Method;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/11
 * 描述    : 类型转换的工具列
 * @author yuelinwang
 */
public class TypeConverterUtil {
    public static Class<?> findByType(Class<?> in) {
        if (in == null) {
            return null;
        }
        // 获取类型转换器
        ITypeConverter converter = DB.getConf().getConverter();
        if (converter == null) {
            return null;
        }

        Method[] methods = converter.getClass().getMethods();
        for (Method method : methods) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length == 1 ) {

            }

        }
        return null;

    }
}
