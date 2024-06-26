package com.superkele.translation.core.metadata.support;

import cn.hutool.core.util.StrUtil;
import com.superkele.translation.annotation.Mapper;
import com.superkele.translation.annotation.Mapping;
import com.superkele.translation.annotation.MappingHandler;
import com.superkele.translation.annotation.RefTranslation;
import com.superkele.translation.core.exception.TranslationException;
import com.superkele.translation.core.mapping.ParamHandlerResolver;
import com.superkele.translation.core.mapping.ResultHandlerResolver;
import com.superkele.translation.core.metadata.FieldTranslationEvent;
import com.superkele.translation.core.metadata.MapperDesc;
import com.superkele.translation.core.metadata.ParamDesc;
import com.superkele.translation.core.translator.definition.ConfigurableTranslatorDefinitionFactory;
import com.superkele.translation.core.translator.definition.TranslatorDefinition;
import com.superkele.translation.core.util.Pair;
import com.superkele.translation.core.util.Singleton;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultMappingFiledTranslationBuilder extends AbstractMappingFiledTranslationBuilder {

    private final ConfigurableTranslatorDefinitionFactory translatorDefinitionFactory;

    private final ParamHandlerResolver paramHandlerResolver;

    private final ResultHandlerResolver resultResolver;

    public DefaultMappingFiledTranslationBuilder(ConfigurableTranslatorDefinitionFactory translatorDefinitionFactory, ParamHandlerResolver paramHandlerResolver, ResultHandlerResolver resultResolver) {
        this.translatorDefinitionFactory = translatorDefinitionFactory;
        this.paramHandlerResolver = paramHandlerResolver;
        this.resultResolver = resultResolver;
    }

    @Override
    protected void setReceive(FieldTranslationEvent event, Field field, Mapping mapping) {
        event.setReceive(mapping.receive());
    }

    @Override
    protected void setMappingStrategy(FieldTranslationEvent event, Field field, Mapping mapping) {
        event.setMappingStrategy(mapping.strategy());
    }

    @Override
    protected void setNullPointerExceptionHandler(FieldTranslationEvent event, Field field, Mapping mapping) {
        event.setNullPointerExceptionHandler(Singleton.get(mapping.nullPointerHandler()));
    }

    @Override
    protected void setOthers(FieldTranslationEvent event, Field field, Mapping mapping) {
        event.setOthers(mapping.other());

    }

    @Override
    protected void setNotNullMapping(FieldTranslationEvent event, Field field, Mapping mapping) {
        event.setNotNullMapping(mapping.notNullMapping());
    }

    @Override
    protected void setTranslator(FieldTranslationEvent event, Field field, Mapping mapping) {
        event.setTranslator(mapping.translator());
    }

    @Override
    protected void setAsync(FieldTranslationEvent event, Field field, Mapping mapping) {
        event.setAsync(mapping.async());
    }

    @Override
    protected void setPropertyName(FieldTranslationEvent event, Field field, Mapping mapping) {
        event.setPropertyName(field.getName());
    }

    @Override
    protected void setRefTranslation(FieldTranslationEvent event, Field field, Mapping mapping) {
        RefTranslation mergedAnnotation = AnnotatedElementUtils.getMergedAnnotation(field, RefTranslation.class);
        event.setRefTranslation(mergedAnnotation);
    }

    @Override
    protected void setResultHandler(FieldTranslationEvent event, Field field, Mapping mapping) {
        event.setResultHandler(resultResolver.resolve(mapping.resultHandler()));
    }

    @Override
    protected void setMapperDesc(FieldTranslationEvent event, Field field, Mapping mapping) {
        if (StrUtil.isBlank(mapping.translator())) {
            return;
        }
        TranslatorDefinition translatorDefinition = translatorDefinitionFactory.findTranslatorDefinition(mapping.translator());
        ParamDesc[] parameterTypes = translatorDefinition.getParameterTypes();
        //获取key的下标
        int[] mapperIndexs = translatorDefinition.getMapperIndex();
        int i = 0;
        Mapper[] mappers = mapping.mappers();
        List<Pair<String, String>> mapperParamHandlerPair = Arrays.stream(mappers)
                .map(mapper -> Arrays.stream(mapper.value())
                        .map(mapperValue -> Pair.of(mapperValue, mapper.paramHandler()))
                        .collect(Collectors.toList()))
                .flatMap(List::stream)
                .collect(Collectors.toList());
        Class<?> declaringClass = field.getDeclaringClass();
        MapperDesc[] mapperDescs = new MapperDesc[mapperParamHandlerPair.size()];
        for (Pair<String, String> pair : mapperParamHandlerPair) {
            try {
                String mapperFieldName = pair.getKey();
                Field declaredField = declaringClass.getDeclaredField(mapperFieldName);
                ParamDesc parameterType = parameterTypes[mapperIndexs[i]];
                MapperDesc mapperDesc = new MapperDesc();
                mapperDescs[i++] = mapperDesc;
                mapperDesc.setParamHandler(paramHandlerResolver.resolve(pair.getValue()));
                mapperDesc.setMapper(mapperFieldName);
                mapperDesc.setSourceClass(declaredField.getType());
                mapperDesc.setTargetClass(parameterType.getTargetClass());
                mapperDesc.setTypes(parameterType.getTypes());
            } catch (NoSuchFieldException e) {
                throw new TranslationException("请填写正确的mapper字段名,传递多个参数时请使用数组", e);
            }
        }
        event.setMappers(mapperDescs);
    }

    @Override
    protected void setGroupKey(FieldTranslationEvent event, Field field, Mapping mapping) {
        String[] groupKey = mapping.groupKey();
        if (groupKey.length == 0) {
            Mapper[] mappers = mapping.mappers();
            groupKey = Arrays.stream(mappers)
                    .map(Mapper::value)
                    .flatMap(Arrays::stream)
                    .toArray(String[]::new);
        }
        event.setGroupKey(groupKey);
    }

    @Override
    protected void processAfterBuild(Field field, FieldTranslationEvent event) {

    }
}
