package com.wyl.db.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.wyl.db.DB;
import com.wyl.db.annotations.PrimaryKey;
import com.wyl.db.constant.Codes;
import com.wyl.db.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/8
 * 描述    : 提供数据库的增、删、该、查，隔离使用者与数据库（即使用者无法直接操作数据库）
 * 解决：1、解决多个SQLiteOpenHelper对象同时写的问题
 * 2、多个线程使用同一个SQLiteDatabase，但是SQLiteDatabase提前关闭导致的问题
 */
public class DBManager {

    /**
     * 查询
     *
     * @param sql
     * @param <T>
     * @return
     */
    public <T> List<T> query(String sql, String[] selectionArgs, Class<T> entityClz) {
        if (TextUtils.isEmpty(sql) || entityClz == null) return null;
        SQLiteDatabase database = SQLiteHelper.getInstance().getReadableDatabase();
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
        }
        return data;
    }

    /**
     * 插入到数据库
     *
     * @param entity
     * @return
     */
    public long insert(Object entity) {
        if (entity == null) return Codes.ERROR_CODE;
        // 反射获取对象的值并放到ContentValues
        ContentValues values = fillContentValues(entity);
        // 获取数据库的表名
        Class<?> clz = entity.getClass();
        String tableName = ReflectionUtil.getTableName(clz);

        // 将数据掺入到数据库
        SQLiteDatabase database = SQLiteHelper.getInstance().getReadableDatabase();
        if (database == null) return Codes.ERROR_CODE;
        long ret = Codes.ERROR_CODE;
        try {
            ret = database.insert(tableName, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * 更新
     *
     * @param entity
     * @param whereClause
     * @param whereArgs
     * @param <T>
     * @return
     */
    public <T> long update(T entity, String whereClause, String[] whereArgs) {
        if (entity == null) return Codes.ERROR_CODE;
        // 获取数据库的表名
        Class<?> clz = entity.getClass();
        String tableName = ReflectionUtil.getTableName(clz);

        // 反射获取对象的值并放到ContentValues
        ContentValues values = fillContentValues(entity);

        // 更新
        SQLiteDatabase database = SQLiteHelper.getInstance().getWritableDatabase();
        if (database == null) return Codes.ERROR_CODE;
        long ret = Codes.ERROR_CODE;
        try {
            ret = database.update(tableName, values, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;


    }

    /**
     * 更新实体对应在数据库中的数据
     *
     * @param entity
     * @param <T>
     * @return
     */
    public <T> long update(T entity) {
        if (entity == null) return Codes.ERROR_CODE;
        String tag = DB.getConf().getLogTag();
        // 获取主键
        Field field = ReflectionUtil.getPrimaryKeyField(entity);
        if (field == null) {
            Log.e(tag, "update更新操作失败：找不到主键，实体：" + entity);
            return Codes.ERROR_CODE;
        }

        Object primaryKey = getValue(entity, field);
        if (primaryKey == null) {
            Log.e(tag, "update更新操作失败：获取主键值失败，实体：" + entity);
            return Codes.ERROR_CODE;
        }

        String columnName = ReflectionUtil.getColumnName(field);

        // 获取数据库的表名
        Class<?> clz = entity.getClass();
        String tableName = ReflectionUtil.getTableName(clz);

        // 反射获取对象的值并放到ContentValues
        ContentValues values = fillContentValues(entity);


        // 将数据掺入到数据库
        SQLiteDatabase database = SQLiteHelper.getInstance().getWritableDatabase();
        if (database == null) return Codes.ERROR_CODE;
        long ret = Codes.ERROR_CODE;
        try {
            ret = database.update(tableName, values, columnName + " = ?", new String[]{primaryKey.toString()});
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * 将对象的数据填充到ContentValues中
     *
     * @param entity
     * @param <T>
     * @return
     */
    private <T> ContentValues fillContentValues(T entity) {
        if (entity == null) return new ContentValues();
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
        return values;
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
            return Codes.SUCCESS_CODE;
        }

        SQLiteDatabase database = SQLiteHelper.getInstance().getWritableDatabase();
        if (database == null) return Codes.ERROR_CODE;
        boolean isSuccess = true;
        try {
            // 开始事务
            database.beginTransaction();
            // 反射获取对象的值并放到ContentValues
            for (T entity : entitys) {
                Class<?> clz = entity.getClass();
                ContentValues values = fillContentValues(entity);

                // 获取数据库的表名
                String tableName = ReflectionUtil.getTableName(clz);
                long ret = database.insert(tableName, null, values);
                if (ret == Codes.ERROR_CODE) {
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
        return isSuccess ? Codes.SUCCESS_CODE : Codes.ERROR_CODE;

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
        String tag = DB.getConf().getLogTag();
        int len = entitys == null ? 0 : entitys.size();
        if (len == 0) {
            return Codes.SUCCESS_CODE;
        }

        SQLiteDatabase database = SQLiteHelper.getInstance().getWritableDatabase();
        if (database == null) {
            Log.e(tag, "delete 操作失败：从数据库连接池获取到的连接为空，实体集合为：" + entitys);
            return Codes.ERROR_CODE;
        }
        boolean isSuccess = true;
        database.beginTransaction();
        try {
            for (T entity : entitys) {
                int ret = realDelete(database, entity);
                if (ret == Codes.ERROR_CODE) {
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

        return isSuccess ? Codes.SUCCESS_CODE : Codes.ERROR_CODE;
    }

    /**
     * 删除实体在表中对应的数据（必须要求实体有主键）
     *
     * @param entity
     * @param <T>
     * @return
     */
    public <T> int delete(T entity) {
        SQLiteDatabase database = SQLiteHelper.getInstance().getWritableDatabase();
        return realDelete(database, entity);
    }

    private <T> int realDelete(SQLiteDatabase database, T entity) {
        String tag = DB.getConf().getLogTag();
        if (database == null) {
            Log.e(tag, "realDelete 操作失败：传入的数据库连接为空");
            return Codes.ERROR_CODE;
        }

        if (entity == null) {
            Log.e(tag, "delete 操作失败：传入的需要删除对象为空");
            return Codes.ERROR_CODE;
        }

        String tableName = ReflectionUtil.getTableName(entity.getClass());
        if (TextUtils.isEmpty(tableName)) {
            Log.e(tag, "delete 操作失败：表名获取失败, 实体：" + entity);
            return Codes.ERROR_CODE;
        }

        Field primaryKeyField = ReflectionUtil.getPrimaryKeyField(entity);
        if (primaryKeyField == null) {
            Log.e(tag, "delete 操作失败：未找到主键, 实体：" + entity);
            return Codes.ERROR_CODE;
        }

        Object primaryKey = null;
        try {
            primaryKey = primaryKeyField.get(entity);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if (primaryKey == null) {
            Log.e(tag, "delete 操作失败：获取到的主键值为null, 实体：" + entity);
            return Codes.ERROR_CODE;
        }
        // 开始删除
        return database.delete(tableName, ReflectionUtil.getColumnName(primaryKeyField) + " = ?", new String[]{primaryKey.toString()});
    }
}
