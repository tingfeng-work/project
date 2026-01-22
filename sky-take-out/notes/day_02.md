# 公共字段填充

在系统中的很多表中，都需要维护实体对象的公共字段，例如创建时间、修改时间、创建人、修改人等字段。将这一部分代码通过 AOP 抽象出来，进行面向切面编程，完成公共字段的自动填充，能让业务代码更简洁、清晰。

## 实现思路

涉及的字段以及操作类型如下：

| **序号** | **字段名**  | **含义** | **数据类型** | **操作类型**   |
| -------- | ----------- | -------- | ------------ | -------------- |
| 1        | create_time | 创建时间 | datetime     | insert         |
| 2        | create_user | 创建人id | bigint       | insert         |
| 3        | update_time | 修改时间 | datetime     | insert、update |
| 4        | update_user | 修改人id | bigint       | insert、update |

实现步骤：

1). 自定义注解 AutoFill，用于标识需要进行公共字段自动填充的方法，用来区分该方法的操作类型（属于插入还是更新）

2). 自定义切面类 AutoFillAspect，统一拦截加入了 AutoFill 注解的方法，通过反射为公共字段赋值

3). 在 Mapper 的方法上加入 AutoFill 注解

## 实现

1. 自定义注解 AutoFill

```java
/*
 * 自定义注解，用来标识方法需要进行哪种功能字段自动填充
 * */
@Target(ElementType.METHOD) //标注该注解用在方法上
@Retention(RetentionPolicy.RUNTIME) //标注该注解生效在运行时
public @interface AutoFill {
    // 枚举数据库操作类型：插入、新增
    OperationType value(); // 抽象到通用模块下的枚举包，值有插入和更新
}
```

2. 自定义切面类 AutoFillAspect

* 定义切人点：为 mapper 包下的加了 AutoFill 注解的所有方法
* 分析公共字段的注入应该在方法执行前，将公共字段注入实体对象，所以采用前置通知
* 通过 Spring 容器自动注入连接点（需要用注解 @Aspect 标注当前类是切面类）
* 获取到当前被拦截方法上的数据库操作类型（获取当前拦截方法签名对象 => 获取注解对象 => 调用方法获得操作类型）
* 通过反射为公共字段赋值（获取参数 => 拿到实体对象 => 根据插入或更新操作，通过反射拿到对应属性的 setter 方法 => 调用对应方法进行赋值）

```java
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
```

3. 在对应 Mapper 接口的方法上加入 AutoFill 注解

---------------

# 文件上传

TODO 需要补充

# 菜品管理模块

TODO 需要补充

这个模块由于涉及多表操作需要注意：

* 对应的 DTO 以及 VO 的设计
* 事务控制
* 删除菜品由于涉及多表，删除前需要检查逻辑外键
* 修改菜品时，对于口味的修改简化为了删除，再插入

