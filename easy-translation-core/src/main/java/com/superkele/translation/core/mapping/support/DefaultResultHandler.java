package com.superkele.translation.core.mapping.support;

import com.superkele.translation.core.exception.TranslationException;
import com.superkele.translation.core.mapping.ResultHandler;
import com.superkele.translation.core.util.PropertyUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * 结果处理器的默认实现
 * 如果是批处理，且结果是List,Array等类型，则将其自动映射成 Map
 * 当然，也存在需要将结果完整分给每一个对象的情况，需要开发者编写类自行处理
 */
public class DefaultResultHandler implements ResultHandler<Object, Object, Object> {

    @Override
    public Object handle(Object result, String[] groupKey, boolean isBatch) {
        if (!isBatch || groupKey.length == 0) {
            return result;
        }
        if (result instanceof Collection) {
            return groupByAttributesRecursive((Collection) result, groupKey, 0);
        }
        if (result instanceof Object[]) {
            Object[] arr = (Object[]) result;
            return groupByAttributesRecursive(Arrays.asList(arr), groupKey, 0);
        }
        return result;
    }


    @Override
    public Object map(Object processResult,int index ,Object[] mapperKey, boolean isBatch) {
        if (!isBatch || mapperKey.length == 0) {
            return processResult;
        }
        if (processResult instanceof Map) {
            Object res = processResult;
            for (Object key : mapperKey) {
                if (res instanceof Map) {
                    res = ((Map) res).get(key);
                }
                if (res == null) {
                    break;
                }
            }
            return res;
        }else if (processResult instanceof List){
            try {
                List<Object> list = (List<Object>) processResult;
                return list.get(index);
            } catch (IndexOutOfBoundsException e) {
                throw new TranslationException("无法顺利执行结果分配，重自定义ResultHandler，重写分配规则",e);
            }
        }
        return processResult;
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
        return obj -> PropertyUtils.invokeGetter(obj, attribute);
    }

}
