package com.superkele.translation.core.mapping.support;

import com.superkele.translation.core.exception.TranslationException;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ManyToManyMappingHandler extends ReduceParamMappingHandler {

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
            if (res == null){
                return null;
            }
            Map map = (Map) res;
            res = map.get(key);
        }
        return res;
    }

    private <T> Object groupByAttributesRecursive(Collection<T> collection, String[] attributes, int depth) {
        if (depth == attributes.length - 1) {
            return collection.stream().distinct().collect(Collectors.toMap(createKeyExtractor(attributes[depth]), Function.identity()));
        } else {
            return collection.stream().collect(Collectors.groupingBy(createKeyExtractor(attributes[depth]),
                    Collectors.collectingAndThen(Collectors.toList(), list ->
                            groupByAttributesRecursive(list, attributes, depth + 1))));
        }
    }

    private <T> Function<T, Object> createKeyExtractor(String attribute) {
        return obj -> getPropertyHandler().invokeGetter(obj, attribute);
    }


}
