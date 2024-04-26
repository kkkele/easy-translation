package com.superkele.translation.core.function;

import com.superkele.translation.core.handler.ParameterHandler;
import com.superkele.translation.core.metadata.Translator;

@FunctionalInterface
public interface ConditionTranslator extends Translator {

    Object translate(Object mapper, Object other);


    @Override
    default ParameterHandler getParameterHandler() {
        return args -> translate(args[0], args[1]);
    }

    @Override
    default Object executeTranslate(Object... parameters) {
        return getParameterHandler().handle(parameters);
    }
}
