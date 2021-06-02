package com.wyl.db.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/9
 * 描述    : 存列信息
 * @author yuelinwang
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface Table {
    /**
     * 列的名字
     *
     * @return
     */
    String name() default "";

}
