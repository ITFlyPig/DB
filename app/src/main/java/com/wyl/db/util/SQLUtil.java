package com.wyl.db.util;

import android.text.TextUtils;

import com.wyl.db.converter.ITypeConverter;
import com.wyl.db.annotations.PrimaryKey;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/11
 * 描述    : SQL工具
 * @author yuelinwang
 */
public class SQLUtil {
    /**
     * 返回实体对应的创建语句
     *
     * @param clz
     * @return
     */
    public static String createTableSQL(Class<?> clz, ITypeConverter converter) {
        if (clz == null) {
            return null;
        }
        String tableName = ReflectionUtil.getTableName(clz);
        if (TextUtils.isEmpty(tableName)) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        builder.append("create table if not exists");
        builder.append(" ").append(tableName).append(" ").append("(");

        Field[] fields = clz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            // 检查字段是否需要过滤
            if (ReflectionUtil.isFliter(fields[i])) {
                continue;
            }

            String columnSQL = columnCreateSQL(fields[i], converter);
            if (TextUtils.isEmpty(columnSQL)) {
                continue;
            }
            builder.append(columnSQL);
            if (i < fields.length - 1) {
                builder.append(",");
            }
        }
        builder.append(")");
        return builder.toString();

    }

    private static String columnCreateSQL(Field field, ITypeConverter converter) {
        String columnName = ReflectionUtil.getColumnName(field);
        if (TextUtils.isEmpty(columnName)) {
            throw new IllegalStateException("字段" + field.getName() + "对应的列名获取失败");
        }

        StringBuilder builder = new StringBuilder();
        String dbType = null;
        Class<?> type = field.getType();
        if (isSQLLiteSupport(type)) {
            dbType = getSQLLiteType(type);
        } else {
            //需要用convert找到对应的类型
            if (converter == null) {
                throw new IllegalStateException("ITypeConverter 为空，导致无法知道复杂类型字段" + field.getName() + "如何存数据库");
            }
            Type genericType = field.getGenericType();
            // 从转换器中找到对应数据类型的转换方法
            Method method = ReflectionUtil.findByParamType(converter.getClass(), genericType);
            if (method == null) {
                throw new IllegalStateException("未找到合适的类型转换器，导致无法知道复杂类型字段" + field.getName() + "如何存数据库");
            }
            Class<?> returnType = method.getReturnType();
            if (!isSQLLiteSupport(returnType)) {
                throw new IllegalStateException("字段 " + field.getName() + " 对应的转换器的返回值不是数据库支持的数据类型");
            }
            dbType = getSQLLiteType(returnType);
            if (!TextUtils.isEmpty(dbType)) {
                // 去除不为空的约束
                dbType = dbType.replaceAll("NOT NULL", "");
            }
        }
        if (!TextUtils.isEmpty(dbType)) {
            builder.append("`").append(columnName).append("` ").append(dbType);

            PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
            if (primaryKey != null) {
                // 主键
                builder.append(" PRIMARY KEY ");
                if (primaryKey.autoGenerate()) {
                    // 自增
                    builder.append(" AUTOINCREMENT ");
                }
            }

        }
        return builder.toString();
    }

    /**
     * 判断是否是数据库支持的类型
     *
     * @param type
     * @return
     */
    public static boolean isSQLLiteSupport(Class<?> type) {
        return type == byte.class || type == short.class || type == int.class || type == long.class || type == boolean.class
                || type == Byte.class || type == Short.class || type == Integer.class || type == Long.class
                || type == Boolean.class || type == float.class || type == double.class || type == Float.class
                || type == Double.class || type == String.class || ReflectionUtil.isByteArr(type);
    }

    /**
     * 据Java数据类型，获取SQLLite对应的数据类型
     *
     * @param type
     * @return
     */
    public static String getSQLLiteType(Class<?> type) {
        String dbType = null;
        if (type == byte.class || type == short.class || type == int.class || type == long.class || type == boolean.class) {
            dbType = "INTEGER NOT NULL";
        } else if (type == Byte.class || type == Short.class || type == Integer.class || type == Long.class || type == Boolean.class) {
            dbType = "INTEGER";
        } else if (type == float.class || type == double.class) {
            dbType = "REAL NOT NULL";
        } else if (type == Float.class || type == Double.class) {
            dbType = "REAL";
        } else if (type == String.class) {
            dbType = "TEXT";
        } else if (ReflectionUtil.isByteArr(type)) {
            dbType = "BLOB";
        }
        return dbType;
    }


}
