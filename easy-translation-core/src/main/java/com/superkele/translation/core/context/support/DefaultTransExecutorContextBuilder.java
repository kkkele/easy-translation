package com.superkele.translation.core.context.support;


import com.superkele.translation.core.config.Config;
import com.superkele.translation.core.translator.definition.TranslatorFactoryPostProcessor;
import com.superkele.translation.core.translator.definition.TranslatorPostProcessor;
import com.superkele.translation.core.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.superkele.translation.core.constant.TranslationConstant.DEFAULT_PACKAGE;


public class DefaultTransExecutorContextBuilder {

    private Config config = new Config();

    private List<Object> invokeObjs = new ArrayList<>();

    private List<String> packages = new ArrayList<>();

    private List<TranslatorFactoryPostProcessor> factoryPostProcessors = new ArrayList<>();

    private List<TranslatorPostProcessor> translatorPostProcessors = new ArrayList<>();

    public DefaultTransExecutorContextBuilder setConfig(Config config) {
        this.config = config;
        return this;
    }

    public DefaultTransExecutorContextBuilder setTranslatorPostProcessors(List<TranslatorPostProcessor> translatorPostProcessors) {
        Assert.notNull(translatorPostProcessors, "translatorPostProcessors must not be null");
        this.translatorPostProcessors = translatorPostProcessors;
        return this;
    }

    public DefaultTransExecutorContextBuilder setFactoryPostProcessors(List<TranslatorFactoryPostProcessor> factoryPostProcessors) {
        Assert.notNull(factoryPostProcessors, "factoryPostProcessors must not be null");
        this.factoryPostProcessors = factoryPostProcessors;
        return this;
    }

    public DefaultTransExecutorContextBuilder setInvokeObjs(Object... invokeObjs) {
        Assert.notNull(invokeObjs, "invokeObjs must not be null");
        this.invokeObjs = Arrays.asList(invokeObjs);
        return this;
    }

    public DefaultTransExecutorContextBuilder setPackages(String... packages) {
        Assert.notNull(packages, "packages must not be null");
        this.packages = Arrays.asList(packages);
        return this;
    }

    public DefaultTransExecutorContextBuilder addPackages(String... packages) {
        if (packages != null) {
            for (String packageItem : packages) {
                this.packages.add(packageItem);
            }
        }
        return this;
    }

    public DefaultTransExecutorContextBuilder addInvokeObj(Object... invokeObjs) {
        if (invokeObjs != null) {
            for (Object invokeObj : invokeObjs) {
                this.invokeObjs.add(invokeObj);
            }
        }
        return this;
    }

    public DefaultTransExecutorContextBuilder addTranslatorPostProcessor(TranslatorPostProcessor... translatorPostProcessor) {
        if (translatorPostProcessor != null) {
            for (TranslatorPostProcessor translatorPostProcessorItem : translatorPostProcessor) {
                this.translatorPostProcessors.add(translatorPostProcessorItem);
            }
        }
        return this;
    }

    public DefaultTransExecutorContextBuilder addTranslatorFactoryPostProcessor(TranslatorFactoryPostProcessor... translatorFactoryPostProcessor) {
        if (translatorFactoryPostProcessor != null) {
            for (TranslatorFactoryPostProcessor translatorFactoryPostProcessorItem : translatorFactoryPostProcessor) {
                this.factoryPostProcessors.add(translatorFactoryPostProcessorItem);
            }
        }
        return this;
    }

    public DefaultTransExecutorContext build() {
        DefaultTransExecutorContext context = new DefaultTransExecutorContext();
        context.setConfig(config);
        packages.remove(DEFAULT_PACKAGE);
        packages.add(0, DEFAULT_PACKAGE);
        context.setPackages(packages.toArray(String[]::new));
        context.setInvokeObjs(invokeObjs.toArray(Object[]::new));
        for (TranslatorFactoryPostProcessor factoryPostProcessor : factoryPostProcessors) {
            context.addTranslatorFactoryPostProcessor(factoryPostProcessor);
        }
        for (TranslatorPostProcessor translatorPostProcessor : translatorPostProcessors) {
            context.getTranslatorFactory()
                    .addTranslatorPostProcessor(translatorPostProcessor);
        }
        context.refresh();
        return context;
    }
}
