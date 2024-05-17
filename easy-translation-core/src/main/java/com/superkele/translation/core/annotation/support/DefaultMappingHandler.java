package com.superkele.translation.core.annotation.support;

import com.superkele.translation.core.property.PropertyGetter;
import com.superkele.translation.core.property.PropertySetter;
import com.superkele.translation.core.property.support.AbstractMethodHandlePropertyHandler;
import com.superkele.translation.core.property.support.PropertyHandler;
import com.superkele.translation.core.translator.factory.TransExecutorFactory;

public class DefaultMappingHandler extends AbstractMappingHandler {

    private PropertyHandler propertyHandler = new AbstractMethodHandlePropertyHandler();

    public DefaultMappingHandler(TransExecutorFactory translatorFactory) {
        super(translatorFactory);
    }

    public PropertyHandler getPropertyHandler() {
        return propertyHandler;
    }

    public DefaultMappingHandler setPropertyHandler(PropertyHandler propertyHandler) {
        this.propertyHandler = propertyHandler;
        return this;
    }

    @Override
    protected PropertyGetter getPropertyGetter() {
        return propertyHandler::invokeGetter;
    }

    @Override
    protected PropertySetter getPropertySetter() {
        return propertyHandler::invokeSetter;
    }


}
