package com.superkele.translation.core.context;

import java.util.function.Consumer;

/**
 * 可配置的翻译器上下文
 */
public interface ConfigurableTranslatorContext extends TranslatorContext {
    /**
     * 刷新容器
     */
    void refresh();

    /**
     * 注册事件
     */
    void register(Consumer<ConfigurableTranslatorContext> consumer);
}
