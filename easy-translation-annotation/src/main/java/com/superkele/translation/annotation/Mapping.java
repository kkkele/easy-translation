package com.superkele.translation.annotation;


import com.superkele.translation.annotation.constant.DefaultNullPointerExceptionHandler;
import com.superkele.translation.annotation.constant.TranslateTiming;

import java.lang.annotation.*;

/**
 * 用以model类中翻译字段，标记了该注解的字段在被processor解析后会被翻译。
 */
@Inherited
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Mapping {

    /**
     * 翻译器名称
     */
    String translator() default "";

    /**
     * 映射的字段
     */
    String[] mapper() default {};


    /**
     * 接收的属性内容
     */
    String receive() default "";

    /**
     * 其他字段
     */
    String[] other() default {};

    /**
     * 执行时机
     */
    TranslateTiming timing() default TranslateTiming.AFTER_RETURN;

    /**
     * 用户使用该mappingHandler字段控制映射器
     * 必须指定为mappingHandler的实现类
     * @see com.superkele.translation.core.mapping.MappingHandler
     * <ul>
     *     <li>可以使用全类名指定映射器</li>
     *     <li>可以 '@'+beanName 指定映射器，例如 @OneToOneMappingHandler </li>
     * </ul>
     */
    String mappingHandler() default "";

    /**
     * 当不为null时，是否也映射
     */
    boolean notNullMapping() default false;

    /**
     * 控制同步任务的执行顺序
     */
    int sort() default 0;

    /**
     * 是否异步执行;
     * 开启后仍然遵循sort排序，需要等待sort低的批次同步翻译字段全部执行完毕才开始翻译
     */
    boolean async() default false;

    /**
     * 在该字段翻译执行后再开始翻译，主要用于精细化控制异步翻译时的执行顺序
     * 当该字段生效时，无视sort的执行顺序
     * 该字段默认是由事件驱动进行翻译的，所以即时您将async设为false,也存在不在主线程中运行的情况
     * 这主要取决于最后触发该事件的翻译字段在哪个线程中
     */
    String[] after() default {};


    /**
     * 当翻译时，属性为空导致了空指针异常的解决方案
     *
     * @return
     */
    Class<? extends NullPointerExceptionHandler> nullPointerHandler() default DefaultNullPointerExceptionHandler.class;

}
