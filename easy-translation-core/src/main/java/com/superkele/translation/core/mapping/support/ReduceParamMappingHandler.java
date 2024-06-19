package com.superkele.translation.core.mapping.support;

import com.superkele.translation.core.metadata.FieldTranslationEvent;
import com.superkele.translation.core.property.PropertyHandler;
import com.superkele.translation.core.translator.Translator;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 聚集参数 映射处理器
 */
public abstract class ReduceParamMappingHandler extends SingleMappingHandler {

    protected ReduceParamMappingHandler(PropertyHandler propertyHandler) {
        super(propertyHandler);
    }

    @Override
    public Object handleBatch(List<Object> sourceCollection, FieldTranslationEvent event, Translator translator, Map<String, Object> cache) {
        String[] mapper = event.getMapper();
        String[] other = event.getOther();
        int mapperLength = mapper.length;
        int otherLength = other.length;
        //组建参数
        List<Object[]> mapperKeys = sourceCollection.stream()
                .map(obj -> buildMapperKey(obj, mapperLength, mapper, event.getNullPointerExceptionHandler()))
                .collect(Collectors.toList());
        Object mappingValue = null;
        if (event.isCacheEnable()) {
            mappingValue = cache.get(event.getCacheKey());
            if (mappingValue == null && cache.containsKey(event.getCacheKey())) {
                return null;
            }
        }
        mappingValue = Optional.ofNullable(mappingValue)
                .orElseGet(() -> {
                    //填充args的真正key参数
                    //处理key
                    Object[] processedMapperKey = processMapperKeyBatch(mapperKeys);
                    Object[] args = new Object[16];
                    fillTranslatorArgs(args, mapperLength, processedMapperKey, otherLength, other);
                    return translator.doTranslate(args);
                });
        if (event.isCacheEnable()) {
            cache.put(event.getCacheKey(), mappingValue);
        }
        Optional.ofNullable(mappingValue)
                .map(val -> processMappingValue(val, event.getMapperOriginField()))
                .ifPresent(result -> {
                    for (int i = 0; i < sourceCollection.size(); i++) {
                        Object source = sourceCollection.get(i);
                        if (!event.isNotNullMapping()) {
                            if (getPropertyHandler().invokeGetter(source, event.getPropertyName()) != null) {
                                continue;
                            }
                        }
                        Object[] mapperKey = mapperKeys.get(i);
                        Object map = map(result, mapperKey);
                        Optional.ofNullable(map)
                                .map(val -> getPropertyHandler().invokeGetter(val, event.getReceive()))
                                .ifPresent(val -> getPropertyHandler().invokeSetter(source, event.getPropertyName(), val));
                    }
                });
        return mappingValue;
    }

    @Override
    public Object handleBatch(List<Object> sourceCollection, FieldTranslationEvent event, Translator translator) {
        return handleBatch(sourceCollection, event, translator, null);
    }

    /**
     * 批量处理映射参数
     *
     * @param params 列表元素为mapperKey,每个处理元素的item的mapperKey组成了params
     * @return
     */
    protected abstract Object[] processMapperKeyBatch(List<Object[]> params);

    @Override
    protected Object[] processMapperKey(Object[] params) {
        return processMapperKeyBatch(Collections.singletonList(params));
    }

    @Override
    public boolean waitPreEventWhenBatch() {
        return true;
    }
}
