package com.superkele.translation.core.annotation.support;

import cn.hutool.core.util.StrUtil;
import com.superkele.translation.annotation.Mapping;
import com.superkele.translation.annotation.NullPointerExceptionHandler;
import com.superkele.translation.core.annotation.FieldTranslationInvoker;
import com.superkele.translation.core.annotation.MappingHandler;
import com.superkele.translation.core.property.PropertyGetter;
import com.superkele.translation.core.property.PropertySetter;
import com.superkele.translation.core.translator.Translator;
import com.superkele.translation.core.translator.factory.TranslatorFactory;
import com.superkele.translation.core.util.Assert;
import com.superkele.translation.core.util.HandlerUtil;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;

public abstract class AbstractMappingHandler implements MappingHandler {

    private final TranslatorFactory translatorFactory;

    public AbstractMappingHandler(TranslatorFactory translatorFactory) {
        this.translatorFactory = translatorFactory;
    }

    protected abstract PropertyGetter getPropertyGetter();

    protected abstract PropertySetter getPropertySetter();

    @Override
    public FieldTranslationInvoker convert(Field declaringField, Mapping mapping) {
        Assert.isTrue(translatorFactory.containsTranslator(mapping.translator()), "translator not found: " + mapping.translator());
        String uniqueName = StrUtil.join(",", mapping.translator(), mapping.mapper(), mapping.other());
        return (obj, cacheResSupplier, callback) -> {
            if (obj == null) {
                return null;
            }
            //当字段不为空也映射关闭时，判断字段情况，当不为空时不进行处理
            if (!mapping.notNullMapping()) {
                if (getPropertyGetter().invokeGetter(obj, declaringField.getName()) != null) {
                    return obj;
                }
            }
            Translator translator = translatorFactory.findTranslator(mapping.translator());
            //加载缓存中的值，如果有的话，使用缓存中提供的值
            if (cacheResSupplier != null) {
                Object cacheRes = cacheResSupplier.apply(uniqueName);
                if (cacheRes != null) {
                    Object setterValue = getPropertyGetter().invokeGetter(cacheRes, mapping.receive());
                    getPropertySetter().invokeSetter(obj, declaringField.getName(), setterValue);
                    return obj;
                }
            }
            String[] mapper = mapping.mapper();
            String[] other = mapping.other();
            int mapperLength = mapper.length;
            int otherLength = other.length;
            //组建参数
            Object[] args = new Object[16];
            for (int i = 0; i < mapperLength; i++) {
                if (StringUtils.isNotBlank(mapper[i])) {
                    try {
                        args[i] = getPropertyGetter().invokeGetter(obj, mapper[i]);
                    } catch (NullPointerException e) {
                        NullPointerExceptionHandler nullPointerExceptionHandler = HandlerUtil.getNullPointerExceptionHandler(mapping.nullPointerHandler());
                        nullPointerExceptionHandler.handle(e);
                        return obj;
                    }
                }
            }
            int j = 0;
            int i = mapperLength;
            while (i < mapperLength + otherLength) {
                args[i++] = other[j++];
            }
            //翻译值
            Object mappingValue = translator.doTranslate(args);
            if (callback != null) {
                callback.accept(uniqueName, mappingValue);
            }
            //set注入
            if (mappingValue != null) {
                getPropertySetter().invokeSetter(obj, declaringField.getName(), getPropertyGetter().invokeGetter(mappingValue, mapping.receive()));
            }
            return obj;
        };
    }

}
