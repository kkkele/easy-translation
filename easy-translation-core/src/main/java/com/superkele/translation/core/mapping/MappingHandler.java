package com.superkele.translation.core.mapping;

import com.superkele.translation.core.metadata.FieldTranslationEvent;
import com.superkele.translation.core.property.PropertyHandler;
import com.superkele.translation.core.translator.Translator;

import java.util.List;

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
    Object handleBatch(List<Object> collection, FieldTranslationEvent event, Translator translator);

    /**
     * 当执行对单个对象处理 或者 waitPreEventWhenBatch为false时，调用该方法
     *
     * @param obj
     * @param event
     * @param translator
     * @return
     */
    Object handle(Object obj, FieldTranslationEvent event, Translator translator);

}
