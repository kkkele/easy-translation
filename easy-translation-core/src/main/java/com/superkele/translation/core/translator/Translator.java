package com.superkele.translation.core.translator;


import com.superkele.translation.core.translator.handle.MethodHandle;

public interface Translator {

    MethodHandle getMethodHandle();

    void setMethodHandle(MethodHandle methodHandle);

    default Object executeTranslate(Object... parameters) {
        return getMethodHandle().invoke(parameters);
    }
}
