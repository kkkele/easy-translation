package com.superkele.translation.core.translator.handle;


/**
 * 调整用户接收的参数顺序
 */
@FunctionalInterface
public interface TranslateHandler {
    Object invoke(Object... parameters);
}
