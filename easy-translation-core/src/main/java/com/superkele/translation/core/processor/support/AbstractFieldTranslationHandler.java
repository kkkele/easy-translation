package com.superkele.translation.core.processor.support;

import com.superkele.translation.core.metadata.FieldTranslation;
import com.superkele.translation.core.metadata.FieldTranslationEvent;
import com.superkele.translation.core.processor.FieldTranslationHandler;
import com.superkele.translation.core.thread.ContextPasser;

import java.util.Collection;
import java.util.List;

public abstract class AbstractFieldTranslationHandler implements FieldTranslationHandler {

    protected final FieldTranslation fieldTranslation;

    public AbstractFieldTranslationHandler(FieldTranslation fieldTranslation) {
        this.fieldTranslation = fieldTranslation;
    }

    protected abstract void awaitToTranslation();

    protected abstract boolean getAsyncEnable();

    protected abstract List<ContextPasser> getContextPassers();

    protected abstract void translate(Object obj, FieldTranslationEvent sortEvent);

    @Override
    public FieldTranslation getFieldTranslation() {
        return fieldTranslation;
    }

    @Override
    public void handle(Object obj) {
        if (getAsyncEnable()) {
            getContextPassers().forEach(ContextPasser::setPassValue);
        }
        FieldTranslationEvent[] sortEvents = this.fieldTranslation.getSortEvents();
        //顺序执行事件
        for (FieldTranslationEvent sortEvent : sortEvents) {
            translate(obj, sortEvent);
        }
        if (getAsyncEnable()) {
            awaitToTranslation();
            getContextPassers().forEach(ContextPasser::clearContext);
        }
    }

    @Override
    public void handle(Collection collection, boolean async) {

    }
}
