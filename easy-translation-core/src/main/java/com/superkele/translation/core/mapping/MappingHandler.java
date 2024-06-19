package com.superkele.translation.core.mapping;

import com.superkele.translation.core.metadata.FieldTranslationEvent;
import com.superkele.translation.core.property.PropertyHandler;
import com.superkele.translation.core.translator.Translator;

import java.util.List;
import java.util.Map;

public interface MappingHandler {


    /**
     * 当批处理时，是否等待前置事件执行完再执行该事件
     *
     * @return
     */
    default boolean waitPreEventWhenBatch() {
        return false;
    }

    /**
     * 当执行批处理时，且 waitPreEventWhenBatch为true时，调用该方法
     *
     * @param collection
     * @param event
     * @param translator
     * @return
     */
    default Object handleBatch(List<Object> collection, FieldTranslationEvent event, Translator translator) {
        return handleBatch(collection, event, translator, null);
    }

    /**
     * 当翻译结果可以复用，且 执行批处理时， waitPreEventWhenBatch为true时，调用该方法
     *
     * @param collection
     * @param event
     * @param translator
     * @return
     */
    Object handleBatch(List<Object> collection, FieldTranslationEvent event, Translator translator, Map<String, Object> cache);

    /**
     * 当执行对单个对象处理 或者 waitPreEventWhenBatch为false时，调用该方法
     *
     * @param obj        处理对象
     * @param event      对象对应的事件
     * @param translator 翻译器
     * @return
     */
    default Object handle(Object obj, FieldTranslationEvent event, Translator translator) {
        return handle(obj, event, translator, null);
    }

    /**
     * 当翻译结果可以复用，且 当执行对单个对象处理 或者 waitPreEventWhenBatch为false时，调用该方法
     *
     * @param obj        处理对象
     * @param event      对象对应的事件
     * @param translator 翻译器
     * @param cache      结果缓存
     * @return
     */
    Object handle(Object obj, FieldTranslationEvent event, Translator translator, Map<String, Object> cache);

}
