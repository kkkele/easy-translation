package com.superkele.translation.core.async;

import com.superkele.translation.core.metadata.FieldTranslation;

import java.util.List;

/**
 * 任务执行链构造器
 * @description
 * 可自定义实现不同Mapped字段的翻译器执行顺序
 */
public interface TaskExecutionChainBuilder {

    List<FieldTranslation> build(Class<?> clazz);
}
