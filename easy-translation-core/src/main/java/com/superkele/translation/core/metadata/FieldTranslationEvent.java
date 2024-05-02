package com.superkele.translation.core.metadata;

import java.util.function.Consumer;

public class FieldTranslationEvent {

    private String fieldName;

    private Consumer<String> translatorConsumer;

    private short eventIndex;

    private String[] mapper;

    private String[] other;

    private boolean notNullMapping;
}
