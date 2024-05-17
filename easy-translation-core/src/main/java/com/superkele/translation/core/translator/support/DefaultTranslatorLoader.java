package com.superkele.translation.core.translator.support;

import com.superkele.translation.core.translator.definition.TranslatorLoader;
import com.superkele.translation.core.util.Pair;

import java.lang.reflect.Method;
import java.util.Set;

public class DefaultTranslatorLoader implements TranslatorLoader {
    @Override
    public Pair<Class<?>, Set<Method>> getTranslator(String location) {
        //todo
        return null;
    }
}
