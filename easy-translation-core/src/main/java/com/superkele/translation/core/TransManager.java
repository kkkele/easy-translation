package com.superkele.translation.core;

import com.superkele.translation.core.config.TranslationConfig;
import com.superkele.translation.core.context.FieldTranslationInfoContext;
import com.superkele.translation.core.context.support.DefaultTranslatorContext;
import com.superkele.translation.core.exception.TranslationException;
import com.superkele.translation.core.invoker.support.ConfigurableInvokeBeanFactory;
import com.superkele.translation.core.log.TransLog;
import com.superkele.translation.core.log.TransLogForConsole;
import com.superkele.translation.core.mapping.ParamHandler;
import com.superkele.translation.core.mapping.ParamHandlerResolver;
import com.superkele.translation.core.mapping.ResultHandler;
import com.superkele.translation.core.mapping.ResultHandlerResolver;
import com.superkele.translation.core.metadata.support.DefaultFieldTranslationInfoContext;
import com.superkele.translation.core.processor.support.DefaultTranslationProcessor;
import com.superkele.translation.core.util.EasyTransUtil;
import com.superkele.translation.core.util.Singleton;

/**
 * 管理 Easy-Translation 全局组件，可通过此类快速获取、写入各种全局组件对象
 *
 * @author superkele
 * @since 1.4.0-beta
 */
public class TransManager {

    public volatile static TransLog transLog = new TransLogForConsole();
    public volatile static TranslationConfig translationConfig;
    public volatile static ConfigurableInvokeBeanFactory invokeBeanFactory;
    public volatile static DefaultTranslatorContext translatorContext;
    public volatile static FieldTranslationInfoContext fieldTranslationInfoContext;
    public volatile static DefaultTranslationProcessor translationProcessor;
    public volatile static ParamHandlerResolver paramHandlerResolver;
    public volatile static ResultHandlerResolver resultHandlerResolver;

    public static ResultHandlerResolver getResultHandlerResolver() {
        if (resultHandlerResolver == null) {
            synchronized (TransManager.class) {
                resultHandlerResolver = resultHandlerName -> {
                    ResultHandler paramHandler = Singleton.get(resultHandlerName);
                    if (paramHandler instanceof ResultHandler) {
                        return paramHandler;
                    }
                    throw new TranslationException("请为ResultHandler提供正确的全类名。例如:com.superkele.translation.core.mapping.support.DefaultResultHandler");
                };
            }
        }
        return resultHandlerResolver;
    }

    public static void setResultHandlerResolver(ResultHandlerResolver resultHandlerResolver) {
        TransManager.resultHandlerResolver = resultHandlerResolver;
    }

    public static ParamHandlerResolver getParamHandlerResolver() {
        if (paramHandlerResolver == null) {
            synchronized (TransManager.class) {
                paramHandlerResolver = paramHandlerName -> {
                    ParamHandler paramHandler = Singleton.get(paramHandlerName);
                    if (paramHandler instanceof ParamHandler) {
                        return paramHandler;
                    }
                    throw new TranslationException("请为ParamHandler提供正确的全类名。例如:@defaultParamHandler 或者 com.superkele.translation.core.mapping.support.DefaultParamHandler");
                };
            }
        }
        return paramHandlerResolver;
    }

    public static void setParamHandlerResolver(ParamHandlerResolver paramHandlerResolver) {
        TransManager.paramHandlerResolver = paramHandlerResolver;
    }

    public static ConfigurableInvokeBeanFactory getInvokeBeanFactory() {
        if (invokeBeanFactory == null) {
            throw new TranslationException("请先调用 `TransManager.setInvokeBeanFactory(ConfigurableInvokeBeanFactory invokeBeanFactory)` 方法设置 InvokeBeanFactory");
        }
        return invokeBeanFactory;
    }


    public static void setInvokeBeanFactory(ConfigurableInvokeBeanFactory invokeBeanFactory) {
        TransManager.invokeBeanFactory = invokeBeanFactory;
    }

    public static DefaultTranslatorContext getTranslatorContext() {
        if (translatorContext == null) {
            synchronized (TransManager.class) {
                if (translatorContext == null) {
                    translatorContext = createTranslatorContext();
                }
            }
        }
        return translatorContext;
    }

    public static void setTranslatorContext(DefaultTranslatorContext translatorContext) {
        TransManager.translatorContext = translatorContext;
    }

    private static DefaultTranslatorContext createTranslatorContext() {
        return new DefaultTranslatorContext();
    }

    public static FieldTranslationInfoContext getFieldTranslationInfoContext() {
        if (fieldTranslationInfoContext == null) {
            synchronized (TransManager.class) {
                if (fieldTranslationInfoContext == null) {
                    fieldTranslationInfoContext = new DefaultFieldTranslationInfoContext();
                }
            }
        }
        return fieldTranslationInfoContext;
    }

    public static void setFieldTranslationInfoContext(FieldTranslationInfoContext fieldTranslationInfoContext) {
        TransManager.fieldTranslationInfoContext = fieldTranslationInfoContext;
    }

    public static DefaultTranslationProcessor getTranslationProcessor() {
        if (translationProcessor == null) {
            synchronized (TransManager.class) {
                if (translationProcessor == null) {
                    translationProcessor = new DefaultTranslationProcessor();
                }
            }
        }
        return translationProcessor;
    }

    public static void setTranslationProcessor(DefaultTranslationProcessor translationProcessor) {
        TransManager.translationProcessor = translationProcessor;
    }

    public static TranslationConfig getConfig() {
        if (translationConfig == null) {
            synchronized (TransManager.class) {
                if (translationConfig == null) {
                    translationConfig = createConfig();
                }
            }
        }
        return translationConfig;
    }

    public static void setConfig(TranslationConfig translationConfig) {
        TransManager.translationConfig = translationConfig;
        if (translationConfig.isPrintLog()){
            EasyTransUtil.printEasyTranslation();
        }
    }

    /**
     * 当在非Spring等环境下，用于加载默认的Config配置
     */
    public static TranslationConfig createConfig() {
        //todo 从配置文件中读
        return new TranslationConfig();
    }


    public static TransLog getTransLog() {
        return TransManager.transLog;
    }

    public static void setTransLog(TransLog transLog) {
        TransManager.transLog = transLog;
    }


}
