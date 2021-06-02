package com.wyl.db.util;

import android.text.TextUtils;

import com.wyl.db.annotations.Ignore;

import java.lang.reflect.Field;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/8
 * 描述    : 数据库相关工具类
 * @author yuelinwang
 */
public class DBUtil {
    /**
     * 检查是否是db支持的插入类型
     * @param obj
     */
    public static boolean checkDBSupport(Object obj) {
        return obj == null || obj instanceof String || obj instanceof Byte || obj instanceof Short
                || obj instanceof Integer || obj instanceof Long || obj instanceof Float
                || obj instanceof Double || obj instanceof Boolean || obj instanceof byte[];
    }

    /**
     * 将 字段名称转为数据库对应的列名称：转换规则：将大写字母转为小写，前面加下划线
     *
     * @param fieldName
     * @return
     */
    public static String field2Column(String fieldName) {
        if (TextUtils.isEmpty(fieldName)) {
            return null;
        }
        StringBuilder builder = new StringBuilder(fieldName);
        for (int i = 0; i < builder.length(); i++) {
            char c = builder.charAt(i);
            if (Character.isUpperCase(c)) {
                builder.replace(i, i + 1, String.valueOf(Character.toLowerCase(c)));
                if (i != 0) {
                    builder.insert(i, '_');
                }
            }

        }
        return builder.toString();
    }

    /**
     * 将数据库列名转为对应的字段名：转换规则：去除下划线，并将下划线后面的一个字符大写
     * @param columnName
     * @return
     */
    public static String column2Field(String columnName) {
        if (TextUtils.isEmpty(columnName)) {
            return null;
        }
        StringBuilder builder = new StringBuilder(columnName);
        for (int i = 0; i < builder.length(); i++) {
            char c = builder.charAt(i);
            //删除下划线，并将后一个字母转为大写
            if (c == '_') {
                builder.delete(i, i +1);
                if (i < builder.length()) {
                    char underlineNext = builder.charAt(i);
                    builder.replace(i, i +1, String.valueOf(Character.toUpperCase(underlineNext)));
                }
            }
        }
        return builder.toString();
    }

}
