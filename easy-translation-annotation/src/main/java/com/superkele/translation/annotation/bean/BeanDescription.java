package com.superkele.translation.annotation.bean;


public class BeanDescription {

    private Object bean;

    private Class<?> clazz;

    public BeanDescription(Object bean, Class<?> clazz) {
        this.bean = bean;
        this.clazz = clazz;
    }

    public BeanDescription() {
    }

    public Object getBean() {
        return bean;
    }

    public BeanDescription setBean(Object bean) {
        this.bean = bean;
        return this;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public BeanDescription setClazz(Class<?> clazz) {
        this.clazz = clazz;
        return this;
    }
}
