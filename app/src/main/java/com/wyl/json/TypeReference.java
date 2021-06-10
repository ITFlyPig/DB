package com.wyl.json;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 指定反序列化的类型
 * TypeReference的存在是因为java中子类可以获取到父类泛型的真实类型
 */
public abstract class TypeReference<T> {
  public Type genericParamType() {
    //获取父类类型
    //由于是抽象类，其实现类必然是继承当前类，所以父类类型即是TypeMarker<XXX>
    // getClass().getGenericSuperclass() 获取父类中的参数化类型（ParameterizedType）
    // getGenericSuperclass返回一个Type类型的对象，代表实体（class, interface, primitive type or void）的直接父类，
    // 如果父类是参数化类型，则返回的Type对象可准确反映源代码中使用的实际type参数。
    Type superType = getClass().getGenericSuperclass();
    //如果没有指定泛型参数，则返回的Type实际类型为Class
    //未指定泛型参数时，默认将泛型视为Object类
    if (superType instanceof Class) {
      return Object.class;
    }
    //如果有泛型参数，则返回的Type实际类型为ParameterizedType
    //强转并获取泛型参数，即XXX的实际类型
    // ParameterizedType是一个记录类型泛型的接口, 继承自Type, 一共三方法:
    // Type[] getActualTypeArguments(); //返回泛型类型数组
    // Type getRawType(); //返回原始类型Type
    // Type getOwnerType(); //返回 Type 对象，表示此类型是其成员之一的类型。
    ParameterizedType parameterizedType = (ParameterizedType) superType;
    // getActualTypeArguments：获取父类真实的泛型类型，返回泛型类型数组
    Type argumentType = parameterizedType.getActualTypeArguments()[0];
    return argumentType;
  }
}
