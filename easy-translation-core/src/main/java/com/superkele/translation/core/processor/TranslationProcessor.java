package com.superkele.translation.core.processor;


import com.superkele.translation.annotation.TranslationUnpackingHandler;
import com.superkele.translation.annotation.bean.BeanDescription;

import java.util.Collection;

public interface TranslationProcessor {

    void process(Object obj);

    void process(Object obj, Class<?> clazz);

    void processList(Collection<BeanDescription> obj);

    void processListAsync(Collection<BeanDescription> obj);

    void process(Object obj,Class<?> type, String field, boolean async, Class<? extends TranslationUnpackingHandler> listTypeHandlerClazz);
}
