package com.superkele.translation.core.metadata;


import lombok.Data;

@Data
public class MapperDesc {

    private String mapper;

    private Class<?> sourceClass;
}
