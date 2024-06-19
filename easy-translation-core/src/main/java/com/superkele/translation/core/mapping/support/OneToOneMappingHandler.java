package com.superkele.translation.core.mapping.support;

import com.superkele.translation.core.property.PropertyHandler;

import java.util.List;

public class OneToOneMappingHandler extends AbstractMappingHandler {

    protected OneToOneMappingHandler(PropertyHandler propertyHandler) {
        super(propertyHandler);
    }

    @Override
    protected Object[] processMapperKeyBatch(List<Object[]> params) {
        return params.get(0);
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
