package com.superkele.translation.core.context.support;

import com.superkele.translation.core.config.Config;
import com.superkele.translation.core.translator.support.DefaultTranslatorDefinitionReader;

/**
 * 默认翻译器上下文
 */
public class DefaultTranslatorContext extends AbstractAutoLoadTranslatorContext {

    private Config config;

    private Object[] invokeObjs;

    private DefaultTranslatorDefinitionReader definitionReader;



    @Override
    protected DefaultTranslatorDefinitionReader getDefinitionReader() {
        return definitionReader;
    }

    public DefaultTranslatorContext setDefinitionReader(DefaultTranslatorDefinitionReader definitionReader) {
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

    public DefaultTranslatorContext setConfig(Config config) {
        this.config = config;
        return this;
    }

    public DefaultTranslatorContext setInvokeObjs(Object[] invokeObjs) {
        this.invokeObjs = invokeObjs;
        return this;
    }

}
