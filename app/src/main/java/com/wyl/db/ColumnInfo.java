package com.wyl.db;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/9
 * 描述    : 存列信息
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface ColumnInfo {
    /**
     * 列的名字
     *
     * @return
     */
    String name() default "";

    /**
     * 是否是主键
     * @return
     */
    boolean isPrimaryKey() default false;

}
