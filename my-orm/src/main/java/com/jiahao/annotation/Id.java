package com.jiahao.annotation;

import java.lang.annotation.*;

/**
 * 主键id
 * @author JiaHao
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Id {
    //字段名称
    String name();
    //字段类型
    String type() default "int";
    //字段长度
    int length() default 20;
    //自增 默认自增
    int increment() default 1;
    
}
