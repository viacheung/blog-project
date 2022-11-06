package com.example.common.cache;


import java.lang.annotation.*;
//功能：指明了修饰的这个注解的使用范围，即被描述的注解可以用在哪里。
@Target({ElementType.METHOD})
//功能：指明修饰的注解的生存周期，即会保留到哪个阶段。
@Retention(RetentionPolicy.RUNTIME)
//功能：指明修饰的注解，可以被例如javadoc此类的工具文档化，只负责标记，没有成员取值。
@Documented
//@interface意思是声明一个注解，方法名对应参数名，返回值类型对应参数类型。
public @interface Cache {

    long expire() default 1 * 60 * 1000;

    String name() default "";

}