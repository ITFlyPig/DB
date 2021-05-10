package com.wyl.db;

import android.database.Cursor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import static android.database.Cursor.FIELD_TYPE_BLOB;
import static android.database.Cursor.FIELD_TYPE_FLOAT;
import static android.database.Cursor.FIELD_TYPE_INTEGER;
import static android.database.Cursor.FIELD_TYPE_NULL;
import static android.database.Cursor.FIELD_TYPE_STRING;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/8
 * 描述    : 反射工具类
 */
public class ReflectionUtil {

    /**
     * 将对象的值和字段名称放入到Map中
     *
     * @param obj
     * @return
     */
    public static HashMap<String, Object> parseValues(Object obj) {
        HashMap<String, Object> valuesMap = null;
        if (obj == null) return valuesMap;
        Class<?> clz = obj.getClass();
        // 获取对象的字段
        Field[] fields = clz.getDeclaredFields();
        valuesMap = new HashMap<>(fields.length);
        // 循环获取字段对应的值
        for (Field field : fields) {
            Object value = parseValue(obj, field);
            //检查获取到的值的类型是否是数据库支持的
            if (!DBUtil.checkDBSupport(value)) {
                value = null;
            }
            valuesMap.put(field.getName(), value);
        }
        return valuesMap;
    }

    /**
     * 获取字段对应的值
     *
     * @param obj
     * @param field
     * @return
     */
    public static Object parseValue(Object obj, Field field) {
        if (field == null || obj == null) return null;

        Object value = null;
        try {
            field.setAccessible(true);
            value = field.get(obj);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * 从游标中解析出数据模型
     *
     * @param cursor
     * @param clz
     * @return
     */
    public static <T> ArrayList<T> parseBeans(Cursor cursor, Class<T> clz) {
        if (cursor == null || clz == null) return null;

        // 获取对象所具有的字段
        HashMap<String, Field> fieldsMap = getFields(clz);

        ArrayList<T> data = new ArrayList<>(cursor.getCount());

        // 将游标中的数据填充到对象中
        while (cursor.moveToNext()) {
            T obj = newInstance(clz);
            if (obj == null) continue;
            //取出一行的所有数据
            int count = cursor.getColumnCount();
            for (int i = 0; i < count; i++) {
                // 将游标中取出来的数据填充到对应字段
                setField(cursor, fieldsMap, obj, i);
            }
            data.add(obj);

        }
        return data;

    }

    /**
     * 将游标中columnIndex对应的值填充到对象
     *
     * @param cursor
     * @param fieldsMap
     * @param obj
     * @param columnIndex
     */
    private static void setField(Cursor cursor, HashMap<String, Field> fieldsMap, Object obj, int columnIndex) {
        String columnName = cursor.getColumnName(columnIndex);
        Field field = fieldsMap.get(DBUtil.column2Field(columnName));
        if (field == null) {
            return;
        }
        field.setAccessible(true);
        // 列数据类型
        int type = cursor.getType(columnIndex);
        try {
            switch (type) {
                case FIELD_TYPE_NULL:
                    break;
                case FIELD_TYPE_INTEGER:
                    int n = cursor.getInt(columnIndex);
                    field.set(obj, n);
                    break;
                case FIELD_TYPE_FLOAT:
                    float f = cursor.getFloat(columnIndex);
                    field.set(obj, f);
                    break;
                case FIELD_TYPE_STRING:
                    // 还需要看字段对应的数据类型来做转换
                    String s = cursor.getString(columnIndex);
                    field.set(obj, s);
                    break;
                case FIELD_TYPE_BLOB:
                    byte[] byteArr = cursor.getBlob(columnIndex);
                    field.set(obj, byteArr);
                    break;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取申明的字段
     *
     * @param clz
     * @return
     */
    private static HashMap<String, Field> getFields(Class<?> clz) {
        Field[] fields = clz.getDeclaredFields();
        HashMap<String, Field> fieldsMap = new HashMap<>(fields.length);
        for (Field field : fields) {
            fieldsMap.put(field.getName(), field);
        }
        return fieldsMap;
    }

    /**
     * 创建Class 对应的对象
     *
     * @param clz
     * @return
     */
    private static <T> T newInstance(Class<T> clz) {
        T obj = null;
        try {
            obj = clz.newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return obj;
    }
}
