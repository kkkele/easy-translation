package com.superkele.translation.core.processor.support;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.superkele.translation.annotation.RefTranslation;
import com.superkele.translation.core.config.Config;
import com.superkele.translation.core.mapping.MappingHandler;
import com.superkele.translation.core.metadata.FieldTranslation;
import com.superkele.translation.core.metadata.FieldTranslationEvent;
import com.superkele.translation.core.processor.TranslationProcessor;
import com.superkele.translation.core.thread.ContextPasser;
import com.superkele.translation.core.translator.factory.TranslatorFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class OnceFieldTranslationHandler extends AbstractFieldTranslationHandler {

    private final TranslatorFactory translatorFactory;

    public OnceFieldTranslationHandler(FieldTranslation fieldTranslation, List<Object> sources, TranslatorFactory translatorFactory) {
        super(fieldTranslation, sources);
        this.translatorFactory = translatorFactory;
    }

    @Override
    protected TranslatorFactory getTranslatorFactory() {
        return translatorFactory;
    }
}
