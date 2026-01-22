package com.sky.annotation;


import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * 自定义注解，用来标识方法需要进行哪种功能字段自动填充
 * */
@Target(ElementType.METHOD) //标注该注解用在方法上
@Retention(RetentionPolicy.RUNTIME) //标注该注解生效在运行时
public @interface AutoFill {
    // 数据库操作类型：插入、新增
    OperationType value();
}
