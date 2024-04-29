package com.superkele.translation.core.translator.handle;


/**
 * 调整用户接收的参数顺序然后将其翻译
 */
@FunctionalInterface
public interface TranslateHandler {
    Object invoke(Object... parameters);
}
