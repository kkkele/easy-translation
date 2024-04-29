package com.superkele.translation.core.translator;

import com.superkele.translation.core.translator.handle.MethodHandle;

public abstract class AbstractTranslator implements Translator {

    protected MethodHandle methodHandle;


    protected AbstractTranslator() {
        init();
    }

    protected abstract void init();

    @Override
    public MethodHandle getMethodHandle() {
        return methodHandle;
    }

    @Override
    public void setMethodHandle(MethodHandle methodHandle) {
        this.methodHandle = methodHandle;
    }
}
