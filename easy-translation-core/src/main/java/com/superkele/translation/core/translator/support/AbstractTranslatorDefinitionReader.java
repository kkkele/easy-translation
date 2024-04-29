package com.superkele.translation.core.translator.support;

import com.superkele.translation.annotation.Translation;
import com.superkele.translation.core.config.Config;
import com.superkele.translation.core.translator.definition.TranslatorDefinition;
import com.superkele.translation.core.translator.definition.TranslatorDefinitionReader;
import com.superkele.translation.core.translator.definition.TranslatorDefinitionRegistry;
import com.superkele.translation.core.util.Assert;
import com.superkele.translation.core.util.ReflectUtils;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

public abstract class AbstractTranslatorDefinitionReader implements TranslatorDefinitionReader {

    private final TranslatorDefinitionRegistry registry;
    private final Config config;

    protected AbstractTranslatorDefinitionReader(TranslatorDefinitionRegistry registry, Config config) {
        this.registry = registry;
        this.config = config;
    }

    protected AbstractTranslatorDefinitionReader(TranslatorDefinitionRegistry registry) {
        this(registry, new Config());
    }

    @Override
    public TranslatorDefinitionRegistry getRegistry() {
        return registry;
    }

    @Override
    public void loadVirtualTranslatorDefinitions(Object invokeObj) {
        Optional.ofNullable(invokeObj)
                .ifPresent(obj -> {
                    Method[] declaredMethods = obj.getClass()
                            .getDeclaredMethods();
                    Arrays.stream(declaredMethods)
                            .filter(method -> method.isAnnotationPresent(Translation.class))
                            .filter(method -> !ReflectUtils.isStaticMethod(method) && !ReflectUtils.isAbstractMethod(method))
                            .forEach(method -> {
                                TranslatorDefinition translatorDefinition = convertDynamicMethodToTranslatorDefinition(obj, method);
                                registry.register(getTranslatorName(method), translatorDefinition);
                            });
                });
    }

    @Override
    public void loadVirtualTranslatorDefinitions(Object[] invokeObjs) {
        Optional.ofNullable(invokeObjs)
                .ifPresent(invokeObjArr -> {
                    for (Object invokeObj : invokeObjArr) {
                        loadVirtualTranslatorDefinitions(invokeObj);
                    }
                });
    }

    @Override
    public void loadStaticTranslatorDefinitions(String location) {
        Optional.ofNullable(location)
                .ifPresent(packageName -> {
                    // 配置Reflections来扫描子类型和方法注解，尽管这里主要关注接口，但这样配置可以保持灵活性
                    Reflections reflections = new Reflections(
                            new ConfigurationBuilder()
                                    .forPackages(packageName)
                                    .setScanners(new MethodAnnotationsScanner())
                    );
                    Set<Method> methods = reflections.getMethodsAnnotatedWith(Translation.class);
                    methods.stream()
                            .filter(ReflectUtils::isStaticMethod)
                            .forEach(method ->
                                    registry.register(getTranslatorName(method), convertStaticMethodToTranslatorDefinition(method)));
                });
    }

    @Override
    public void loadStaticTranslatorDefinitions(String[] locations) {
        Optional.ofNullable(locations)
                .ifPresent(packageUrls -> {
                    for (String packageUrl : packageUrls) {
                        loadStaticTranslatorDefinitions(packageUrl);
                    }
                });
    }

    @Override
    public void loadEnumTranslatorDefinitions(String location) {
        Optional.ofNullable(location)
                .ifPresent(packageUrl -> {
                    Reflections reflections = new Reflections(location, new SubTypesScanner());
                    reflections.getSubTypesOf(Enum.class)
                            .stream()
                            .filter(clazz -> clazz.isAnnotationPresent(Translation.class))
                            .forEach(clazz -> {
                                Translation translation = clazz.getAnnotation(Translation.class);
                                TranslatorDefinition definition = convertEnumToTranslatorDefinition(clazz);
                                registry.register(translation.name(), definition);
                            });
                });
    }

    @Override
    public void loadEnumTranslatorDefinitions(String[] locations) {
        Optional.ofNullable(locations)
                .ifPresent(packageUrls -> {
                    for (String packageUrl : packageUrls) {
                        loadEnumTranslatorDefinitions(packageUrl);
                    }
                });
    }

    protected String getTranslatorName(Method method) {
        Translation translation = method.getAnnotation(Translation.class);
        String translatorName = translation.name();
        if (StringUtils.isBlank(translatorName)) {
            translatorName = getDefaultTranslatorName(method);
        }
        return translatorName;
    }

    protected String getDefaultTranslatorName(Method method) {
        String beanName = config.getBeanNameGetter().getDeclaringBeanName(method.getDeclaringClass());
        return config.getDefaultTranslatorNameGenerator().genName(beanName, method.getName());
    }

    /**
     * 将枚举类转化成TranslatorDefinition
     *
     * @param enumClass
     * @return
     */
    protected abstract TranslatorDefinition convertEnumToTranslatorDefinition(Class<? extends Enum> enumClass);

    /**
     * 将类中的静态方法转为TranslatorDefinition
     *
     * @param method 静态方法
     * @return
     */
    protected abstract TranslatorDefinition convertStaticMethodToTranslatorDefinition(Method method);

    /**
     * 将对象中的方法转为TranslatorDefinition
     *
     * @param invokeObj
     * @return
     */
    protected abstract TranslatorDefinition convertDynamicMethodToTranslatorDefinition(Object invokeObj, Method method);
}
