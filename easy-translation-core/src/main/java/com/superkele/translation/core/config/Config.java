package com.superkele.translation.core.config;

import cn.hutool.core.util.StrUtil;
import com.superkele.translation.core.exception.TranslationException;
import com.superkele.translation.core.translator.*;
import com.superkele.translation.core.util.Pair;
import com.superkele.translation.core.util.ReflectUtils;

import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

/**
 * Easy-Translation 全局配置类
 */
public class Config {
    /**
     * key: 参数长度
     * value: 被映射成的翻译器类
     */
    private Map<Integer, Class<? extends Translator>> translatorClazzMap = new ConcurrentHashMap<>(16);
    /**
     * 翻译线程池
     */
    private Executor threadPoolExecutor = null;
    /**
     * 是否开启异步翻译
     */
    private Supplier<Boolean> asyncEnabled = () -> this.threadPoolExecutor != null;
    /**
     * 默认翻译器名称生成器
     */
    private DefaultTranslatorNameGenerator defaultTranslatorNameGenerator = (clazz, methodName) -> {
        String beanName = Optional.of(clazz)
                .map(Class::getSimpleName)
                .map(str -> StrUtil.lowerFirst(str))
                .orElseThrow(() -> new TranslationException("DefaultTranslatorNameGenerator生成名字失败"));
        return Optional.ofNullable(methodName)
                .map(str -> beanName + "." + str)
                .orElse(beanName);
    };
    /**
     * 是否开启事务翻译缓存
     */
    private Supplier<Boolean> cacheEnabled = () -> true;

    public Map<Integer, Class<? extends Translator>> getTranslatorClazzMap() {
        return translatorClazzMap;
    }

    public Executor getThreadPoolExecutor() {
        return threadPoolExecutor;
    }

    public Config setThreadPoolExecutor(Executor threadPoolExecutor) {
        this.threadPoolExecutor = threadPoolExecutor;
        return this;
    }

    public Supplier<Boolean> getAsyncEnabled() {
        return asyncEnabled;
    }

    public Config setAsyncEnabled(Supplier<Boolean> asyncEnabled) {
        this.asyncEnabled = asyncEnabled;
        return this;
    }

    public DefaultTranslatorNameGenerator getDefaultTranslatorNameGenerator() {
        return defaultTranslatorNameGenerator;
    }

    public Config setDefaultTranslatorNameGenerator(DefaultTranslatorNameGenerator defaultTranslatorNameGenerator) {
        this.defaultTranslatorNameGenerator = defaultTranslatorNameGenerator;
        return this;
    }

    public Supplier<Boolean> getCacheEnabled() {
        return cacheEnabled;
    }

    public Config setCacheEnabled(Supplier<Boolean> cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
        return this;
    }

    public Config registerTranslatorClazz(Class<? extends Translator>... translatorClazzArr) {
        for (Class<? extends Translator> translatorClazz : translatorClazzArr) {
            Pair<Method, MethodType> pair = ReflectUtils.findFunctionInterfaceMethodType(translatorClazz);
            translatorClazzMap.put(pair.getKey().getParameterCount(), translatorClazz);
        }
        return this;
    }

    public Config() {
        init();
    }

    protected void init() {
        registerTranslatorClazz(ContextTranslator.class, MapperTranslator.class, ConditionTranslator.class, ThreeParamTranslator.class, FourParamTranslator.class, FiveParamTranslator.class);
    }

}
