package com.superkele.translation.core.mapping.support;

import com.superkele.translation.core.exception.TranslationException;
import com.superkele.translation.core.property.PropertyHandler;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class OneToManyMappingHandler extends AbstractMappingHandler {

    protected OneToManyMappingHandler(PropertyHandler propertyHandler) {
        super(propertyHandler);
    }

    private <T> Object groupByAttributesRecursive(Collection<T> collection, String[] attributes, int depth) {
        if (depth == attributes.length - 1) {
            return collection.stream().collect(Collectors.groupingBy(createKeyExtractor(attributes[depth])));
        } else {
            return collection.stream().collect(Collectors.groupingBy(createKeyExtractor(attributes[depth]),
                    Collectors.collectingAndThen(Collectors.toList(), list ->
                            groupByAttributesRecursive(list, attributes, depth + 1))));
        }
    }

    private <T> Function<T, Object> createKeyExtractor(String attribute) {
        return obj -> propertyHandler.invokeGetter(obj, attribute);
    }

    @Override
    protected PropertyHandler getPropertyHandler() {
        return propertyHandler;
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
        if (!(originValue instanceof Collection)) {
            return originValue;
        }
        if (originMapperField.length == 0) {
            throw new TranslationException("OneToManyMappingHandler映射失败，mapper参数为0，无法进行映射。请自行完成映射器或更换其他映射器");
        }
        Collection collectionRes = (Collection) originValue;
        return groupByAttributesRecursive(collectionRes, originMapperField, 0);
    }

    @Override
    protected Object map(Object processedResult, Object[] mapperKey) {
        Object res = processedResult;
        for (Object key : mapperKey) {
            Map map = (Map) res;
            res = map.get(key);
        }
        return res;
    }

}
