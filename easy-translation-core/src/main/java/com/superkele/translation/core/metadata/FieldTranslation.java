package com.superkele.translation.core.metadata;

import lombok.Data;

import java.util.Map;

@Data
public class FieldTranslation {

    /**
     * 使用after回调触发的event
     * short为2个字节，16位，即最大(11111111)可支持16个翻译事件后触发回调
     */
    private Map<Short, FieldTranslationEvent[]> afterEventMaskMap;

    /**
     * 使用sort顺序触发的event
     */
    private FieldTranslationEvent[] sortEvents;

}
