package com.keluokeda;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * item bean 上的注解
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface Item {
    /**
     * 获取 item 关联的 layout id
     *
     * @return id
     */
    int resource();


}
