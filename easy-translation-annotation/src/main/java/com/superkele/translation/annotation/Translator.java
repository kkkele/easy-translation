package com.superkele.translation.annotation;


import java.lang.annotation.*;

/**
 *  <p>
 *      标注在方法上，使其成为一个翻译器。
 *      需要传递的两个参数value和other,
 *      必须是确定的常量，这样才能确保映射的字段顺利找到翻译器
 *  </p>
 *  <p>
 *      annotate the method to make it a translator. The two parameters that need to be passed,
 *      'value' and 'other', must be definite constants,
 *      so as to ensure that the mapped field successfully finds the translator
 *  </p>
 */
@Inherited
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Translator {

    /**
     * <p>转换器名称，当value为空时，默认赋值为方法名</p>
     * <p>translator name</p>
     * @return
     */
    String value() default "";

    /**
     * <p>其他的补充条件</p>
     *
     * <p>Other complementary conditions</p>
     */
    String other() default "";


    /**
     * <p>是否为默认翻译器</p>
     * <p>Whether it is the default translator</p>
     */
    boolean isDefault() default true;
}
