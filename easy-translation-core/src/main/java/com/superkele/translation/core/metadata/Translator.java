package com.superkele.translation.core.metadata;


import com.superkele.translation.core.handler.ParameterHandler;

public interface Translator {


    ParameterHandler getParameterHandler();

    Object executeTranslate(Object... parameters);
}
