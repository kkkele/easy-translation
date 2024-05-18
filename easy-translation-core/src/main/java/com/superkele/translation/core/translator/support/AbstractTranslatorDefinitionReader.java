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

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

public abstract class AbstractTranslatorDefinitionReader implements TranslatorDefinitionReader {


    protected TranslatorLoader translatorLoader;
    protected Map<Class<?>, Set<Method>> translatorMap = new HashMap<>();

    protected AbstractTranslatorDefinitionReader(TranslatorLoader translatorLoader, String[] locations) {
        this.translatorLoader = translatorLoader;
        for (String location : locations) {
            Map<Class<?>, Set<Method>> translator = this.translatorLoader.getTranslator(location);
            this.translatorMap.putAll(translator);
        }
    }

    protected AbstractTranslatorDefinitionReader(String... locations) {
        this(new DefaultTranslatorLoader(), locations);
    }

    @Override
    public Set<Class<?>> getTranslatorDeclaringClasses() {
        return translatorMap.keySet();
    }


    @Override
    public TranslatorLoader getTranslatorLoader() {
        return this.translatorLoader;
    }

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
                    return Pair.of(obj,clazz);
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
    }

    @Override
    public void loadDynamicTranslatorDefinitions(Object[] invokeObjs, TranslatorDefinitionRegistry registry) {
        Optional.ofNullable(invokeObjs)
                .ifPresent(invokeObjArr -> {
                    for (Object invokeObj : invokeObjArr) {
                        loadDynamicTranslatorDefinitions(invokeObj, registry);
                    }
                });
    }

    @Override
    public void loadStaticTranslatorDefinitions(TranslatorDefinitionRegistry registry) {
        translatorMap.forEach((clazz, methods) -> {
            if (clazz.isEnum()) {
                return;
            }
            if (methods == null) {
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
    public void loadEnumTranslatorDefinitions(TranslatorDefinitionRegistry registry) {
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
