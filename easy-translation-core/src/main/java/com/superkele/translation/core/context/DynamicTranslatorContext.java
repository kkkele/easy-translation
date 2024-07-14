package com.superkele.translation.core.context;

/**
 * 动态翻译器上下文
 */
public interface DynamicTranslatorContext extends TranslatorContext {

    /**
     * 加载临时的翻译器
     */
    void loadExtract(String... path);
}
