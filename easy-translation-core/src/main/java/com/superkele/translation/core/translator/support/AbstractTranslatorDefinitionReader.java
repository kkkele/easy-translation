package com.superkele.translation.core.translator.support;

import com.superkele.translation.annotation.Translation;
import com.superkele.translation.core.config.Config;
import com.superkele.translation.core.translator.definition.TranslatorDefinition;
import com.superkele.translation.core.translator.definition.TranslatorDefinitionReader;
import com.superkele.translation.core.translator.definition.TranslatorDefinitionRegistry;
import com.superkele.translation.core.translator.definition.TranslatorLoader;
import com.superkele.translation.core.util.Pair;
import com.superkele.translation.core.util.ReflectUtils;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.util.*;

public abstract class AbstractTranslatorDefinitionReader implements TranslatorDefinitionReader {


    protected final TranslatorDefinitionRegistry registry;
    private final SubTypesScanner subTypesScanner = new SubTypesScanner();
    private final TypeAnnotationsScanner typeAnnotationsScanner = new TypeAnnotationsScanner();
    private final MethodAnnotationsScanner methodAnnotationsScanner = new MethodAnnotationsScanner();
    private TranslatorLoader translatorLoader;

    protected AbstractTranslatorDefinitionReader(TranslatorDefinitionRegistry registry, TranslatorLoader translatorLoader) {
        this.registry = registry;
        this.translatorLoader = translatorLoader;
    }

    protected AbstractTranslatorDefinitionReader(TranslatorDefinitionRegistry registry) {
        this(registry, new DefaultTranslatorLoader());
    }

    @Override
    public TranslatorDefinitionRegistry getRegistry() {
        return registry;
    }

    @Override
    public TranslatorLoader getTranslatorLoader() {
        return this.translatorLoader;
    }

    @Override
    public void loadDynamicTranslatorDefinitions(Object invokeObj) {
        Optional.ofNullable(invokeObj)
                .ifPresent(obj -> {
                    Method[] declaredMethods = obj.getClass()
                            .getDeclaredMethods();
                    Arrays.stream(declaredMethods)
                            .filter(method -> !ReflectUtils.isStaticMethod(method) && !ReflectUtils.isAbstractMethod(method))
                            .map(method -> {
                                AnnotationUtils.getAnnotation(method, Translation.class);
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
    public void loadDynamicTranslatorDefinitions(Object[] invokeObjs) {
        Optional.ofNullable(invokeObjs)
                .ifPresent(invokeObjArr -> {
                    for (Object invokeObj : invokeObjArr) {
                        loadDynamicTranslatorDefinitions(invokeObj);
                    }
                });
    }

    @Override
    public void loadTranslatorDefinitions(String location) {
        Map<Class<?>, Set<Method>> translatorMap = getTranslatorLoader()
                .getTranslator(location);

    }

    public void loadTranslatorDefinitions(String[] locations) {

    }

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
                            .filter(ReflectUtils::isStaticMethod)
                            .map(method -> {
                                Translation mergedAnnotation = AnnotatedElementUtils.getMergedAnnotation(method, Translation.class);
                                return Pair.of(method, mergedAnnotation);
                            })
                            .forEach(pair ->
                                    registry.register(getTranslatorName(pair.getValue(), pair.getKey()), convertStaticMethodToTranslatorDefinition(pair.getKey())));
                });
    }

    public void loadStaticTranslatorDefinitions(String[] locations) {
        Optional.ofNullable(locations)
                .ifPresent(packageUrls -> {
                    for (String packageUrl : packageUrls) {
                        loadStaticTranslatorDefinitions(packageUrl);
                    }
                });
    }

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
            translatorName = getDefaultTranslatorName(clazz);
        }
        return translatorName;
    }


    protected abstract String getDefaultTranslatorName(Method method);

    protected abstract String getDefaultTranslatorName(Class<?> clazz);


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
