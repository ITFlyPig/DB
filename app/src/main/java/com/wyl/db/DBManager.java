package com.wyl.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/8
 * 描述    : 提供数据库的增、删、该、查，隔离使用者与数据库（即使用者无法直接操作数据库），
 * 避免数据库连接忘关闭导致的资源泄漏。
 * 1、解决SQLLite不允许多线程写的问题
 */
public class DBManager {
    private IDBPool mDBPool;

    public DBManager(IDBPool mDBPool) {
        this.mDBPool = mDBPool;
    }

    /**
     * 查询
     *
     * @param sql
     * @param <T>
     * @return
     */
    public <T> ArrayList<T> query(String sql, String[] selectionArgs, Class<T> entityClz) {
        if (TextUtils.isEmpty(sql) || entityClz == null) return null;
        SQLiteDatabase database = mDBPool.borrowSQLiteDatabase();
        if (database == null) return null;
        // 数据库查询
        Cursor cursor = null;
        ArrayList<T> data = null;
        try {
            cursor = database.rawQuery(sql, selectionArgs);
            data = ReflectionUtil.parseBeans(cursor, entityClz);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            // 将使用到数据库连接放回连接池
            mDBPool.returnSQLiteDatabase(database);
        }
        return data;
    }

    /**
     * 将对象数据插入到数据库
     *
     * @param entity
     * @return
     */
    public long insert(Object entity) {
        if (entity == null) return -1;
        // 从数据库连接池获取连接
        SQLiteDatabase database = mDBPool.borrowSQLiteDatabase();
        if (database == null) return -1;

        // 反射获取对象的值并放到ContentValues
        Class<?> clz = entity.getClass();
        Field[] fields = clz.getDeclaredFields();
        ContentValues values = new ContentValues(fields.length);
        for (Field field : fields) {
            field.setAccessible(true);

            // 获取字段的注解信息
            ColumnInfo columnInfo = field.getAnnotation(ColumnInfo.class);

            // 主键自动生成，不需要设置值
            if (columnInfo != null && columnInfo.isPrimaryKey()) {
                continue;
            }
            // 通过注解获取字段对应的列名
            String columnName = null;
            if (columnInfo != null) {
                columnName = columnInfo.name();
            }

            // 默认使用字段名
            if (TextUtils.isEmpty(columnName)) {
                columnName = field.getName();
            }

            Class<?> type = field.getType();

            if (Byte.class == type || byte.class == type) {
                Byte value = getValue(entity, field);
                values.put(columnName, value);
            } else if (Float.class == type || float.class == type) {
                Float value = getValue(entity, field);
                values.put(columnName, value);
            } else if (Short.class == type || short.class == type) {
                Short value = getValue(entity, field);
                values.put(columnName, value);
            } else if (Double.class == type || double.class == type) {
                Double value = getValue(entity, field);
                values.put(columnName, value);
            } else if (String.class == type) {
                String value = getValue(entity, field);
                values.put(columnName, value);
            } else if (Boolean.class == type || boolean.class == type) {
                Boolean value = getValue(entity, field);
                values.put(columnName, value);
            } else if (Integer.class == type || int.class == type) {
                Integer value = getValue(entity, field);
                values.put(columnName, value);
            } else if (Long.class == type || long.class == type) {
                Long value = getValue(entity, field);
                values.put(columnName, value);
            } else if (isByteArr(type)) {
                byte[] value = getValue(entity, field);
                values.put(columnName, value);
            } else { // 这里的类型，使用用户提供的转换器进行转换
                Object obj = getValue(entity, field);
                if (obj != null) {
                    String json = new Gson().toJson(obj);
                    values.put(columnName, json);
                }

            }
        }

        // 获取数据库的表名
        String tableName = null;
        Table table = entity.getClass().getAnnotation(Table.class);
        if (table != null) {
            tableName = table.name();
        }

        if (TextUtils.isEmpty(tableName)) {
            tableName = entity.getClass().getSimpleName();
        }

        // 将数据掺入到数据库
        return database.insert(tableName, null, values);
    }

    /**
     * 是否是字节数组
     *
     * @param clz
     * @return
     */
    private boolean isByteArr(Class<?> clz) {
        if (clz == null) return false;
        return clz.isArray() && (clz.getComponentType() == Byte.class || clz.getComponentType() == byte.class);

    }

    /**
     * 获取对象中对应字段的值
     *
     * @param obj
     * @param field
     * @param <T>
     * @return
     */
    private <T> T getValue(Object obj, Field field) {
        if (obj == null || field == null) return null;
        Object value = null;
        try {
            value = field.get(obj);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if (value == null) return null;
        return (T) value;
    }


}
