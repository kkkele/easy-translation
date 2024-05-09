package com.superkele.translation.core.processor;


import com.superkele.translation.annotation.bean.BeanDescription;

import java.util.Collection;

public interface TranslationProcessor {

    void process(Object obj);

    void process(Object obj, Class<?> clazz);

    void process(Collection<BeanDescription> obj);

    void processAsync(Collection<BeanDescription> obj);


}
