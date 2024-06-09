package com.superkele.translation.core.translator.definition;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

public interface TranslatorLoader {

    Map<Class<?>,Set<Method>> getTranslator(String location);
}
