package com.superkele.translation.core.mapping.support;

import cn.hutool.core.util.StrUtil;
import com.superkele.translation.annotation.NullPointerExceptionHandler;
import com.superkele.translation.core.mapping.ParamHandler;
import com.superkele.translation.core.mapping.TranslationInvoker;
import com.superkele.translation.core.metadata.FieldTranslationEvent;
import com.superkele.translation.core.translator.Translator;
import com.superkele.translation.core.util.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DefaultTranslationInvoker implements TranslationInvoker {
    @Override
    public void invoke(Object source, Translator translator, TranslationEnvironment environment) {
        FieldTranslationEvent event = environment.getEvent();
        Map<String, Object> cache = environment.getCache();
        if (!event.isNotNullMapping()) {
            if (PropertyUtils.invokeGetter(source, event.getPropertyName()) != null) {
                return;
            }
        }
        String[] other = event.getOther();
        int otherLength = other.length;
        //todo
    }

    @Override
    public void invokeBatch(List<Object> sources, Translator translator, TranslationEnvironment environment) {

    }

}
