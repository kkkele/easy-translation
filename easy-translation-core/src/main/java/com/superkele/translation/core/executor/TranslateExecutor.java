package com.superkele.translation.core.executor;

import com.superkele.translation.annotation.Mapping;
import com.superkele.translation.core.container.IMappingFieldContainer;
import com.superkele.translation.core.container.ITranslatorContainer;
import com.superkele.translation.core.function.TranslationHandler;
import com.superkele.translation.core.metadata.FieldInfo;
import com.superkele.translation.core.util.ReflectUtils;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class TranslateExecutor {


    private final ITranslatorContainer translatorContainer;

    private final IMappingFieldContainer mappingFieldContainer;


    public <T> void execute(T source) {
        Objects.requireNonNull(source, "translate source must not be null");
        List<FieldInfo> fieldInfo = mappingFieldContainer.find2bMappedField(source.getClass());
    }

}
