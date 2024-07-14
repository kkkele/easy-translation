package com.superkele.translation.boot.invoker;

import cn.hutool.core.exceptions.UtilException;
import com.superkele.translation.core.invoker.support.AbstractAutoNoticeInvokeBeanFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
public class SpringInvokeBeanFactory extends AbstractAutoNoticeInvokeBeanFactory implements BeanFactoryPostProcessor, ApplicationContextAware, CommandLineRunner {

    private ConfigurableListableBeanFactory beanFactory;

    private ApplicationContext applicationContext;

    @Override
    public <T> T getBean(String beanName) {
        return (T) getBeanFactory().getBean(beanName);
    }

    @Override
    public <T> T getBean(Class<?> clazz) {
        return (T) getBeanFactory().getBean(clazz);
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> clazz) {
        return getBeanFactory().getBeansOfType(clazz);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        this.beanFactory = configurableListableBeanFactory;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public ListableBeanFactory getBeanFactory() {
        final ListableBeanFactory factory = null == beanFactory ? applicationContext : beanFactory;
        if (null == factory) {
            throw new UtilException("No ConfigurableListableBeanFactory or ApplicationContext injected, maybe not in the Spring environment?");
        }
        return factory;
    }

    @Override
    public void run(String... args) throws Exception {
        refresh();
    }
}
