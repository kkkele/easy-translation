package com.superkele.translation.core.translator.support;

import com.superkele.translation.annotation.Translation;
import com.superkele.translation.core.translator.Resource;
import com.superkele.translation.core.translator.definition.TranslatorDefinition;
import com.superkele.translation.core.translator.definition.TranslatorDefinitionReader;
import com.superkele.translation.core.translator.definition.TranslatorDefinitionRegistry;
import com.superkele.translation.core.translator.definition.TranslatorLoader;
import com.superkele.translation.core.util.Pair;
import com.superkele.translation.core.util.ReflectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractTranslatorDefinitionReader implements TranslatorDefinitionReader {


    private final TranslatorDefinitionRegistry registry;
    protected TranslatorLoader translatorLoader = new DefaultTranslatorLoader();

    protected AbstractTranslatorDefinitionReader(TranslatorDefinitionRegistry registry) {
        this.registry = registry;
    }

    @Override
    public TranslatorDefinitionRegistry getRegistry() {
        return registry;
    }

    @Override
    public void loadTranslatorDefinitions(String basePath) {
        Map<Class<?>, Set<Method>> translatorMap = translatorLoader.getTranslator(basePath);
        translatorMap.forEach((clazz, methods) -> {
            loadEnumTranslatorDefinitions(clazz);
            Optional.ofNullable(methods)
                    .ifPresent(methodSet -> {
                        Map<Boolean, List<Method>> staticMethodMap = methodSet.stream()
                                .collect(Collectors.groupingBy(ReflectUtils::isStaticMethod));
                        Optional.ofNullable(staticMethodMap.get(true))
                                .ifPresent(staticMethodList -> {
                                    loadStaticMethodTranslatorDefinitions(clazz, staticMethodList);
                                });
                        Optional.ofNullable(staticMethodMap.get(false))
                                .ifPresent(dynamicMethodList -> {
                                    loadDynamicMethodTranslatorDefinitions(clazz, dynamicMethodList);
                                });
                    });
        });
    }

    protected void loadStaticMethodTranslatorDefinitions(Class<?> clazz, Collection<Method> methods) {
        methods.stream()
                .map(method -> {
                    Translation mergedAnnotation = AnnotatedElementUtils.getMergedAnnotation(method, Translation.class);
                    return Pair.of(method, mergedAnnotation);
                })
                .forEach(pair -> {
                    TranslatorDefinition translatorDefinition = convertStaticMethodToTranslatorDefinition(clazz, pair.getKey());
                    registry.register(getTranslatorName(pair.getValue(), pair.getKey()), translatorDefinition);
                });
    }

    protected void loadDynamicMethodTranslatorDefinitions(Class<?> clazz, Collection<Method> methods) {
        methods.stream()
                .map(method -> {
                    Translation mergedAnnotation = AnnotatedElementUtils.getMergedAnnotation(method, Translation.class);
                    return Pair.of(method, mergedAnnotation);
                })
                .forEach(methodAnnotationPair -> {
                    TranslatorDefinition translatorDefinition = convertDynamicMethodToTranslatorDefinition(clazz, methodAnnotationPair.getKey(), methodAnnotationPair.getValue());
                    registry.register(getTranslatorName(methodAnnotationPair.getValue(), methodAnnotationPair.getKey()), translatorDefinition);
                });
    }


    @Override
    public void loadTranslatorDefinitions(String[] basePath) {
        Optional.ofNullable(basePath)
                .ifPresent(paths -> Arrays.stream(paths)
                        .distinct()
                        .forEach(this::loadTranslatorDefinitions));
    }

    @Override
    public void loadTranslatorDefinitions(Resource resource) {
        //todo 增加读文件或者定义远程接口方式实现
    }
/*
    @Override
    public void loadDynamicTranslatorDefinitions(Object invokeObj, TranslatorDefinitionRegistry registry) {
        Optional.ofNullable(invokeObj)
                .map(obj -> {
                    Class<?> clazz = obj.getClass();
                    if (Proxy.isProxyClass(clazz)) {
                        clazz = clazz.getInterfaces()[0];
                    } else if (StringUtils.contains(clazz.getName(), "$Enhancer")) {
                        do {
                            clazz = clazz.getSuperclass();
                        } while (StringUtils.contains(clazz.getName(), "$Enhancer"));
                    }
                    return Pair.of(obj, clazz);
                })
                .filter(pair -> translatorMap.containsKey(pair.getValue()))
                .ifPresent(pair -> {
                    Object obj = pair.getKey();
                    Class<?> clazz = pair.getValue();
                    Set<Method> methods = translatorMap.get(clazz);
                    if (methods == null) {
                        return;
                    }
                    methods.stream()
                            .filter(method -> !ReflectUtils.isStaticMethod(method))
                            .map(method -> {
                                Translation mergedAnnotation = AnnotatedElementUtils.getMergedAnnotation(method, Translation.class);
                                return Pair.of(method, mergedAnnotation);
                            })
                            .forEach(methodAnnotationPair -> {
                                TranslatorDefinition translatorDefinition = convertDynamicMethodToTranslatorDefinition(obj, methodAnnotationPair.getKey());
                                registry.register(getTranslatorName(methodAnnotationPair.getValue(), methodAnnotationPair.getKey()), translatorDefinition);
                            });
                });
    }*/

    protected void loadEnumTranslatorDefinitions(Class<?> clazz) {
        Optional.of(clazz)
                .filter(Class::isEnum)
                .map(enumClazz -> Pair.of(enumClazz, AnnotatedElementUtils.getMergedAnnotation(enumClazz, Translation.class)))
                .ifPresent(pair -> {
                    Class<? extends Enum> key = (Class<? extends Enum>) pair.getKey();
                    registry.register(getTranslatorName(pair.getValue(), key), convertEnumToTranslatorDefinition(key));
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
     * @param clazz
     * @param method 静态方法
     * @return
     */
    protected abstract TranslatorDefinition convertStaticMethodToTranslatorDefinition(Class<?> clazz, Method method);

    /**
     * 将对象中的方法转为TranslatorDefinition
     *
     * @return
     */
    protected abstract TranslatorDefinition convertDynamicMethodToTranslatorDefinition(Class<?> clazz, Method method, Translation value);
}
