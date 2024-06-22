package com.superkele.translation.core.metadata;

import com.superkele.translation.annotation.NullPointerExceptionHandler;
import com.superkele.translation.annotation.RefTranslation;
import com.superkele.translation.core.mapping.MappingHandler;
import com.superkele.translation.core.translator.Translator;
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

    private TranslationResDesc translationResDesc;

    /**
     * 用于映射的处理器
     */
    private MappingHandler mappingHandler;

    /**
     * 属性名称
     */
    private String propertyName;

    /**
     * 是否可以复用结果
     */
    private boolean cacheEnable;

    /**
     * 复用结果的关键key
     */
    private String cacheKey;

    /**
     * 映射字段对应的类的属性
     * 例如：@Mapping中将 mapper="stuId:id" 则mapper=“stuId",mapperOriginField="id",如果是从一个结果集中以属性分组时，将会使用该字段
     * 具体的分割方法，可以通过设置config来配置
     * @see com.superkele.translation.core.config.Config#mapperSplitExecutor
     */
    private String[] groupKey;

    /**
     * 映射的字段
     */
    private MapperDesc[] mapper;

    /**
     * 其他字段
     */
    private String[] other;

    /**
     * 接收参数
     */
    private String receive;

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
