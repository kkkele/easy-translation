package com.superkele.translation.core.metadata;

import com.superkele.translation.core.annotation.FieldTranslationInvoker;
import lombok.Data;

@Data
public class FieldTranslationEvent {

    /**
     * 事件
     */
    private short eventValue;

    private FieldTranslationInvoker  fieldTranslationInvoker;


}
