package com.superkele.translation.core.annotation;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 字段翻译器
 */
public interface FieldTranslationInvoker {

    /**
     * 激活方法，开始翻译字段
     *
     * @param translationTarget 翻译目标
     * @param cacheResSupplier  缓存的对象
     * @param resultCallback    翻译激活的方法结果回调 T:唯一translator,V:translator 的执行结果
     * @return
     */
    Object invoke(Object translationTarget, Function<String, Object> cacheResSupplier, BiConsumer<String, Object> resultCallback);

    default Object invoke(Object translationTarget, BiConsumer<String, Object> resultCallback) {
        return invoke(translationTarget, null, resultCallback);
    }

    default Object invoke(Object translationTarget) {
        return invoke(translationTarget, null, null);
    }
}
