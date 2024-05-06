package com.superkele.translation.core.annotation.support;

import cn.hutool.core.util.StrUtil;
import com.superkele.translation.annotation.Mapping;
import com.superkele.translation.core.annotation.FieldTranslationInvoker;
import com.superkele.translation.core.annotation.MappingHandler;
import com.superkele.translation.core.translator.factory.TransExecutorFactory;
import com.superkele.translation.core.translator.handle.TranslateExecutor;
import com.superkele.translation.core.util.Assert;
import com.superkele.translation.core.util.ReflectUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;

public class DefaultMappingHandler implements MappingHandler {

    private final TransExecutorFactory translatorFactory;

    public DefaultMappingHandler(TransExecutorFactory translatorFactory) {
        this.translatorFactory = translatorFactory;
    }

    @Override
    public FieldTranslationInvoker convert(Field declaringField, Mapping mapping) {
        Assert.isTrue(translatorFactory.containsTranslator(mapping.translator()), "translator not found: " + mapping.translator());
        TranslateExecutor executor = translatorFactory.findExecutor(mapping.translator());
        String uniqueName = StrUtil.join(",", mapping.translator(), mapping.mapper(), mapping.other());
        return (obj, cacheResSupplier, callback) -> {
            if (obj == null) {
                return null;
            }
            //当字段不为空也映射关闭时，判断字段情况，当不为空时不进行处理
            if (!mapping.notNullMapping()) {
                if (ReflectUtils.invokeGetter(obj, declaringField.getName()) != null) {
                    return obj;
                }
            }
            //加载缓存中的值，如果有的话，使用缓存中提供的值
            if (cacheResSupplier != null) {
                Object cacheRes = cacheResSupplier.apply(uniqueName);
                if (cacheRes != null) {
                    ReflectUtils.invokeSetter(obj, declaringField.getName(), ReflectUtils.invokeGetter(cacheRes, mapping.receive()));
                    return obj;
                }
            }
            String[] mapper = mapping.mapper();
            String[] other = mapping.other();
            int mapperLength = mapper.length;
            int otherLength = other.length;
            //组建参数
            Object[] args = new Object[MAX_TRANSLATOR_PARAM_LEN];
            for (int i = 0; i < mapperLength; i++) {
                if (StringUtils.isNotBlank(mapper[i])) {
                    args[i] = ReflectUtils.invokeGetter(obj, mapper[i]);
                }
            }
            int j = 0;
            int i = mapperLength;
            while (i < mapperLength + otherLength) {
                args[i++] = other[j++];
            }
            //翻译值
            Object mappingValue = executor.execute(args);
            if (mappingValue != null) {
                callback.accept(uniqueName, mappingValue);
            }
            //set注入
            if (mappingValue != null) {
                ReflectUtils.invokeSetter(obj, declaringField.getName(), ReflectUtils.invokeGetter(mappingValue, mapping.receive()));
            }
            return obj;
        };
    }

}
