package com.superkele.translation.core.annotation;

/**
 * 字段翻译器
 */
public interface FieldTranslationInvoker {

    Object invoke(Object translationTarget);
}
