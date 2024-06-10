package com.superkele.translation.core.translator.definition;

import com.superkele.translation.annotation.constant.InvokeBeanScope;
import com.superkele.translation.core.decorator.TranslatorDecorator;
import com.superkele.translation.core.invoker.enums.TranslatorType;
import com.superkele.translation.core.translator.Translator;
import lombok.Data;

import java.lang.invoke.MethodHandle;

@Data
public class TranslatorDefinition {

    /**
     * 提供invokeObj的Bean类型
     */
    private TranslatorType translatorType;

    /**
     * 默认单例scope
     */
    private InvokeBeanScope scope = InvokeBeanScope.SINGLETON;

    /**
     * 翻译器方法返回值类型
     */
    private Class<?> returnType;

    /**
     * 翻译器方法参数类型
     */
    private Class<?>[] parameterTypes;

    /**
     * 翻译器的类型
     */
    private Class<? extends Translator> translatorClass;

    /**
     * 提供翻译器的Bean名称
     */
    private String invokeBeanName;

    /**
     * 提供翻译器的Bean 类型
     */
    private Class<?> invokeBeanClazz;

    /**
     * 翻译器修饰器
     */
    private TranslatorDecorator translateDecorator;

    /**
     * 方法句柄
     */
    private MethodHandle methodHandle;

    /**
     * 需要映射的参数位置
     */
    private int[] mapperIndex;

}
