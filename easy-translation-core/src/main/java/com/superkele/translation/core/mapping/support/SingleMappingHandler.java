/*
package com.superkele.translation.core.mapping.support;

import cn.hutool.core.util.StrUtil;
import com.superkele.translation.annotation.NullPointerExceptionHandler;
import com.superkele.translation.core.mapping.MappingHandler;
import com.superkele.translation.core.metadata.FieldTranslationEvent;
import com.superkele.translation.core.property.PropertyHandler;
import com.superkele.translation.core.translator.Translator;
import com.superkele.translation.core.util.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Optional;

*/
/**
 * 单映射处理器
 *//*

public abstract class SingleMappingHandler implements MappingHandler {

    @Override
    public Object handle(Object source, FieldTranslationEvent event, Translator translator, Map<String, Object> cache) {
        if (!event.isNotNullMapping()) {
            if (getPropertyHandler().invokeGetter(source, event.getPropertyName()) != null) {
                return null;
            }
        }
        String[] mapper = event.getMapper();
        String[] other = event.getOther();
        int mapperLength = mapper.length;
        int otherLength = other.length;
        Object[] mapperKey = buildMapperKey(source, mapperLength, mapper, event.getNullPointerExceptionHandler());
        Object mappingValue = null;
        if (cache != null) {
            String cacheUnique = event.getCacheKey() + StrUtil.join(",", mapperKey);
            mappingValue = cache.get(cacheUnique);
        }
        mappingValue = Optional.ofNullable(mappingValue)
                .orElseGet(() -> {
                    //组建参数
                    Object[] args = new Object[16];
                    //填充args的真正key参数
                    //处理key
                    Object[] processedMapperKey = processMapperKey(mapperKey);
                    fillTranslatorArgs(args, mapperLength, processedMapperKey, otherLength, other);
                    //翻译值
                    return translator.doTranslate(args);
                });
        if (cache != null && mappingValue != null) {
            String cacheUnique = event.getCacheKey() + StrUtil.join(",", mapperKey);
            cache.put(cacheUnique, mappingValue);
        }
        Optional.ofNullable(mappingValue)
                .map(val -> processMappingValue(val, event.getGroupKey()))
                .map(val -> map(val, mapperKey))
                .map(val -> getPropertyHandler().invokeGetter(val, event.getReceive()))
                .ifPresent(val -> getPropertyHandler().invokeSetter(source, event.getPropertyName(), val));
        return mappingValue;
    }

    @Override
    public PropertyHandler getPropertyHandler() {
        return PropertyUtils.getPropertyHandler();
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


    */
/**
     * 单个处理映射参数
     *
     * @param params 处理元素的item的mapperKey组成了params
     * @return
     *//*

    protected abstract Object[] processMapperKey(Object[] params);

    protected abstract Object processMappingValue(Object originValue, String[] originMapperField);

    protected abstract Object map(Object processedResult, Object[] mapperKey);


}
*/
