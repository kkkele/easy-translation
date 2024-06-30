package com.superkele.translation.extension.serialize.jackson;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.superkele.translation.core.config.Config;
import com.superkele.translation.core.metadata.FieldTranslationBuilder;
import com.superkele.translation.core.metadata.FieldTranslationFactory;
import com.superkele.translation.core.translator.factory.TranslatorFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TranslationJsonNodeModule extends Module {

    public static final String MODULE_NAME = TranslationJsonNodeModule.class.getSimpleName();


    private final FieldTranslationFactory fieldTranslationFactory;

    private final TranslatorFactory translatorFactory;

    private final Config config;


    @Override
    public String getModuleName() {
        return MODULE_NAME;
    }

    @Override
    public Version version() {
        return Version.unknownVersion();
    }

    @Override
    public void setupModule(SetupContext setupContext) {
        setupContext.addBeanSerializerModifier(new TranslationSerializerModifier(fieldTranslationFactory, translatorFactory, config));
    }

    @Override
    public Object getTypeId() {
        return MODULE_NAME;
    }
}
