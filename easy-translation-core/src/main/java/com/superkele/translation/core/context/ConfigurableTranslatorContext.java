package com.superkele.translation.core.context;

/**
 * 可配置的翻译器上下文
 */
public interface ConfigurableTranslatorContext extends TranslatorContext {
    /**
     * 刷新容器
     */
    void refresh();
}
