package com.superkele.translation.core.executor;

import java.util.List;

public interface ITranslatorExecutor {

    void execute(Object source);

    void execute(List<Object> sourceList);

    void executeAsync(Object source);

    void executeAsync(List<Object> sourceList);
}
