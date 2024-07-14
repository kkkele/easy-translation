package com.superkele.translation.core.invoker.support;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public abstract class AbstractAutoNoticeInvokeBeanFactory implements ConfigurableInvokeBeanFactory {

    List<Consumer<ConfigurableInvokeBeanFactory>> listeners = new CopyOnWriteArrayList<>();
    private boolean active = false;

    @Override
    public void refresh() {
        active = true;
        notice();
    }

    @Override
    public void notice() {
        listeners.forEach(listener -> listener.accept(this));
    }

    @Override
    public void addListener(Consumer<ConfigurableInvokeBeanFactory> action) {
        listeners.add(action);
        if (active) {
            action.accept(this);
        }
    }
}
