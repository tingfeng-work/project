package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;


/*
 * 自定义切面类，实现公共字段填充
 * */

@Aspect
@Component
@Slf4j
public class AutoFillAspect {


    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPoinCut() {
    }

    // 前置通知
    @Before("autoFillPoinCut()")
    public void autoFill(JoinPoint joinPoint) {
        log.info("开始进行公共字段填充...");

        /*
        获取到当前被拦截方法上的数据库操作类型：
        1、获取当前方法签名对象
        2、获取注解对象
        3、获得注解对象中的值：拿到数据库操作类型
        * */
        // 1、获取当前方法签名对象
        // 这里由切入点定义的注解 annotation + AutoFill 注解中的target限制进来的一定是方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        //2、通过方法签名对象拿到方法，进而获取注解对象
        AutoFill annotation = signature.getMethod().getAnnotation(AutoFill.class);

        // 3、获取注解中的值，拿到数据库操作类型
        OperationType operationType = annotation.value();

        /*通过反射为公共字段赋值
         * 1、拿到当前被拦截的方法的参数——实体对象
         * 2、准备数据
         * 3、根据当前操作的不同类型，拿到对应的属性的setter
         * 4、通过调用拿到的对应属性的 setter 进行赋值
         * */
        // 1、拿到实体对象:按照参数顺序
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0)
            return;
        Object entity = args[0];

        // 2、准备数据
        LocalDateTime now = LocalDateTime.now();
        Long id = BaseContext.getCurrentId();

        // 3、根据不同操作类型，通过反射拿到实体对象对应属性的setter
        if (operationType == OperationType.INSERT) {
            try {
                //拿到方法
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                // 调用方法，进行赋值
                setCreateTime.invoke(entity, now);
                setUpdateTime.invoke(entity, now);
                setCreateUser.invoke(entity, id);
                setUpdateUser.invoke(entity, id);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (operationType == OperationType.UPDATE) {
            try {
                //拿到方法
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                // 调用方法，进行赋值
                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, id);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        log.info("公共字段填充完毕...");
    }
}
