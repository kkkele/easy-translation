package com.superkele.translation.core.executor;

import com.superkele.translation.annotation.Mapping;
import com.superkele.translation.core.function.TranslationHandler;
import com.superkele.translation.core.handler.TranslatorContainer;
import com.superkele.translation.core.util.ReflectUtils;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Field;
import java.util.List;

@RequiredArgsConstructor
public class TranslateExecutor {

    private final TranslatorContainer container;

    public <T> void execute(T source) {
        Class<?> clazz = source.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Mapping.class)) {
                Mapping annotation = field.getAnnotation(Mapping.class);
                TranslationHandler<Long, String> translationHandler = container.findTranslationHandler(annotation.translator(), annotation.other());
                String translate = translationHandler.translate(ReflectUtils.invokeGetter(source, annotation.mapper()));
                ReflectUtils.invokeSetter(source, field.getName(), translate);
            }
        }
    }

    public <T> void execute(List<T> list) {

    }
}
