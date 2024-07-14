package com.superkele.translation.core.metadata.support;

import com.superkele.translation.core.TransManager;
import com.superkele.translation.core.context.ConfigurableFieldTranslationInfoContext;
import com.superkele.translation.core.context.ConfigurableTranslatorContext;
import com.superkele.translation.core.log.TransLog;
import com.superkele.translation.core.metadata.FieldTranslationInfo;

import java.util.function.Consumer;

public class DefaultFieldTranslationInfoContext implements ConfigurableFieldTranslationInfoContext {

    private final ConfigurableTranslatorContext configurableTranslatorContext;
    protected DefaultFieldTranslationInfoFactory fieldTranslationFactory;

    public DefaultFieldTranslationInfoContext(ConfigurableTranslatorContext configurableTranslatorContext) {
        this.configurableTranslatorContext = configurableTranslatorContext;
        configurableTranslatorContext.addListener(context -> refresh());
    }

    public DefaultFieldTranslationInfoContext() {
        this(TransManager.getTranslatorContext());
    }

    @Override
    public void refresh() {
        DefaultMappingFiledTranslationBuilder builder = new DefaultMappingFiledTranslationBuilder(configurableTranslatorContext, TransManager.getParamHandlerResolver(), TransManager.getResultHandlerResolver());
        this.fieldTranslationFactory = new DefaultFieldTranslationInfoFactory(builder);
        DefaultFiledTranslationReader defaultFiledTranslationReader = new DefaultFiledTranslationReader(this.fieldTranslationFactory, builder);
        defaultFiledTranslationReader.load(TransManager.getConfig().getDomainPackages());
        TransLog transLog = TransManager.getTransLog();
        transLog.info("FieldTranslationInfoContext load successfully");
    }

    @Override
    public void notice() {

    }

    @Override
    public void addListener(Consumer action) {

    }

    @Override
    public FieldTranslationInfo get(Class<?> clazz, boolean isJsonSerialize) {
        return fieldTranslationFactory.get(clazz, isJsonSerialize);
    }
}
