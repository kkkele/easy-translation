package com.superkele.translation.core.executor;

import java.util.List;

public interface  ITranslatorExecutor {

    Object execute(Object source);

    Object execute(List<Object> sourceList);

    void executeAsync(Object source);

    void executeAsync(List<Object> sourceList);
}
