package com.superkele.translation.core.handler;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

public interface TranslationProcessor {
    void process(Object obj);

    void process(List<Object> obj);

    void process(Object obj, ThreadPoolExecutor executor);

    void process(List<Object> obj,ThreadPoolExecutor executor);

    void processAsync(Object obj);

    void processAsync(List<Object> obj);


}
