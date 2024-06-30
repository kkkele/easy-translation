package com.superkele.translation.extension.serialize.jackson;

import com.superkele.translation.annotation.Mapper;
import com.superkele.translation.annotation.Mapping;
import com.superkele.translation.annotation.NullPointerExceptionHandler;
import com.superkele.translation.annotation.constant.DefaultNullPointerExceptionHandler;
import com.superkele.translation.annotation.constant.MappingStrategy;
import com.superkele.translation.annotation.constant.TranslateTiming;

import java.lang.annotation.*;

@Inherited
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Mapping(timing = TranslateTiming.JSON_SERIALIZE)
public @interface JsonMapping {

    /**
     * 翻译器名称
     */
    String translator() default "";

    /**
     * 接收的属性内容
     */
    String receive() default "";

    /**
     * 辅助条件，将值修改成对应类型后直接传递给翻译器
     */
    String[] other() default {};

    /**
     * 映射策略，是单个处理还是批量处理
     */
    MappingStrategy strategy() default MappingStrategy.SINGLE;

    /**
     * 替代原先的mapper字段
     * 后续将逐步替换掉mapper字段的使用
     * @since 1.4.0
     * @return
     */
    Mapper[] mappers() default {};

    /**
     * <p>分组依据
     * <p>辅助结果处理器进行结果筛选
     * <p>具体用法，可以查考默认结果处理器
     * @see com.superkele.translation.core.mapping.ResultHandler
     * @see com.superkele.translation.core.mapping.support.DefaultResultHandler
     */
    String[] groupKey() default {};

    /**
     * 结果处理器
     * @see com.superkele.translation.core.mapping.ResultHandler
     */
    String resultHandler() default "";

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
     * 该字段会使得field在前置事件执行完然后回调进行翻译，所以即时您将async设为false,也存在不在主线程中运行的情况
     * 这主要取决于最后触发该事件的翻译字段在哪个线程中
     */
    String[] after() default {};


    /**
     * 当翻译时，属性为空导致了空指针异常的解决方案
     */
    Class<? extends NullPointerExceptionHandler> nullPointerHandler() default DefaultNullPointerExceptionHandler.class;
}