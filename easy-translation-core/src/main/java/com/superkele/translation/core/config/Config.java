package com.superkele.translation.core.config;

import cn.hutool.core.util.StrUtil;
import com.superkele.translation.core.translator.ConditionTranslator;
import com.superkele.translation.core.translator.ContextTranslator;
import com.superkele.translation.core.translator.MapperTranslator;
import com.superkele.translation.core.translator.Translator;
import com.superkele.translation.core.util.Pair;
import com.superkele.translation.core.util.ReflectUtils;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * Easy-Translation 全局配置类
 */
@Getter
public class Config {

    public static final Config INSTANCE = new Config();

    /**
     * key: 参数长度
     * value: 被映射成的翻译器类
     */
    private Map<Integer, Class<? extends Translator>> translatorClazzMap = new ConcurrentHashMap<>(16);

    private BeanNameGetter beanNameGetter = clazz -> StrUtil.lowerFirst(clazz.getSimpleName());

    private ExecutorService threadPoolExecutor;

    private volatile long timeout = 3000; //ms

    private DefaultTranslatorNameGenerator defaultTranslatorNameGenerator = (clazzName, methodName) -> StringUtils.join(clazzName, ".", methodName);

    private Config() {
        init();
    }

    public Config setTimeout(long timeout) {
        this.timeout = timeout;
        return this;
    }

    public Config setBeanNameGetter(BeanNameGetter beanNameGetter) {
        this.beanNameGetter = beanNameGetter;
        return this;
    }

    public Config setDefaultTranslatorNameGenerator(DefaultTranslatorNameGenerator defaultTranslatorNameGenerator) {
        this.defaultTranslatorNameGenerator = defaultTranslatorNameGenerator;
        return this;
    }

    /**
     * 仅支持函数式接口
     *
     * @param translatorClazz
     * @return
     */
    public Config registerTranslatorClazz(Class<? extends Translator> translatorClazz) {
        Pair<Method, MethodType> pair = ReflectUtils.findFunctionInterfaceMethodType(translatorClazz);
        translatorClazzMap.put(pair.getKey().getParameterCount(), translatorClazz);
        return this;
    }

    public Config registerTranslatorClazz(Class<? extends Translator>... translatorClazzArr) {
        for (Class<? extends Translator> translatorClazz : translatorClazzArr) {
            Pair<Method, MethodType> pair = ReflectUtils.findFunctionInterfaceMethodType(translatorClazz);
            translatorClazzMap.put(pair.getKey().getParameterCount(), translatorClazz);
        }
        return this;
    }

    protected void init() {
        registerTranslatorClazz(ContextTranslator.class, MapperTranslator.class, ConditionTranslator.class);
    }

    public ExecutorService getThreadPoolExecutor() {
        return threadPoolExecutor;
    }

    public Config setThreadPoolExecutor(ExecutorService threadPoolExecutor) {
        this.threadPoolExecutor = threadPoolExecutor;
        return this;
    }

    @FunctionalInterface
    public interface BeanNameGetter {

        String getDeclaringBeanName(Class clazz);

    }

    @FunctionalInterface
    public interface DefaultTranslatorNameGenerator {
        String genName(String beanName, String methodName);
    }


}
