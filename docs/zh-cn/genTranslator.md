# 生成翻译器

## 核心注解

所有的注册成为翻译器的方法或者枚举都要使用`@Translation`**注解或标记了该注解的组合注解**

```java
/**
 * 用来标记成为翻译器的静态方法，动态方法，枚举类
 */
@Inherited
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Translation {

    /**
     * <p>转换器名称</p>
     * <p>translator name</p>
     */
    String name() default "";

}

```

如果您使用的是<font size=4>**spring-boot**</font>版本，那么，框架已经为您提供了一个组合注解`@Translator`，来省略掉使用注解时还要拼写name的麻烦。

您也可以自定义其他的组合注解，只要您标记了`@Translation`即可。

## 对象动态翻译器

## 静态翻译器

## 枚举翻译器

