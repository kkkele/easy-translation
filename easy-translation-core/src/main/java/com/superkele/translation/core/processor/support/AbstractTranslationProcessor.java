package com.superkele.translation.core.processor.support;


import com.superkele.translation.annotation.TranslationUnpackingHandler;
import com.superkele.translation.annotation.bean.BeanDescription;
import com.superkele.translation.core.util.HandlerUtil;
import com.superkele.translation.core.util.ReflectUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class AbstractTranslationProcessor extends FilterTranslationProcessor {

    @Override
    public void process(Object obj) {
        process(obj, obj.getClass());
    }

    @Override
    public void process(Object obj, Class<?> type, String field, boolean async, Class<? extends TranslationUnpackingHandler> listTypeHandlerClazz) {
        Object targetObj = ReflectUtils.invokeGetter(obj, field);
        TranslationUnpackingHandler listTypeHandler = HandlerUtil.getInstance(listTypeHandlerClazz);
        int unpackingType = listTypeHandler.unpackingType(targetObj);
        if (unpackingType > 0) {
            List<BeanDescription> unpacking = null;
            switch (unpackingType) {
                case 1:
                    unpacking = listTypeHandler.unpackingCollection((Collection) targetObj, type);
                    break;
                case 2:
                    unpacking = listTypeHandler.unpackingMap((Map) targetObj, type);
                    break;
                case 3:
                    unpacking = listTypeHandler.unpackingArray((Object[]) targetObj, type);
                    break;
                case 4:
                    unpacking = listTypeHandler.unpackingOther(targetObj, type);
                    break;
                default:
                    throw new RuntimeException("暂不支持的解包类型");
            }
            if (async) {
                processListAsync(unpacking);
            } else {
                processList(unpacking);
            }
            return;
        }
        if (type.equals(Object.class)) {
            process(targetObj);
        } else {
            process(targetObj, type);
        }

    }
}
