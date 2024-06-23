package com.superkele.translation.boot.resolver;

import cn.hutool.core.util.StrUtil;
import com.superkele.translation.core.exception.TranslationException;
import com.superkele.translation.core.mapping.ParamHandler;
import com.superkele.translation.core.mapping.ParamHandlerResolver;
import com.superkele.translation.core.mapping.support.DefaultParamHandler;
import com.superkele.translation.core.util.Singleton;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringParamHandlerResolver implements ParamHandlerResolver, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public ParamHandler resolve(String name) {
        if (StrUtil.isBlank(name)){
            return Singleton.get(DefaultParamHandler.class);
        }
        if (StrUtil.startWith(name, "@")) {
            Object bean = applicationContext.getBean(name.substring(1));
            if (bean instanceof ParamHandler) {
                return (ParamHandler) bean;
            }
            throw new TranslationException("该bean" + name + "并非ParamHandler类型的Bean");
        } else {
            ParamHandler paramHandler = Singleton.get(name);
            if (paramHandler instanceof ParamHandler) {
                return paramHandler;
            }
            throw new TranslationException("请为ParamHandler提供正确的全类名或使用 `@`+`beanName`的方式传递ParamHandler\n 例如:@defaultParamHandler 或者 com.superkele.translation.core.mapping.support.DefaultParamHandler");
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
