package com.wyl.db.annotations;

import androidx.room.Insert;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 创建人   : yuelinwang
 * 创建时间 : 2021/5/11
 * 描述    : 主键
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface PrimaryKey {
    /**
     * 是否自增
     * @return
     */
    boolean autoGenerate() default false;
}
