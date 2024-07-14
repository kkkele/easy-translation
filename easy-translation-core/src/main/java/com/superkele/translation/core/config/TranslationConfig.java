package com.superkele.translation.core.config;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.superkele.translation.core.exception.TranslationException;
import com.superkele.translation.core.log.LogLevel;
import com.superkele.translation.core.translator.*;
import com.superkele.translation.core.util.EasyTransUtil;
import com.superkele.translation.core.util.Pair;
import com.superkele.translation.core.util.ReflectUtils;

import java.io.Serializable;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * Easy-Translation 全局配置类
 * 可使用 configCustomizer配置
 *
 * @see TranslationAutoConfigurationCustomizer
 */
public class TranslationConfig implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * key: 参数长度
     * value: 被映射成的翻译器类
     */
    private final Map<Integer, Class<? extends Translator>> translatorClazzMap = new ConcurrentHashMap<>(16);
    /**
     * 是否打印日志
     */
    public boolean printLog = true;
    /**
     * 日志等级
     */
    private LogLevel logLevel = LogLevel.INFO;
    /**
     * 翻译线程池
     */
    private Executor threadPoolExecutor = null;
    /**
     * 是否开启异步翻译
     */
    private boolean asyncEnabled = false;
    /**
     * 是否打印彩色日志
     */
    private boolean colorLog = true;
    /**
     * 翻译器包
     */
    private String[] translatorPackages;
    /**
     * 域包
     */
    private String[] domainPackages;
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
    private boolean cacheEnabled = true;

    public TranslationConfig() {
        init();
    }

    @Override
    public String toString() {
        return "TranslationConfig{" +
               "printLog=" + printLog +
               ", logLevel=" + logLevel +
               ", threadPoolExecutor=" + threadPoolExecutor +
               ", asyncEnabled=" + asyncEnabled +
               ", colorLog=" + colorLog +
               ", translatorPackages=" + Arrays.toString(translatorPackages) +
               ", domainPackages=" + Arrays.toString(domainPackages) +
               ", defaultTranslatorNameGenerator=" + defaultTranslatorNameGenerator +
               ", cacheEnabled=" + cacheEnabled +
               '}';
    }

    public String[] getTranslatorPackages() {
        return translatorPackages;
    }

    public TranslationConfig setTranslatorPackages(String[] translatorPackages) {
        this.translatorPackages = translatorPackages;
        return this;
    }

    public String[] getDomainPackages() {
        return domainPackages;
    }

    public TranslationConfig setDomainPackages(String[] domainPackages) {
        this.domainPackages = domainPackages;
        return this;
    }

    public boolean isPrintLog() {
        return printLog;
    }

    public TranslationConfig setPrintLog(boolean printLog) {
        this.printLog = printLog;
        return this;
    }

    public boolean isCacheEnabled() {
        return cacheEnabled;
    }

    public TranslationConfig setCacheEnabled(boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
        return this;
    }

    public DefaultTranslatorNameGenerator getDefaultTranslatorNameGenerator() {
        return defaultTranslatorNameGenerator;
    }

    public TranslationConfig setDefaultTranslatorNameGenerator(DefaultTranslatorNameGenerator defaultTranslatorNameGenerator) {
        this.defaultTranslatorNameGenerator = defaultTranslatorNameGenerator;
        return this;
    }

    public boolean isColorLog() {
        return colorLog;
    }

    public TranslationConfig setColorLog(boolean colorLog) {
        this.colorLog = colorLog;
        return this;
    }

    public boolean isAsyncEnabled() {
        return asyncEnabled;
    }

    public TranslationConfig setAsyncEnabled(boolean asyncEnabled) {
        this.asyncEnabled = asyncEnabled;
        return this;
    }

    public Executor getThreadPoolExecutor() {
        return threadPoolExecutor;
    }

    public TranslationConfig setThreadPoolExecutor(Executor threadPoolExecutor) {
        this.threadPoolExecutor = threadPoolExecutor;
        return this;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }

    public TranslationConfig setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
        return this;
    }

    public Map<Integer, Class<? extends Translator>> getTranslatorClazzMap() {
        return translatorClazzMap;
    }

    public TranslationConfig registerTranslatorClazz(Class<? extends Translator>... translatorClazzArr) {
        for (Class<? extends Translator> translatorClazz : translatorClazzArr) {
            Pair<Method, MethodType> pair = ReflectUtils.findFunctionInterfaceMethodType(translatorClazz);
            translatorClazzMap.put(pair.getKey().getParameterCount(), translatorClazz);
        }
        return this;
    }

    public TranslationConfig addTranslatorPackage(String... extraPackage) {
        this.translatorPackages = Optional.ofNullable(this.translatorPackages)
                .map(basePackages -> {
                    if (extraPackage == null) {
                        return basePackages;
                    }
                    List<String> collect = Arrays.stream(extraPackage)
                            .collect(Collectors.toList());
                    collect.addAll(Arrays.asList(basePackages));
                    return collect.stream()
                            .distinct()
                            .toArray(String[]::new);
                })
                .orElse(extraPackage);
        return this;
    }

    public TranslationConfig addDomainPackage(String... extraPackage) {
        this.domainPackages = Optional.ofNullable(this.domainPackages)
                .map(basePackages -> {
                    if (extraPackage == null) {
                        return basePackages;
                    }
                    List<String> collect = Arrays.stream(extraPackage)
                            .collect(Collectors.toList());
                    collect.addAll(Arrays.asList(basePackages));
                    return collect.stream()
                            .distinct()
                            .toArray(String[]::new);
                })
                .orElse(extraPackage);
        return this;
    }

    protected void init() {
        registerTranslatorClazz(ContextTranslator.class, MapperTranslator.class, ConditionTranslator.class, ThreeParamTranslator.class, FourParamTranslator.class, FiveParamTranslator.class);
    }


}
