package com.superkele.translation.core.translator.definition;

import java.util.Set;

public interface TranslatorDefinitionReader {

    TranslatorLoader getTranslatorLoader();

    Set<Class<?>> getTranslatorDeclaringClasses();

    void loadDynamicTranslatorDefinitions(Object invokeObj,TranslatorDefinitionRegistry registry);

    void loadDynamicTranslatorDefinitions(Object[] invokeObjs,TranslatorDefinitionRegistry registry);

    void loadStaticTranslatorDefinitions(TranslatorDefinitionRegistry registry);

    void loadEnumTranslatorDefinitions(TranslatorDefinitionRegistry registry);

}
