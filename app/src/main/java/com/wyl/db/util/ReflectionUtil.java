package com.wyl.db.util;

import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.wyl.db.annotations.ColumnInfo;
import com.wyl.db.DB;
import com.wyl.db.annotations.Ignore;
import com.wyl.db.converter.ITypeConverter;
import com.wyl.db.annotations.PrimaryKey;
import com.wyl.db.annotations.Table;

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
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
 *
 * @author yuelinwang
 */
public class ReflectionUtil {

    /**
     * 从游标中解析出数据模型
     *
     * @param cursor
     * @param clz
     * @return
     */
    public static <T> ArrayList<T> parseBeans(Cursor cursor, Class<T> clz) {
        if (cursor == null || clz == null) {
            return null;
        }
        // 获取对象所具有的字段
        HashMap<String, Field> fieldsMap = getFields(clz);

        ArrayList<T> data = new ArrayList<>(cursor.getCount());

        // 将游标中的数据填充到对象中
        while (cursor.moveToNext()) {
            T obj = newInstance(clz);
            if (obj == null) {
                continue;
            }
            //取出一行的所有数据
            int count = cursor.getColumnCount();
            for (int i = 0; i < count; i++) {
                // 将游标中columnIndex对应的值取出来的数据填充到对象对应字段
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
        Field field = fieldsMap.get(columnName);
        if (field == null) {
            return;
        }
        field.setAccessible(true);
        // 列数据类型
        int type = cursor.getType(columnIndex);
        switch (type) {
            case FIELD_TYPE_NULL:
                break;
            case FIELD_TYPE_INTEGER:
                int n = cursor.getInt(columnIndex);
                fillField(field, obj, n);
                break;
            case FIELD_TYPE_FLOAT:
                float f = cursor.getFloat(columnIndex);
                fillField(field, obj, f);
                break;
            case FIELD_TYPE_STRING:
                // 还需要看字段对应的数据类型来做转换
                String s = cursor.getString(columnIndex);
                fillField(field, obj, s);
                break;
            case FIELD_TYPE_BLOB:
                byte[] byteArr = cursor.getBlob(columnIndex);
                fillField(field, obj, byteArr);
                break;
            default:

        }
    }

    /**
     * 将int类型的数据恢复到数据模型中
     *
     * @param field
     * @param obj
     * @param value
     */
    public static void fillField(Field field, Object obj, int value) {
        if (field == null || obj == null) {
            return;
        }
        Class<?> type = field.getType();
        try {
            if (type == byte.class) {
                field.setByte(obj, (byte) value);
            } else if (type == Byte.class) {
                field.set(obj, (byte) value);
            } else if (type == short.class) {
                field.setShort(obj, (short) value);
            } else if (type == Short.class) {
                field.set(obj, (short) value);
            } else if (type == int.class) {
                field.setInt(obj, (int) value);
            } else if (type == Integer.class) {
                field.set(obj, value);
            } else if (type == long.class) {
                field.setLong(obj, (long) value);
            } else if (type == Long.class) {
                field.set(obj, (long) value);
            } else if (type == boolean.class || type == Boolean.class) {
                field.set(obj, value == 1);
            } else {
                fillComplexField(field, obj, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将float类型的数据恢复到数据模型中
     *
     * @param field
     * @param obj
     * @param value
     */
    public static void fillField(Field field, Object obj, float value) {
        if (field == null || obj == null) {
            return;
        }
        Class<?> type = field.getType();
        try {
            if (type == float.class) {
                field.setFloat(obj, (float) value);
            } else if (type == Float.class) {
                field.set(obj, (float) value);
            } else if (type == double.class) {
                field.setDouble(obj, (double) value);
            } else if (type == Double.class) {
                field.set(obj, (double) value);
            } else {
                fillComplexField(field, obj, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将String类型的数据恢复到数据模型中
     *
     * @param field
     * @param obj
     * @param value
     */
    public static void fillField(Field field, Object obj, String value) {
        if (field == null || obj == null || value == null) {
            return;
        }
        Class<?> type = field.getType();
        try {
            if (type == String.class) {
                field.set(obj, value);
            } else {
                fillComplexField(field, obj, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将复杂类型的数据填充到到字段
     *
     * @param field
     * @param obj
     * @param value
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private static void fillComplexField(Field field, Object obj, Object value) throws InvocationTargetException, IllegalAccessException {
        if (field == null || obj == null || value == null) {
            return;
        }
        Type type = field.getGenericType();
        ITypeConverter converter = DB.getConf().getConverter();
        if (converter != null) {
            Method method = ReflectionUtil.findByReturnAndParamType(converter.getClass(), type, value.getClass());
            if (method != null) {
                Object oValue = method.invoke(converter, value);
                field.set(obj, oValue);
                return;
            }
        }
        LogUtil.e(DB.tag(), "int 类型的值无法放到" + type + " 类型的字段中，字段名为：" + field.getName());

    }

    /**
     * 将字节数组类型的数据恢复到数据模型中
     *
     * @param field
     * @param obj
     * @param bytes
     */
    private static void fillField(Field field, Object obj, byte[] bytes) {
        if (field == null || obj == null || bytes == null) {
            return;
        }
        Class<?> type = field.getType();
        try {
            if (isByteArr(type)) {
                field.set(obj, bytes);
            } else {
                // 尝试使用转换器
                fillComplexField(field, obj, bytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取申明的字段
     *
     * @param clz
     * @return
     */
    public static HashMap<String, Field> getFields(Class<?> clz) {
        Field[] fields = clz.getDeclaredFields();
        HashMap<String, Field> fieldsMap = new HashMap<>(fields.length);
        for (Field field : fields) {
            fieldsMap.put(ReflectionUtil.getColumnName(field), field);
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

    /**
     * 获取类对应的表名
     *
     * @param clz
     * @return
     */
    public static String getTableName(Class<?> clz) {
        if (clz == null) {
            return "";
        }
        String tableName = null;
        // 使用注解里面的表名
        Table table = clz.getAnnotation(Table.class);
        if (table != null) {
            tableName = table.name();
        }
        // 使用类名作为表名
        if (TextUtils.isEmpty(tableName)) {
            tableName = clz.getSimpleName();
        }
        return tableName;
    }

    /**
     * 获取字段对应的列名
     *
     * @param field
     * @return
     */
    public static String getColumnName(Field field) {
        if (field == null) {
            return null;
        }
        String columnName = null;
        // 从注解获取
        ColumnInfo columnInfo = field.getAnnotation(ColumnInfo.class);
        if (columnInfo != null) {
            columnName = columnInfo.name();
        }
        // 默认使用字段名
        if (TextUtils.isEmpty(columnName)) {
            columnName = field.getName();
        }
        return columnName;
    }

    /**
     * 是否是字节数组
     *
     * @param clz
     * @return
     */
    public static boolean isByteArr(Class<?> clz) {
        if (clz == null) {
            return false;
        }
        return clz.isArray() && (clz.getComponentType() == Byte.class || clz.getComponentType() == byte.class);
    }

    /**
     * 判断两个Type是否相等
     *
     * @param type1
     * @param type2
     * @return
     */
    public static boolean typeEqual(Type type1, Type type2) {
        if (type1 == null || type2 == null) {
            return false;
        }
        // 具体的不具有泛型的类型
        if (type1 instanceof Class && type2 instanceof Class) {
            return type1 == type2;
        } else if (type1 instanceof TypeVariable && type2 instanceof TypeVariable) {
            //泛型变量，如T
            Type[] bounds1 = ((TypeVariable) type1).getBounds();
            Type[] bounds2 = ((TypeVariable) type2).getBounds();
            return typesEqual(bounds1, bounds2);

        } else if (type1 instanceof WildcardType && type2 instanceof WildcardType) {
            // 通配符，如？
            WildcardType wildcardType1 = (WildcardType) type1;
            WildcardType wildcardType2 = (WildcardType) type2;
            return typesEqual(wildcardType1.getLowerBounds(), wildcardType2.getLowerBounds()) && typesEqual(wildcardType1.getUpperBounds(), wildcardType2.getUpperBounds());
        } else if (type1 instanceof ParameterizedType && type2 instanceof ParameterizedType) {
            // 参数化类型
            ParameterizedType parameterizedType1 = (ParameterizedType) type1;
            ParameterizedType parameterizedType2 = (ParameterizedType) type2;
            return typesEqual(parameterizedType1.getActualTypeArguments(), parameterizedType2.getActualTypeArguments());
        } else if (type1 instanceof GenericArrayType && type2 instanceof GenericArrayType) {
            // 泛型数组，如T[]
            Type genericComponentType1 = ((GenericArrayType) type1).getGenericComponentType();
            Type genericComponentType2 = ((GenericArrayType) type2).getGenericComponentType();
            return typeEqual(genericComponentType1, genericComponentType2);
        }
        return false;
    }


    /**
     * 判断两个数组type 是否相等
     *
     * @param types1
     * @param types2
     * @return
     */
    public static boolean typesEqual(Type[] types1, Type[] types2) {
        if (types1 == null && types2 == null) {
            return true;
        } else if (types1 != null && types2 != null && types1.length == types2.length) {
            // 传入的type数组不为null，但是没有元素的情况
            if (types1.length == 0) {
                return true;
            }

            boolean isEqual = false;
            for (int i = 0; i < types1.length; i++) {
                isEqual = typeEqual(types1[i], types2[i]);
                // 只要检查到不相等，立即返回
                if (!isEqual) {
                    break;
                }
            }
            return isEqual;

        } else { // 有一个的边界为null，另一个不为null or 两个边界长度不等
            return false;
        }
    }

    /**
     * 在类中找到方法参数类型和type匹配的方法
     *
     * @param clz
     * @param paramType
     * @return
     */
    public static Method findByParamType(Class<?> clz, Type paramType) {
        if (clz == null || paramType == null) {
            return null;
        }
        Method foundMethod = null;
        Method[] methods = clz.getDeclaredMethods();
        for (Method method : methods) {
            Type[] genericParameterTypes = method.getGenericParameterTypes();
            if (genericParameterTypes.length != 1) {
                continue;
            }
            if (ReflectionUtil.typeEqual(genericParameterTypes[0], paramType)) {
                foundMethod = method;
                break;
            }
        }
        return foundMethod;
    }

    /**
     * 从类中找到返回值类型和参数类型与传入类型匹配的方法
     * 在这里因为是 db -> obj，所以，这里的参数paramType只会是String、字节数组
     *
     * @param clz
     * @param returnType
     * @return
     */
    public static Method findByReturnAndParamType(Class<?> clz, Type returnType, Type paramType) {
        if (clz == null || returnType == null) {
            return null;
        }
        Method foundMethod = null;
        Method[] methods = clz.getDeclaredMethods();
        for (Method method : methods) {
            Type genericReturnType = method.getGenericReturnType();
            Type[] genericParameterTypes = method.getGenericParameterTypes();
            if (genericParameterTypes.length != 1) {
                continue;
            }
            if (ReflectionUtil.typeEqual(genericReturnType, returnType) && ReflectionUtil.typeEqual(genericParameterTypes[0], paramType)) {
                foundMethod = method;
                break;
            }
        }
        return foundMethod;
    }

    /**
     * 据名称获取Field
     * @param clz
     * @param fieldName
     * @return
     */
    public static Field getByName(Class<?> clz, String fieldName) {
        if (clz == null || TextUtils.isEmpty(fieldName)) {
            return null;
        }
        try {
            return clz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean equals(CharSequence a, CharSequence b) {
        if (a == b) {
            return true;
        }
        int length;
        if (a != null && b != null && (length = a.length()) == b.length()) {
            if (a instanceof String && b instanceof String) {
                return a.equals(b);
            } else {
                for (int i = 0; i < length; i++) {
                    if (a.charAt(i) != b.charAt(i)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 获取作为主键的字段
     *
     * @param obj
     * @return
     */
    public static Field getPrimaryKeyField(Object obj) {
        if (obj == null) {
            return null;
        }
        return getPrimaryKeyField(obj.getClass());
    }

    /**
     * 获取主键
     *
     * @param clz
     * @return
     */
    public static Field getPrimaryKeyField(Class<?> clz) {
        if (clz == null) {
            return null;
        }
        Field[] fields = clz.getDeclaredFields();
        for (Field field : fields) {
            PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
            if (primaryKey != null) {
                return field;
            }
        }
        return null;
    }

    /**
     * 表示字段是否需要过滤掉
     *
     * @param field
     * @return
     */
    public static boolean isFliter(Field field) {
        if (field == null) {
            return true;
        }
        return field.getAnnotation(Ignore.class) != null;
    }

    /**
     * 获取用于count函数的列名称
     *
     * @param clz
     * @return
     */
    public static String getCountColumnName(Class<?> clz) {
        if (clz == null) {
            return null;
        }

        Field[] fields = clz.getFields();
        if (fields == null || fields.length == 0) {
            return null;
        }
        String columnName = null;
        for (Field field : fields) {
            PrimaryKey key = field.getAnnotation(PrimaryKey.class);
            if (key == null) {
                continue;
            }
            columnName = getColumnName(field);
            break;
        }

        // 如果未获取到主键对应的列名，则使用Field数组中的第一个字段名
        if (TextUtils.isEmpty(columnName)) {
            columnName = getColumnName(fields[0]);
        }
        return columnName;
    }
}
