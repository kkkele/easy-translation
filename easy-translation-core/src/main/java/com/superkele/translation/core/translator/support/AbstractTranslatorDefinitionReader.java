package com.superkele.translation.core.translator.support;

import com.superkele.translation.annotation.Translation;
import com.superkele.translation.core.translator.definition.TranslatorDefinition;
import com.superkele.translation.core.translator.definition.TranslatorDefinitionReader;
import com.superkele.translation.core.translator.definition.TranslatorDefinitionRegistry;
import com.superkele.translation.core.translator.definition.TranslatorLoader;
import com.superkele.translation.core.util.Pair;
import com.superkele.translation.core.util.ReflectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.util.*;

public abstract class AbstractTranslatorDefinitionReader implements TranslatorDefinitionReader {


    protected final TranslatorDefinitionRegistry registry;
    private TranslatorLoader translatorLoader;
    private Map<Class<?>, Set<Method>> translatorMap = new HashMap<>();

    protected AbstractTranslatorDefinitionReader(TranslatorDefinitionRegistry registry, TranslatorLoader translatorLoader, String[] locations) {
        this.registry = registry;
        this.translatorLoader = translatorLoader;
        for (String location : locations) {
            Map<Class<?>, Set<Method>> translator = this.translatorLoader.getTranslator(location);
            this.translatorMap.putAll(translator);
        }
    }

    protected AbstractTranslatorDefinitionReader(TranslatorDefinitionRegistry registry, String... locations) {
        this(registry, new DefaultTranslatorLoader(), locations);
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
                .filter(obj -> translatorMap.containsKey(obj.getClass()))
                .ifPresent(obj -> {
                    Set<Method> methods = translatorMap.get(obj.getClass());
                    methods.stream()
                            .filter(method -> !ReflectUtils.isStaticMethod(method) && !ReflectUtils.isAbstractMethod(method))
                            .map(method -> {
                                Translation mergedAnnotation = AnnotatedElementUtils.getMergedAnnotation(method, Translation.class);
                                return Pair.of(method, mergedAnnotation);
                            })
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
    public void loadStaticTranslatorDefinitions() {
        translatorMap.forEach((clazz, methods) -> {
            if (clazz.isEnum()) {
                return;
            }
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

    @Override
    public void loadEnumTranslatorDefinitions() {
        translatorMap.forEach((clazz, methods) -> {
            Optional.of(clazz)
                    .filter(Class::isEnum)
                    .map(enumClazz -> Pair.of(enumClazz, AnnotatedElementUtils.getMergedAnnotation(enumClazz, Translation.class)))
                    .ifPresent(pair -> {
                        Class<? extends Enum> key = (Class<? extends Enum>) pair.getKey();
                        registry.register(getTranslatorName(pair.getValue(), key), convertEnumToTranslatorDefinition(key));
                    });
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
