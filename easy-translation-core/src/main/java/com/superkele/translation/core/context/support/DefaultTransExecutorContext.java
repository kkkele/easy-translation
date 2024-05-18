package com.superkele.translation.core.context.support;

import com.superkele.translation.core.config.Config;
import com.superkele.translation.core.translator.support.DefaultTranslatorDefinitionReader;

import java.util.Optional;

import static com.superkele.translation.core.constant.TranslationConstant.DEFAULT_PACKAGE;

/**
 * 默认翻译器上下文
 */
public class DefaultTransExecutorContext extends AbstractAutoLoadTransExecutorContext {

    private Config config;

    private Object[] invokeObjs;

    private DefaultTranslatorDefinitionReader definitionReader;



    @Override
    protected DefaultTranslatorDefinitionReader getDefinitionReader() {
        return definitionReader;
    }

    public DefaultTransExecutorContext setDefinitionReader(DefaultTranslatorDefinitionReader definitionReader) {
        this.definitionReader = definitionReader;
        return this;
    }

    @Override
    public Object[] getRegisterObjs() {
        return this.invokeObjs;
    }

    @Override
    public Config getConfig() {
        return this.config;
    }

    public DefaultTransExecutorContext setConfig(Config config) {
        this.config = config;
        return this;
    }

    public DefaultTransExecutorContext setInvokeObjs(Object[] invokeObjs) {
        this.invokeObjs = invokeObjs;
        return this;
    }

}
