package com.superkele.translation.core.mapping.support;

import com.superkele.translation.core.property.PropertyHandler;

import java.util.List;

public class OneToOneMappingHandler extends SingleMappingHandler {

    protected OneToOneMappingHandler(PropertyHandler propertyHandler) {
        super(propertyHandler);
    }

    @Override
    protected Object[] processMapperKey(Object[] params) {
        return params;
    }

    @Override
    protected Object processMappingValue(Object originValue, String[] originMapperField) {
        return originValue;
    }


    @Override
    protected Object map(Object processedResult, Object[] mapperKey) {
        return processedResult;
    }
}
