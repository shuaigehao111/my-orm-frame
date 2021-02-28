package com.jiahao.annotation;

import java.lang.annotation.*;


/**
 * 表
 * @author JiaHao
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Table {
    //表名
    String name();
}
