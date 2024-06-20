package com.superkele.translation.core.mapping;

import com.superkele.translation.core.metadata.FieldTranslationEvent;
import com.superkele.translation.core.property.PropertyHandler;
import com.superkele.translation.core.translator.Translator;

import java.util.List;
import java.util.Map;

/**
 * 映射处理器
 * 不同的MappingHandler会根据 #waitPreEventWhenBatch 决定它有没有批处理的能力
 * 具有批处理能力的MappingHandler可以在处理集合时聚合所有对象的mapper参数列表，然后对结果进行映射
 * 不具有批处理能力的MappingHandler在处理集合时，会对每个对象进行单独的翻译，当然，它也具有改变参数形式和结果处理的能力
 * 它将被AbstractFieldTranslationHandler调用控制，所以无需关心是如何做到批处理的。
 * @see com.superkele.translation.core.processor.support.AbstractFieldTranslationHandler
 */
public interface MappingHandler {

    PropertyHandler getPropertyHandler();

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
