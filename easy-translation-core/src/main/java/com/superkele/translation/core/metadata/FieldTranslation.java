package com.superkele.translation.core.metadata;

import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Data
public class FieldTranslation {

    /**
     * byte为2个字节，16位，即最大(11111111)可支持16个翻译事件后触发回调
     */
    private byte[] afterEvents;

    /**
     * 使用after回调触发的event
     */
    private Map<Short, FieldTranslationEvent[]> afterEventMap;

    /**
     * 使用sort顺序触发的event
     */
    private List<FieldTranslationEvent> sortEvents;

}
