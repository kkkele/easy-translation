package com.superkele.translation.core.context.support;

import com.superkele.translation.core.TransManager;
import com.superkele.translation.core.context.DynamicTranslatorContext;
import com.superkele.translation.core.log.TransLog;
import com.superkele.translation.core.translator.definition.ConfigurableTranslatorDefinitionFactory;


/**
 * 抽象动态翻译器上下文
 */
public abstract class AbstractDynamicTranslatorContext extends AbstractTranslatorContext implements DynamicTranslatorContext {

    @Override
    public void loadExtract(String... path) {
        //判断是否已经调用过refresh方法，如果还没有调用，则使用监听器等待refresh方法的调用
        ConfigurableTranslatorDefinitionFactory translatorFactory = getTranslatorFactory();
        if (null == translatorFactory) {
            addListener(factory -> loadPath(getTranslatorFactory(), path));
        } else {
            loadPath(translatorFactory, path);
        }
    }

    protected void loadPath(ConfigurableTranslatorDefinitionFactory translatorFactory, String... path) {
        ConfigurableTranslatorDefinitionFactory extractFactory = createTempTranslatorDefinitionFactory(path);
        //在translator正式转载前，对translatorFactory进行一些初始化操作
        invokeTranslatorFactoryPostProcessors(extractFactory);
        //装载translatorPostProcessor
        loadTranslatorPostProcessors(extractFactory);
        //合并到主分支
        mergeToMain(extractFactory);
        //再次实例化translator
        ConfigurableTranslatorDefinitionFactory mainFactory = translatorFactory;
        loadTranslators(extractFactory.getTranslatorNames(), mainFactory);
        //打印日志
        TransLog transLog = TransManager.getTransLog();
        transLog.info("TranslatorContext load successfully");
    }

    public abstract ConfigurableTranslatorDefinitionFactory createTempTranslatorDefinitionFactory(String... path);

    public abstract void mergeToMain(ConfigurableTranslatorDefinitionFactory translatorFactory);

}
