package com.superkele.translation.core.event;

import com.superkele.translation.core.context.ConfigurableTranslatorContext;
import lombok.Data;

@Data
public class TranslatorContextEvent {

    public static String LOAD_EXTRA = "load_extra";

    public static String REFRESH = "refresh";

    private String eventType = REFRESH;

    private String[] basePath;

    private ConfigurableTranslatorContext context;
}
