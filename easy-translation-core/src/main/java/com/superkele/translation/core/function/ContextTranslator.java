package com.superkele.translation.core.function;

import com.superkele.translation.core.handler.ParameterHandler;
import com.superkele.translation.core.metadata.Translator;

@FunctionalInterface
public interface ContextTranslator extends Translator {

    Object translate();

    @Override
    default ParameterHandler getParameterHandler() {
        return args -> translate();
    }

    @Override
    default Object executeTranslate(Object... parameters) {
        return getParameterHandler().handle(parameters);
    }
}
