package com.jiahao.annotation;

import java.lang.annotation.*;


/**
 * 列
 * @author JiaHao
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Column {
    //字段名称
    String name();
    //字段类型
    String type() default "String";
    //字段长度
    int length() default 20;
}
