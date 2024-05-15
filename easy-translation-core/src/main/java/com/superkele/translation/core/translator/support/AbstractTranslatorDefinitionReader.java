package com.superkele.translation.core.translator.support;

import com.superkele.translation.annotation.Translation;
import com.superkele.translation.core.config.Config;
import com.superkele.translation.core.translator.definition.TranslatorDefinition;
import com.superkele.translation.core.translator.definition.TranslatorDefinitionReader;
import com.superkele.translation.core.translator.definition.TranslatorDefinitionRegistry;
import com.superkele.translation.core.util.Pair;
import com.superkele.translation.core.util.MethodUtils;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

public abstract class AbstractTranslatorDefinitionReader implements TranslatorDefinitionReader {


    protected final TranslatorDefinitionRegistry registry;
    protected final Config config;
    private final SubTypesScanner subTypesScanner = new SubTypesScanner();
    private final TypeAnnotationsScanner typeAnnotationsScanner = new TypeAnnotationsScanner();
    private final MethodAnnotationsScanner methodAnnotationsScanner = new MethodAnnotationsScanner();

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
                            .filter(method -> !MethodUtils.isStaticMethod(method) && !MethodUtils.isAbstractMethod(method))
                            .map(method -> {
                                Translation mergedAnnotation = AnnotatedElementUtils.getMergedAnnotation(method, Translation.class);
                                return Pair.of(method, mergedAnnotation);
                            })
                            .filter(pair -> pair.getValue() != null)
                            .forEach(pair -> {
                                TranslatorDefinition translatorDefinition = convertDynamicMethodToTranslatorDefinition(obj, pair.getKey());
                                registry.register(getTranslatorName(pair.getValue(), pair.getKey()), translatorDefinition);
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
                                    .setScanners(methodAnnotationsScanner)
                    );
                    Set<Method> methods = reflections.getMethodsAnnotatedWith(Translation.class);
                    methods.stream()
                            .filter(MethodUtils::isStaticMethod)
                            .map(method -> {
                                Translation mergedAnnotation = AnnotatedElementUtils.getMergedAnnotation(method, Translation.class);
                                return Pair.of(method, mergedAnnotation);
                            })
                            .forEach(pair ->
                                    registry.register(getTranslatorName(pair.getValue(), pair.getKey()), convertStaticMethodToTranslatorDefinition(pair.getKey())));
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
                    Reflections reflections = new Reflections(location, typeAnnotationsScanner, subTypesScanner);
                    reflections.getTypesAnnotatedWith(Translation.class)
                            .stream()
                            .filter(clazz -> clazz.isEnum())
                            .map(clazz -> Pair.of(clazz, AnnotatedElementUtils.getMergedAnnotation(clazz, Translation.class)))
                            .forEach(pair -> {
                                Class<? extends Enum> enumClazz = (Class<? extends Enum>) pair.getKey();
                                registry.register(getTranslatorName(pair.getValue(), enumClazz), convertEnumToTranslatorDefinition(enumClazz));
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

    protected String getTranslatorName(Translation translation, Method method) {
        String translatorName = translation.name();
        if (StringUtils.isBlank(translatorName)) {
            translatorName = getDefaultTranslatorName(method);
        }
        return translatorName;
    }

    protected String getTranslatorName(Translation translation, Class<? extends Enum> clazz) {
        String translatorName = translation.name();
        if (StringUtils.isBlank(translatorName)) {
            translatorName = config.getBeanNameGetter().getDeclaringBeanName(clazz);
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
