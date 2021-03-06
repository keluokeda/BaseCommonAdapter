package com.keluokeda;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface Bind {
    int viewId();

    Class binderClass();

    Class viewClass();

    boolean click() default false;
}
