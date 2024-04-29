package com.superkele.translation.core.translator;

public abstract class AbstractMapperTranslator extends AbstractTranslator implements MapperTranslator {

    @Override
    protected void init() {
        setMethodHandle(args -> this.translate(args[0]));
    }

}
