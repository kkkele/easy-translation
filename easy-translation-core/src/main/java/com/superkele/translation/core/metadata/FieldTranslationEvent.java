package com.superkele.translation.core.metadata;

import lombok.Data;

import java.util.function.Consumer;

@Data
public class FieldTranslationEvent {

    private String fieldName;

    private Consumer<FieldTranslationEvent> action;

    private short event;

    private String[] mapper;

    private String[] other;

    private boolean notNullMapping;
}
