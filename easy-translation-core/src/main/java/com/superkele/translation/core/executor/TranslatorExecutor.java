package com.superkele.translation.core.executor;

import java.util.List;

public interface TranslatorExecutor {

    Object execute(Object source);

    Object execute(List<Object> sourceList);

    void executeAsync(Object source);

    void executeAsync(List<Object> sourceList);
}
