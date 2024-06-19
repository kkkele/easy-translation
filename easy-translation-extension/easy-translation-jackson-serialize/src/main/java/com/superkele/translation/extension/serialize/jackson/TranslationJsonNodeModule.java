package com.superkele.translation.extension.serialize.jackson;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;

@RequiredArgsConstructor
public class TranslationJsonNodeModule extends Module {

    public static final String MODULE_NAME = TranslationJsonNodeModule.class.getSimpleName();

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
        setupContext.addBeanSerializerModifier(new TranslationSerializerModifier());
    }

    @Override
    public Object getTypeId() {
        return MODULE_NAME;
    }
}
