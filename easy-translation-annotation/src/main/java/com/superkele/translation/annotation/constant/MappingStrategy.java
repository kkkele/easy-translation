package com.superkele.translation.annotation.constant;

public enum MappingStrategy {
    ONE_TO_ONE((obj,property,filedValue) -> {

    }),
    MANY_TO_MANY((obj,property,filedValue) -> {

    });


    private MappingHandler mappingHandler;

    MappingStrategy(MappingHandler mappingHandler) {
        this.mappingHandler = mappingHandler;
    }

    public MappingStrategy setMappingHandler(MappingHandler mappingHandler) {
        this.mappingHandler = mappingHandler;
        return this;
    }

    public void invoke(Object bean, String propertyName, Object filedValue){
        mappingHandler.setValue(bean,propertyName,filedValue);
    }
}
