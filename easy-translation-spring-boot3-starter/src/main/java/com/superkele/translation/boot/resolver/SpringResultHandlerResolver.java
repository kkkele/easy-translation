package com.superkele.translation.boot.resolver;

import cn.hutool.core.util.StrUtil;
import com.superkele.translation.core.exception.TranslationException;
import com.superkele.translation.core.mapping.ResultHandler;
import com.superkele.translation.core.mapping.ResultHandlerResolver;
import com.superkele.translation.core.mapping.support.DefaultResultHandler;
import com.superkele.translation.core.util.Singleton;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


public class SpringResultHandlerResolver implements ResultHandlerResolver, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public ResultHandler resolve(String name) {
        if (StrUtil.isBlank(name)){
            return Singleton.get(DefaultResultHandler.class);
        }
        if (StrUtil.startWith(name, "@")) {
            Object bean = applicationContext.getBean(name.substring(1));
            if (bean instanceof ResultHandler) {
                return (ResultHandler) bean;
            }
            throw new TranslationException("该bean" + name + "并非ResultHandler类型的Bean");
        } else {
            ResultHandler paramHandler = Singleton.get(name);
            if (paramHandler instanceof ResultHandler) {
                return paramHandler;
            }
            throw new TranslationException("请为ResultHandler提供正确的全类名或使用 `@`+`beanName`的方式传递。\n 例如:@defaultParamHandler 或者 com.superkele.translation.core.mapping.support.DefaultResultHandler");
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
