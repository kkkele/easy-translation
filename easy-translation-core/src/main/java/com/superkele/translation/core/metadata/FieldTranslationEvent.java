package com.superkele.translation.core.metadata;

import com.superkele.translation.annotation.NullPointerExceptionHandler;
import com.superkele.translation.annotation.RefTranslation;
import com.superkele.translation.annotation.constant.MappingStrategy;
import com.superkele.translation.core.mapping.ParamHandler;
import com.superkele.translation.core.mapping.ResultHandler;
import lombok.Data;


/**
 * 字段翻译事件，该类的不同对象可以翻译不同的字段
 */
@Data
public class FieldTranslationEvent {

    /**
     * 事件值
     */
    private short eventValue;

    /**
     * 翻译器名称
     */
    private String translator;

    /**
     * tubs个策略
     */
    private MappingStrategy mappingStrategy;

    /**
     * 属性名称
     */
    private String propertyName;

    /**
     * 是否可以复用结果
     */
    private boolean cacheEnable;

    /**
     * 映射字段对应的类的属性
     */
    private String[] groupKey;

    /**
     * 映射的字段
     */
    private MapperDesc[] mappers;

    /**
     * 其他字段
     */
    private String[] others;

    /**
     * 接收参数
     */
    private String receive;

    /**
     * 结果处理器
     */
    private ResultHandler resultHandler;

    /**
     * 是否为关联翻译字段
     */
    private RefTranslation refTranslation;

    /**
     * 参数不为空时是否映射
     */
    private boolean notNullMapping;

    /**
     * 空指针异常处理器
     */
    private NullPointerExceptionHandler nullPointerExceptionHandler;

    /**
     * 是否异步
     */
    private boolean async;

    /**
     * 需要用于触发的事件掩码
     * 使用after回调触发的event
     * short为2个字节，16位，即最大(11111111)可支持16个翻译事件后触发回调
     */
    private short triggerMask;

    /**
     * 在此事件更新后发生的回调事件
     */
    private FieldTranslationEvent[] activeEvents;
}
