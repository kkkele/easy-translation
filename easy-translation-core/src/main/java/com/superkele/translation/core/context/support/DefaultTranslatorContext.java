package com.superkele.translation.core.context.support;

import com.superkele.translation.core.TransManager;
import com.superkele.translation.core.config.DefaultTranslatorNameGenerator;
import com.superkele.translation.core.context.ConfigurableTranslatorContext;
import com.superkele.translation.core.invoker.InvokeBeanFactory;
import com.superkele.translation.core.invoker.support.ConfigurableInvokeBeanFactory;
import com.superkele.translation.core.log.TransLog;
import com.superkele.translation.core.translator.Translator;
import com.superkele.translation.core.translator.definition.TranslatorFactoryPostProcessor;
import com.superkele.translation.core.translator.definition.TranslatorPostProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 默认翻译器上下文
 */
public class DefaultTranslatorContext extends AbstractAutoLoadTranslatorContext {

    @Override
    protected Map<Integer, Class<? extends Translator>> getTranslatorClazzMap() {
        return TransManager.getConfig().getTranslatorClazzMap();
    }

    @Override
    protected DefaultTranslatorNameGenerator getTranslatorNameGenerator() {
        return TransManager.getConfig().getDefaultTranslatorNameGenerator();
    }

    @Override
    protected String[] getBasePackages() {
        return TransManager.getConfig().getTranslatorPackages();
    }

    @Override
    protected ConfigurableInvokeBeanFactory getInvokeBeanFactory() {
        return TransManager.getInvokeBeanFactory();
    }
}
