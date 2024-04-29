package com.superkele.translation.core.translator;

public abstract class AbstractConditionTranslator extends AbstractTranslator implements ConditionTranslator {

    @Override
    protected void init() {
        setMethodHandle(args -> this.translate(args[0], args[1]));
    }

}
