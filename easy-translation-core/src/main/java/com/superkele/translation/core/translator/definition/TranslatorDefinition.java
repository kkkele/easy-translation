package com.superkele.translation.core.translator.definition;

import com.superkele.translation.core.translator.Translator;
import com.superkele.translation.core.translator.handle.TranslateHandler;
import lombok.Data;

@Data
public class TranslatorDefinition {

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
     * 该对象提供的翻译器
     */
    private Object invokeObj;

    /**
     * 翻译器
     */
    private Translator translator;

    /**
     * 翻译器处理器
     */
    private TranslateHandler translateHandler;

    /**
     * 需要映射的参数位置
     */
    private int[] mapperIndex;

    /**
     * 不需要映射的参数位置
     */
    private int[] otherIndex;

}