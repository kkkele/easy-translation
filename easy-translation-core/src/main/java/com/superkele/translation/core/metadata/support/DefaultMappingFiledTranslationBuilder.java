package com.superkele.translation.core.metadata.support;

import cn.hutool.core.util.StrUtil;
import com.superkele.translation.annotation.Mapping;
import com.superkele.translation.annotation.MappingHandler;
import com.superkele.translation.core.exception.TranslationException;
import com.superkele.translation.core.mapping.ParamHandlerResolver;
import com.superkele.translation.core.mapping.ResultHandlerResolver;
import com.superkele.translation.core.metadata.FieldTranslationEvent;
import com.superkele.translation.core.metadata.MapperDesc;
import com.superkele.translation.core.metadata.ParamDesc;
import com.superkele.translation.core.translator.definition.ConfigurableTranslatorDefinitionFactory;
import com.superkele.translation.core.translator.definition.TranslatorDefinition;

import java.lang.reflect.Field;

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
    protected void buildParamHandler(FieldTranslationEvent event, Field field, Mapping mapping) {
        event.setParamHandler(paramHandlerResolver.resolve(mapping.mappingHandler().paramHandler()));
    }

    @Override
    protected void buildResultHandler(FieldTranslationEvent event, Field field, Mapping mapping) {
        event.setResultHandler(resultResolver.resolve(mapping.mappingHandler().resultHandler()));
    }

    @Override
    protected void buildMapper(FieldTranslationEvent event, Field field, Mapping mapping) {
        if (StrUtil.isBlank(mapping.translator())) {
            return;
        }
        TranslatorDefinition translatorDefinition = translatorDefinitionFactory.findTranslatorDefinition(mapping.translator());
        ParamDesc[] parameterTypes = translatorDefinition.getParameterTypes();
        //获取key的下标
        int[] mapperIndexs = translatorDefinition.getMapperIndex();
        int i = 0;
        String[] mapper = mapping.mapper();
        MapperDesc[] mapperDescs = new MapperDesc[mapper.length];
        Class<?> declaringClass = field.getDeclaringClass();
        for (String mapperFieldName : mapper) {
            try {
                Field declaredField = declaringClass.getDeclaredField(mapperFieldName);
                MapperDesc mapperDesc = new MapperDesc();
                mapperDesc.setMapper(mapperFieldName);
                mapperDesc.setSourceClass(declaredField.getType());
                ParamDesc parameterType = parameterTypes[mapperIndexs[i]];
                mapperDesc.setTargetClass(parameterType.getTargetClass());
                mapperDesc.setTypes(parameterType.getTypes());
                mapperDescs[i++] = mapperDesc;
            } catch (NoSuchFieldException e) {
                throw new TranslationException("请填写正确的mapper字段名,传递多个参数时请使用数组", e);
            }
        }
        event.setMapper(mapperDescs);
    }

    @Override
    protected void buildGroupKey(FieldTranslationEvent event, Field field, Mapping mapping) {
        MappingHandler mappingHandler = mapping.mappingHandler();
        String[] groupKey = mappingHandler.groupKey();
        if (groupKey.length == 0) {
            String[] mapper = mapping.mapper();
            event.setGroupKey(mapper);
        } else {
            event.setGroupKey(groupKey);
        }
    }

    @Override
    protected void processAfterBuild(Field field, FieldTranslationEvent event) {

    }
}
