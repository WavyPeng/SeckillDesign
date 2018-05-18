package com.wavy.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 访问限制
 * Created by WavyPeng on 2018/5/18.
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface AccessLimit {
    // 限制时间
    int seconds();
    // 最多访问次数
    int maxCount();
    // 是否需要登录
    boolean needLogin() default true;
}
