package com.wyl.db.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/24
 * 描述    : 标记忽略的字段
 * @author yuelinwang
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public  @interface Ignore {
}
