package com.superkele.translation.core.test.mapping;


import com.superkele.translation.annotation.Mapper;
import com.superkele.translation.annotation.Mapping;
import lombok.Data;

@Data
public class TestDemo {

    private String name;

    @Mapping(mappers = @Mapper("name"))
    private Integer id;
}
