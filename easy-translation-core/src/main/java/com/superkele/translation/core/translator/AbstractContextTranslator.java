package com.superkele.translation.core.translator;

public abstract class AbstractContextTranslator extends AbstractTranslator implements ContextTranslator {

    @Override
    protected void init() {
        setMethodHandle(args -> this.translate());
    }

}
