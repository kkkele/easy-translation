package com.superkele.translation.core.mapping.support;

import cn.hutool.core.util.StrUtil;
import com.superkele.translation.core.mapping.ParamHandler;
import com.superkele.translation.core.mapping.ResultHandler;
import com.superkele.translation.core.mapping.TranslationInvoker;
import com.superkele.translation.core.metadata.FieldTranslationEvent;
import com.superkele.translation.core.metadata.MapperDesc;
import com.superkele.translation.core.translator.Translator;
import com.superkele.translation.core.translator.factory.TranslatorFactory;
import com.superkele.translation.core.util.PropertyUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.superkele.translation.core.util.PropertyUtils.getPropertyHandler;

public class DefaultTranslationInvoker implements TranslationInvoker {

    private final TranslatorFactory translatorFactory;

    public DefaultTranslationInvoker(TranslatorFactory translatorFactory) {
        this.translatorFactory = translatorFactory;
    }

    @Override
    public void invoke(Object source, FieldTranslationEvent event, Map<String, Object> cache) {
        if (filterNotMapping(source, event)) return;
        MapperDesc[] mappers = event.getMappers();
        String[] others = event.getOthers();
        int mapperLength = mappers.length;
        int otherLength = others.length;
        Object[] mapperKey = buildSingleMapperKey(source, event, mappers);
        Object mappingValue = null;
        //loadFromCache
        String cacheKey = null;
        if (cache != null) {
            cacheKey = event.getTranslator() + StrUtil.join(",", mapperKey);
            mappingValue = cache.get(cacheKey);
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
                    Translator translator = translatorFactory.findTranslator(event.getTranslator());
                    return translator.doTranslate(args);
                });
        if (mappingValue == null) {
            return;
        }
        //加载至缓存中
        if (cache != null) {
            cache.put(cacheKey, mappingValue);
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
    public void invokeBatch(List<Object> sources, FieldTranslationEvent event, Map<String, Object> cache) {
        MapperDesc[] mappers = event.getMappers();
        String[] others = event.getOthers();
        int mapperLength = mappers.length;
        int otherLength = others.length;
        List<Object[]> mapperKeys = sources.stream()
                .map(source -> buildSingleMapperKey(source, event, mappers))
                .collect(Collectors.toList());
        //loadFromCache
        String cacheKey = null;
        Object mappingValue = null;
        if (cache != null) {
            cacheKey = event.getTranslator() + StrUtil.join(",", event.getMappers(), event.getOthers(), event.getMappingStrategy());
            mappingValue = cache.get(cacheKey);
        }
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
                    Translator translator = translatorFactory.findTranslator(event.getTranslator());
                    return translator.doTranslate(args);
                });
        if (mappingValue == null) {
            return;
        }
        //加载至缓存中
        if (cache != null) {
            cache.put(cacheKey, mappingValue);
        }
        //结果处理
        ResultHandler resultHandler = event.getResultHandler();
        //处理翻译结果
        Object processedResult = resultHandler.handle(mappingValue, event.getGroupKey(), true);
        for (int i = 0; i < sources.size(); i++) {
            Object source = sources.get(i);
            if (filterNotMapping(source, event)) {
                continue;
            }
            Object[] mapperKey = mapperKeys.get(i);
            //分配结果
            Object mapResult = resultHandler.map(processedResult, mapperKey, true);
            //注入值
            PropertyUtils.invokeSetter(source, event.getPropertyName(), PropertyUtils.invokeGetter(mapResult, event.getReceive()));
        }
    }

    /**
     * 组建单的对象的基础映射键
     *
     * @param source  对象
     * @param event   事件
     * @param mappers 映射详细描述
     * @return
     */
    protected Object[] buildSingleMapperKey(Object source, FieldTranslationEvent event, MapperDesc[] mappers) {
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
        return mapperKey;
    }

    /**
     * 组建翻译参数
     */
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

    /**
     * 过滤不需要映射的对象
     *
     * @param source
     * @param event
     * @return
     */
    protected boolean filterNotMapping(Object source, FieldTranslationEvent event) {
        if (!event.isNotNullMapping()) {
            if (PropertyUtils.invokeGetter(source, event.getPropertyName()) != null) {
                return true;
            }
        }
        return false;
    }

}
