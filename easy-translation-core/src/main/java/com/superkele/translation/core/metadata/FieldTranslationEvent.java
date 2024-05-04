package com.superkele.translation.core.metadata;

import com.superkele.translation.core.translator.handle.TranslateExecutor;
import com.superkele.translation.core.util.ReflectUtils;
import lombok.Data;

import java.util.function.Consumer;
import java.util.function.Predicate;

@Data
public class FieldTranslationEvent {

    /**
     * 字段名
     */
    private String fieldName;


    private TranslateExecutor translateExecutor;
    /**
     * 事件
     */
    private short event;

    /**
     * 翻译执行器调用者
     */
    private Predicate<Object> caller;
    /**
     * 映射字段
     */
    private String[] mapper;

    /**
     * 补充字段
     */
    private String[] other;

    /**
     * 不为Null时，也执行翻译
     */
    private boolean notNullMapping;

    public void translate(Object obj, Consumer<Short> eventConsumer) {
        if (!notNullMapping) {
            Object target = ReflectUtils.invokeGetter(obj, fieldName);
            if (target != null) {
                return;
            }
        }
        if (caller.test(obj)) {
            eventConsumer.accept(event);
        }
    }

}
