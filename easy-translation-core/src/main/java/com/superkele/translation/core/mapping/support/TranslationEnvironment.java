package com.superkele.translation.core.mapping.support;


import com.superkele.translation.core.metadata.FieldTranslationEvent;
import lombok.Data;

import java.util.Map;

@Data
public class TranslationEnvironment {

    private Map<String,Object> cache;

    private FieldTranslationEvent event;
}
