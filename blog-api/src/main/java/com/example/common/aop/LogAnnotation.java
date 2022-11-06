package com.example.common.aop;

import java.lang.annotation.*;

/**
 * 日志注解
 */
//METHOD表示注解放在方法上，Type代表放在类上
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogAnnotation {

    String module() default "";

    String operation() default "";
}
