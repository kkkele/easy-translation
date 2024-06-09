package com.superkele.translation.core.translator.definition;

import com.superkele.translation.core.translator.Resource;

import java.util.Set;

public interface TranslatorDefinitionReader {

    TranslatorDefinitionRegistry getRegistry();

    void loadTranslatorDefinitions(String basePath);

    void loadTranslatorDefinitions(String[] basePath);

    void loadTranslatorDefinitions(Resource resource);
}
