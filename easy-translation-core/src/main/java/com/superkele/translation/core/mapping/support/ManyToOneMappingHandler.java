package com.superkele.translation.core.mapping.support;

import com.superkele.translation.core.property.PropertyHandler;

import java.util.List;
import java.util.stream.Collectors;

public class ManyToOneMappingHandler extends AbstractMappingHandler {

    protected ManyToOneMappingHandler(PropertyHandler propertyHandler) {
        super(propertyHandler);
    }

    @Override
    protected Object[] processMapperKeyBatch(List<Object[]> params) {
        Object[] args = new Object[params.get(0).length];
        for (int i = 0; i < args.length; i++) {
            final int index = i;
            List<Object> collect = params.stream()
                    .map(obj -> obj[index])
                    .collect(Collectors.toList());
            args[index] = collect;
        }
        return args;
    }

    @Override
    protected Object[] processMapperKey(Object[] params) {
        return new Object[0];
    }

    @Override
    protected Object processMappingValue(Object originValue, String[] originMapperField) {
        return originValue;
    }

    @Override
    protected Object map(Object processedResult, Object[] mapperKey) {
        return processedResult;
    }

    @Override
    public boolean waitPreEventWhenBatch() {
        return true;
    }
}
