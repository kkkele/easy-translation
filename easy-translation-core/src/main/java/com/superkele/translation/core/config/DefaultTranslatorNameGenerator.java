package com.superkele.translation.core.config;

import java.lang.reflect.Method;

@FunctionalInterface
public interface DefaultTranslatorNameGenerator {
    String genName(Class<?> clazz, Method method);
}
