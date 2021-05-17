package com.wyl.db.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.wyl.db.DB;
import com.wyl.db.annotations.PrimaryKey;
import com.wyl.db.constant.Codes;
import com.wyl.db.converter.ITypeConverter;
import com.wyl.db.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

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
     * 批量插入
     *
     * @param entitys
     * @param <T>
     * @return
     */
    public <T> int bulkInsert(List<T> entitys) {
        int len = entitys == null ? 0 : entitys.size();
        if (len == 0) {
            return Codes.SUCCESS_CODE;
        }

        SQLiteDatabase database = SQLiteHelper.getInstance().getWritableDatabase();
        if (database == null) return Codes.ERROR_CODE;

        T first = entitys.get(0);
        if (first == null) return Codes.ERROR_CODE;

        //获取表名
        String tableName = ReflectionUtil.getTableName(first.getClass());
        if (TextUtils.isEmpty(tableName)) {
            return Codes.ERROR_CODE;
        }

        ContentValues values = fillContentValues(first);
        if (values == null) return Codes.ERROR_CODE;

        // 记录表的列名及顺序
        Set<String> keys = values.keySet();
        String[] args = new String[keys.size()];
        int i = 0;
        for (String key : keys) {
            args[i] = key;
            i++;
        }

        // insert语句
        String insertSQL = parseInsertSQL(values, null, tableName);
        if (TextUtils.isEmpty(insertSQL)) {
            return Codes.ERROR_CODE;
        }

        SQLiteStatement statement = database.compileStatement(insertSQL);
        if (statement == null) {
            return Codes.ERROR_CODE;
        }

        database.beginTransaction();
        long ret = 0;

        try {
            for (T entity : entitys) {
                // 记录该对象的字段及对应的值
                ContentValues valuesTemp = fillContentValues(entity);
                statement.clearBindings();
                if (valuesTemp == null) continue;
                // ContentValues -> sql
                for (int j = 0; j < args.length; j++) {
                    Object v = valuesTemp.get(args[j]);
                    // 数据的绑定
                    bind(statement, j + 1, v);

                }
                // 检查是否失败
                ret = statement.executeInsert();
                if (ret == -1) {
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            ret = -1;
        } finally {
            if (ret != -1) {
                database.setTransactionSuccessful();
            }
        }
        database.endTransaction();
        return ret == -1 ? Codes.ERROR_CODE : Codes.SUCCESS_CODE;
    }

    /**
     * 将基本数据绑定到 SQLiteStatement
     *
     * @param statement
     * @param j
     * @param v
     */
    private boolean bind(SQLiteStatement statement, int j, Object v) {
        if (v == null) {
            statement.bindNull(j);
        } else if (v instanceof Double || v instanceof Float) {
            statement.bindDouble(j, ((Number) v).doubleValue());
        } else if (v instanceof Number) {
            statement.bindLong(j, ((Number) v).longValue());
        } else if (v instanceof Boolean) {
            Boolean bool = (Boolean) v;
            if (bool) {
                statement.bindLong(j, 1);
            } else {
                statement.bindLong(j, 0);
            }
        } else if (v instanceof byte[]) {
            statement.bindBlob(j, (byte[]) v);
        } else if (v instanceof String) {
            statement.bindString(j, v.toString());
        } else {
            return false;
        }
        return true;
    }


    /**
     * 解析得到插入的sql语句
     *
     * @param values
     * @param nullColumnHack
     * @param tableName
     * @return
     */
    private String parseInsertSQL(ContentValues values, String nullColumnHack, String tableName) {
        if (values == null || TextUtils.isEmpty(tableName)) return null;
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT");
        sql.append(" INTO ");
        sql.append(tableName);
        sql.append('(');

        int size = values == null ? 0 : values.size();
        if (size > 0) {
            int i = 0;
            for (String colName : values.keySet()) {
                sql.append((i > 0) ? "," : "");
                sql.append(colName);
                i++;
            }
            sql.append(')');
            sql.append(" VALUES (");
            for (i = 0; i < size; i++) {
                sql.append((i > 0) ? ",?" : "?");
            }
        } else {
            sql.append(nullColumnHack).append(") VALUES (NULL");
        }
        sql.append(')');
        return sql.toString();
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
     * 简单数据的填充
     *
     * @param entity
     * @param values
     * @param field
     * @return
     */
    private boolean fillWithSimpleValue(Object entity, ContentValues values, Field field) {
        if (entity == null || values == null || field == null) return false;
        Object value = getValue(entity, field);
        return fillValue(value, values, field);
    }

    private boolean fillValue(Object value, ContentValues values, Field field) {
        if (values == null || field == null) return false;
        String columnName = ReflectionUtil.getColumnName(field);
        if (value == null) {
            values.putNull(columnName);
        } else {
            Class<?> type = field.getType();
            if (value instanceof Byte) {
                values.put(columnName, (byte) value);
            } else if (value instanceof Float) {
                values.put(columnName, (float) value);
            } else if (value instanceof Short) {
                values.put(columnName, (short) value);
            } else if (value instanceof Double) {
                values.put(columnName, (double) value);
            } else if (value instanceof String) {
                values.put(columnName, (String) value);
            } else if (value instanceof Boolean) {
                values.put(columnName, (boolean) value);
            } else if (value instanceof Integer) {
                values.put(columnName, (int) value);
            } else if (value instanceof Long) {
                values.put(columnName, (long) value);
            } else if (ReflectionUtil.isByteArr(type)) {
                values.put(columnName, (byte[]) value);
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * 复杂数据的填充：复杂数据 -> 简单数据 -> 填充
     *
     * @param entity
     * @param values
     * @param field
     * @return
     */
    private boolean fillWithComplexValue(Object entity, ContentValues values, Field field) {
        if (entity == null || values == null || field == null) return false;
        // 复杂的类型，使用转换器转换
        ITypeConverter converter = DB.getConf().getConverter();
        String tag = DB.getConf().getLogTag();
        if (converter == null) {
            Log.e(tag, "bind 绑定数据失败：未定义类型转换器");
            return false;
        }

        Type complexType = field.getGenericType();
        Method method = ReflectionUtil.findByParamType(converter.getClass(), complexType);
        if (method == null) {
            Log.e(tag, "bind 绑定数据失败：未找到类型匹配的方法");
            return false;
        }
        Object returnObj = null;
        try {
            //获取字段对应的值
            Object v = getValue(entity, field);
            // 将值进行转换
            returnObj = method.invoke(converter, v);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fillValue(returnObj, values, field);

    }

    /**
     * 获取字段的值，填充到ContentValues中
     *
     * @param entity
     * @param values
     * @param field
     */
    private boolean fillWithFieldValue(Object entity, ContentValues values, Field field) {
        if (!fillWithSimpleValue(entity, values, field)) {
            return fillWithComplexValue(entity, values, field);
        }
        return true;
    }


    /**
     * 是否是基本的类型
     *
     * @param type
     * @return
     */
    private boolean isBasicType(Class<?> type) {
        return Byte.class == type || byte.class == type
                || Float.class == type || float.class == type
                || Short.class == type || short.class == type
                || Double.class == type || double.class == type
                || String.class == type
                || Boolean.class == type || boolean.class == type
                || Integer.class == type || int.class == type
                || Long.class == type || long.class == type
                || ReflectionUtil.isByteArr(type);
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
