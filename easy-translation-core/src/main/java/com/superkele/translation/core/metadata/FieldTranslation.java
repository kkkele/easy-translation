package com.superkele.translation.core.metadata;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class FieldTranslation {

    /**
     * 需要执行的size
     */
    private int consumeSize;

    /**
     * 使用sort顺序触发的event
     */
    private FieldTranslationEvent[] sortEvents;

    private boolean hasSameInvoker;
}
