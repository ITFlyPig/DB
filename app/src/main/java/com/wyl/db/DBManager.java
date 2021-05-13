package com.wyl.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/8
 * 描述    : 提供数据库的增、删、该、查，隔离使用者与数据库（即使用者无法直接操作数据库），
 * 避免数据库连接忘关闭导致的资源泄漏。
 * 1、解决SQLLite不允许多线程写的问题
 */
public class DBManager {
    private static final int ERROR_CODE = -1;
    private static final int SUCCESS_CODE = 0;
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
    public <T> List<T> query(String sql, String[] selectionArgs, Class<T> entityClz) {
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
     * 插入到数据库
     * @param entity
     * @return
     */
    public long insert(Object entity) {
        if (entity == null) return -1;
        // 反射获取对象的值并放到ContentValues
        Class<?> clz = entity.getClass();
        Field[] fields = clz.getDeclaredFields();
        ContentValues values = new ContentValues(fields.length);
        for (Field field : fields) {
            field.setAccessible(true);
            // 主键
            PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
            // 主键自动生成，不需要设置值
            if (primaryKey != null && primaryKey.autoGenerate()) {
                continue;
            }

            fillWithFieldValue(entity, values, field);
        }

        // 获取数据库的表名
        String tableName = ReflectionUtil.getTableName(clz);

        // 将数据掺入到数据库
        SQLiteDatabase database = mDBPool.borrowSQLiteDatabase();
        if (database == null) return ERROR_CODE;
        long ret = ERROR_CODE;
        try {
            ret = database.insert(tableName, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mDBPool.returnSQLiteDatabase(database);
        }

        return ret;
    }

    /**
     * 批量插入到数据库
     *
     * @param entitys
     * @return
     */
    public <T> int insert(List<T> entitys) {
        int len = entitys == null ? 0 : entitys.size();
        if (len == 0) {
            return SUCCESS_CODE;
        }

        SQLiteDatabase database = mDBPool.borrowSQLiteDatabase();
        if (database == null) return ERROR_CODE;
        boolean isSuccess = true;
        try {
            // 开始事务
            database.beginTransaction();
            // 反射获取对象的值并放到ContentValues
            for (T entity : entitys) {
                Class<?> clz = entity.getClass();
                Field[] fields = clz.getDeclaredFields();
                ContentValues values = new ContentValues(fields.length);
                for (Field field : fields) {
                    field.setAccessible(true);
                    // 主键
                    PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
                    // 主键自动生成，不需要设置值
                    if (primaryKey != null && primaryKey.autoGenerate()) {
                        continue;
                    }
                    fillWithFieldValue(entity, values, field);
                }

                // 获取数据库的表名
                String tableName = ReflectionUtil.getTableName(clz);
                long ret = database.insert(tableName, null, values);
                if (ret == ERROR_CODE) {
                    isSuccess = false;
                    break;
                }
            }

            if (isSuccess) {
                database.setTransactionSuccessful();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.endTransaction();
            mDBPool.returnSQLiteDatabase(database);

        }
        return isSuccess ? SUCCESS_CODE : ERROR_CODE;

    }

    /**
     * 获取字段的值，填充到ContentValues中
     *
     * @param entity
     * @param values
     * @param field
     */
    private void fillWithFieldValue(Object entity, ContentValues values, Field field) {
        if (entity == null || values == null || field == null) return;
        String columnName = ReflectionUtil.getColumnName(field);
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
        } else if (ReflectionUtil.isByteArr(type)) {
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

    /**
     * 批量删除，只要有一个删除失败，那么此次操作就不会有任何删除
     *
     * @param entitys
     * @param <T>
     * @return 返回是否都删除成功
     */
    public <T> int delete(List<T> entitys) {
        String tag = DB.conf.getLogTag();
        int len = entitys == null ? 0 : entitys.size();
        if (len == 0) {
            return SUCCESS_CODE;
        }

        SQLiteDatabase database = mDBPool.borrowSQLiteDatabase();
        if (database == null) {
            Log.e(tag, "delete 操作失败：从数据库连接池获取到的连接为空，实体集合为：" + entitys);
            return ERROR_CODE;
        }
        boolean isSuccess = true;
        database.beginTransaction();
        try {
            for (T entity : entitys) {
                int ret = realDelete(database, entity);
                if (ret == ERROR_CODE) {
                    isSuccess = false;
                    break;
                }
            }
            if (isSuccess) {
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.endTransaction();
        }

        return isSuccess ? SUCCESS_CODE : ERROR_CODE;
    }

    /**
     * 删除实体在表中对应的数据（必须要求实体有主键）
     *
     * @param entity
     * @param <T>
     * @return
     */
    public <T> int delete(T entity) {
        SQLiteDatabase database = mDBPool.borrowSQLiteDatabase();
        int ret = realDelete(database, entity);
        mDBPool.returnSQLiteDatabase(database);
        return ret;
    }

    private <T> int realDelete(SQLiteDatabase database, T entity) {
        String tag = DB.conf.getLogTag();
        if (database == null) {
            Log.e(tag, "realDelete 操作失败：传入的数据库连接为空");
            return ERROR_CODE;
        }

        if (entity == null) {
            Log.e(tag, "delete 操作失败：传入的需要删除对象为空");
            return ERROR_CODE;
        }

        String tableName = ReflectionUtil.getTableName(entity.getClass());
        if (TextUtils.isEmpty(tableName)) {
            Log.e(tag, "delete 操作失败：表名获取失败, 实体：" + entity);
            return ERROR_CODE;
        }

        Field primaryKeyField = ReflectionUtil.getPrimaryKeyField(entity);
        if (primaryKeyField == null) {
            Log.e(tag, "delete 操作失败：未找到主键, 实体：" + entity);
            return ERROR_CODE;
        }

        Object primaryKey = null;
        try {
            primaryKey = primaryKeyField.get(entity);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if (primaryKey == null) {
            Log.e(tag, "delete 操作失败：获取到的主键值为null, 实体：" + entity);
            return ERROR_CODE;
        }
        // 开始删除
        return database.delete(tableName, ReflectionUtil.getColumnName(primaryKeyField) + " = ?", new String[]{primaryKey.toString()});
    }
}
