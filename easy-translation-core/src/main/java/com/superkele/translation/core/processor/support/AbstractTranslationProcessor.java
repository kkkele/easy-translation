package com.superkele.translation.core.processor.support;


import com.superkele.translation.annotation.UnpackingHandler;
import com.superkele.translation.annotation.bean.BeanDescription;
import com.superkele.translation.core.util.ReflectUtils;
import com.superkele.translation.core.util.Singleton;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.superkele.translation.annotation.UnpackingHandler.*;

public abstract class AbstractTranslationProcessor extends FilterTranslationProcessor {


    @Override
    public void process(Object obj, Class<?> type, String field, boolean async, Class<? extends UnpackingHandler> listTypeHandlerClazz) {
        Object targetObj = ReflectUtils.invokeGetter(obj, field);
        UnpackingHandler listTypeHandler = Singleton.get(listTypeHandlerClazz);
        int unpackingType = listTypeHandler.unpackingType(targetObj);
        if (unpackingType > OBJECT_TYPE) {
            List<BeanDescription> unpacking = null;
            switch (unpackingType) {
                case COLLECTION_TYPE:
                    unpacking = listTypeHandler.unpackingCollection((Collection) targetObj, type);
                    break;
                case MAP_TYPE:
                    unpacking = listTypeHandler.unpackingMap((Map) targetObj, type);
                    break;
                case ARRAY_TYPE:
                    unpacking = listTypeHandler.unpackingArray((Object[]) targetObj, type);
                    break;
                case OTHER_TYPE:
                    unpacking = listTypeHandler.unpackingOther(targetObj, type);
                    break;
                default:
                    throw new RuntimeException("暂不支持的解包类型");
            }
            processBatch(unpacking, async);
            return;
        }
        if (type.equals(Object.class)) {
            process(targetObj);
        } else {
            process(targetObj, type);
        }

    }
}
