package com.superkele.translation.core.mapping.support;

import com.superkele.translation.annotation.NullPointerExceptionHandler;
import com.superkele.translation.core.mapping.MappingHandler;
import com.superkele.translation.core.metadata.FieldTranslationEvent;
import com.superkele.translation.core.property.PropertyHandler;
import com.superkele.translation.core.translator.Translator;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AbstractMappingHandler implements MappingHandler {

    protected final PropertyHandler propertyHandler;

    protected AbstractMappingHandler(PropertyHandler propertyHandler) {
        this.propertyHandler = propertyHandler;
    }

    protected PropertyHandler getPropertyHandler() {
        return propertyHandler;
    }


    protected Object[] buildMapperKey(Object obj, int mapperLength, String[] mapper, NullPointerExceptionHandler nullPointerExceptionHandler) {
        Object[] mapperKey = new Object[mapperLength];
        for (int i = 0; i < mapperLength; i++) {
            if (StringUtils.isNotBlank(mapper[i])) {
                try {
                    mapperKey[i] = getPropertyHandler().invokeGetter(obj, mapper[i]);
                } catch (NullPointerException e) {
                    nullPointerExceptionHandler.handle(e);
                }
            }
        }
        return mapperKey;
    }

    protected void fillTranslatorArgs(Object[] args, int mapperLength, Object[] processedMapperKey, int otherLength, String[] other) {
        for (int i = 0; i < mapperLength; i++) {
            args[i] = processedMapperKey[i];
        }
        int j = 0;
        int i = mapperLength;
        //填充args的other参数
        while (i < mapperLength + otherLength) {
            args[i++] = other[j++];
        }
    }

    @Override
    public Object handleBatch(List<Object> sourceCollection, FieldTranslationEvent event, Translator translator) {
        String[] mapper = event.getMapper();
        String[] other = event.getOther();
        int mapperLength = mapper.length;
        int otherLength = other.length;
        //组建参数
        Object[] args = new Object[16];
        List<Object[]> mapperKeys = sourceCollection.stream()
                .map(obj -> buildMapperKey(obj, mapperLength, mapper, event.getNullPointerExceptionHandler()))
                .collect(Collectors.toList());
        //填充args的真正key参数
        //处理key
        Object[] processedMapperKey = processMapperKeyBatch(mapperKeys);
        fillTranslatorArgs(args, mapperLength, processedMapperKey, otherLength, other);
        Object mappingValue = translator.doTranslate(args);
        Optional.ofNullable(mappingValue)
                .map(val -> processMappingValue(val, event.getMapperOriginField()))
                .ifPresent(result -> {
                    for (int i = 0; i < sourceCollection.size(); i++) {
                        Object source = sourceCollection.get(i);
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
    public Object handle(Object source, FieldTranslationEvent event, Translator translator) {
        String[] mapper = event.getMapper();
        String[] other = event.getOther();
        int mapperLength = mapper.length;
        int otherLength = other.length;
        //组建参数
        Object[] args = new Object[16];
        Object[] mapperKey = buildMapperKey(source, mapperLength, mapper, event.getNullPointerExceptionHandler());
        //填充args的真正key参数
        //处理key
        Object[] processedMapperKey = processMapperKey(mapperKey);
        fillTranslatorArgs(args, mapperLength, processedMapperKey, otherLength, other);
        //翻译值
        Object mappingValue = translator.doTranslate(args);
        Optional.ofNullable(mappingValue)
                .map(val -> processMappingValue(val, event.getMapperOriginField()))
                .map(val -> map(val, mapperKey))
                .map(val -> getPropertyHandler().invokeGetter(val, event.getReceive()))
                .ifPresent(val -> getPropertyHandler().invokeSetter(source, event.getPropertyName(), val));
        return mappingValue;
    }

    /**
     * 批量处理映射参数
     *
     * @param params 列表元素为mapperKey,每个处理元素的item的mapperKey组成了params
     * @return
     */
    protected abstract Object[] processMapperKeyBatch(List<Object[]> params);

    /**
     * 单个处理映射参数
     *
     * @param params 处理元素的item的mapperKey组成了params
     * @return
     */
    protected abstract Object[] processMapperKey(Object[] params);

    protected abstract Object processMappingValue(Object originValue, String[] originMapperField);

    protected abstract Object map(Object processedResult, Object[] mapperKey);
}
