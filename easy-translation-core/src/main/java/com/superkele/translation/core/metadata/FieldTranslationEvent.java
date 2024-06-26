package com.superkele.translation.core.metadata;

import com.superkele.translation.annotation.RefTranslation;
import com.superkele.translation.core.annotation.FieldTranslationInvoker;
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
     * 用于修改字段的函数
     */
    private FieldTranslationInvoker fieldTranslationInvoker;

    /**
     * 属性名称
     */
    private String propertyName;

    /**
     * 是否为关联翻译字段
     */
    private RefTranslation refTranslation;

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
    private FieldTranslationEvent[] afterEvents;
}
