package com.superkele.translation.boot.aware;

import com.superkele.translation.core.context.TransExecutorContext;
import com.superkele.translation.core.context.support.DefaultTransExecutorContext;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class EasyTranslationApplicationAware implements ApplicationContextAware {
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        TransExecutorContext transExecutorContext = applicationContext.getBean("defaultTransExecutorContext", TransExecutorContext.class);
        if (transExecutorContext != null) {
            if (transExecutorContext instanceof DefaultTransExecutorContext) {
                DefaultTransExecutorContext defaultTransExecutorContext = (DefaultTransExecutorContext) transExecutorContext;
                defaultTransExecutorContext.refresh();
            }
        }
    }
}
