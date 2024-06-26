package com.superkele.translation.core.mapping.support;

import cn.hutool.core.util.StrUtil;
import com.superkele.translation.core.mapping.ParamHandler;
import com.superkele.translation.core.mapping.ResultHandler;
import com.superkele.translation.core.mapping.TranslationInvoker;
import com.superkele.translation.core.metadata.FieldTranslationEvent;
import com.superkele.translation.core.metadata.MapperDesc;
import com.superkele.translation.core.translator.Translator;
import com.superkele.translation.core.util.PropertyUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.superkele.translation.core.util.PropertyUtils.getPropertyHandler;

public class DefaultTranslationInvoker implements TranslationInvoker {
    @Override
    public void invoke(Object source, Translator translator, FieldTranslationEvent event, Map<String, Object> cache) {
        if (!event.isNotNullMapping()) {
            if (PropertyUtils.invokeGetter(source, event.getPropertyName()) != null) {
                return;
            }
        }
        MapperDesc[] mappers = event.getMappers();
        String[] others = event.getOthers();
        int mapperLength = mappers.length;
        int otherLength = others.length;
        Object[] mapperKey = Arrays.stream(mappers)
                .map(mapperDesc -> {
                    String mapper = mapperDesc.getMapper();
                    Object param = null;
                    if (StrUtil.isNotBlank(mapper)) {
                        try {
                            param = getPropertyHandler().invokeGetter(source, mapper);
                        } catch (NullPointerException e) {
                            event.getNullPointerExceptionHandler().handle(e);
                        }
                    }
                    return param;
                })
                .toArray(Object[]::new);
        Object mappingValue = null;
        //loadFromCache
        if (cache != null) {
            String cacheUnique = event.getCacheKey() + StrUtil.join(",", mapperKey);
            mappingValue = cache.get(cacheUnique);
        }
        //缓存未命中
        mappingValue = Optional.ofNullable(mappingValue)
                .orElseGet(() -> {
                    //组建参数
                    Object[] args = new Object[16];
                    Object[] processedMapperKey = new Object[mapperLength];
                    for (int i = 0; i < mappers.length; i++) {
                        processedMapperKey[i] = mappers[i].getParamHandler()
                                .wrapper(mapperKey[i], mappers[i].getSourceClass(), mappers[i].getTargetClass(), mappers[i].getTypes());
                    }
                    fillTranslatorArgs(args, mapperLength, processedMapperKey, otherLength, others);
                    //翻译值
                    return translator.doTranslate(args);
                });
        if (mappingValue == null) {
            return;
        }
        //加载至缓存中
        if (cache != null) {
            String cacheUnique = event.getCacheKey() + StrUtil.join(",", mapperKey);
            cache.put(cacheUnique, mappingValue);
        }
        //结果处理
        ResultHandler resultHandler = event.getResultHandler();
        //处理翻译结果
        Object processedResult = resultHandler.handle(mappingValue, event.getGroupKey(), false);
        //分配结果
        Object mapResult = resultHandler.map(processedResult, mapperKey, false);
        PropertyUtils.invokeSetter(source, event.getPropertyName(), PropertyUtils.invokeGetter(mapResult, event.getReceive()));
    }

    @Override
    public void invokeBatch(List<Object> sources, Translator translator, FieldTranslationEvent event, Map<String, Object> cache) {
        MapperDesc[] mappers = event.getMappers();
        String[] others = event.getOthers();
        int mapperLength = mappers.length;
        int otherLength = others.length;
        List<Object[]> mapperKeys = sources.stream()
                .map(source -> Arrays.stream(mappers)
                        .map(mapperDesc -> {
                            String mapper = mapperDesc.getMapper();
                            Object param = null;
                            if (StrUtil.isNotBlank(mapper)) {
                                try {
                                    param = getPropertyHandler().invokeGetter(source, mapper);
                                } catch (NullPointerException e) {
                                    event.getNullPointerExceptionHandler().handle(e);
                                }
                            }
                            return param;
                        })
                        .toArray(Object[]::new))
                .collect(Collectors.toList());
        Object mappingValue = Optional.ofNullable(cache)
                .map(c -> {
                    if (event.isCacheEnable()) {
                        return c.get(event.getCacheKey());
                    } else {
                        return null;
                    }
                })
                .orElse(null);
        mappingValue = Optional.ofNullable(mappingValue)
                .orElseGet(() -> {
                    //组建参数
                    Object[] args = new Object[16];
                    Object[] processedMapperKey = new Object[mapperLength];
                    for (int i = 0; i < mappers.length; i++) {
                        final int _index = i;
                        List<Object> params = mapperKeys.stream()
                                .map(mapperKey -> mapperKey[_index])
                                .collect(Collectors.toList());
                        processedMapperKey[i] = mappers[i].getParamHandler().wrapperBatch(params, mappers[i].getSourceClass(), mappers[i].getTargetClass(), mappers[i].getTypes());
                    }
                    fillTranslatorArgs(args, mapperLength, processedMapperKey, otherLength, others);
                    return translator.doTranslate(args);
                });
        if (mappingValue == null) {
            return;
        }
        //加载至缓存中
        if (event.isCacheEnable() && cache != null) {
            cache.put(event.getCacheKey(), mappingValue);
        }
        //结果处理
        ResultHandler resultHandler = event.getResultHandler();
        //处理翻译结果
        Object processedResult = resultHandler.handle(mappingValue, event.getGroupKey(), true);
        for (int i = 0; i < sources.size(); i++) {
            Object source = sources.get(i);
            Object[] mapperKey = mapperKeys.get(i);
            //分配结果
            Object mapResult = resultHandler.map(processedResult, mapperKey, true);
            //注入值
            PropertyUtils.invokeSetter(source, event.getPropertyName(), PropertyUtils.invokeGetter(mapResult, event.getReceive()));
        }
    }

    protected void fillTranslatorArgs(Object[] args, int mapperLength, Object[] mapperKeys, int otherLength, String[] others) {
        for (int i = 0; i < mapperLength; i++) {
            args[i] = mapperKeys[i];
        }
        int j = 0;
        int i = mapperLength;
        //填充args的other参数
        while (i < mapperLength + otherLength) {
            args[i++] = others[j++];
        }
    }

}
