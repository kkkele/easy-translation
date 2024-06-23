package com.superkele.translation.core.test.mapping;


import com.superkele.translation.annotation.Mapping;
import lombok.Data;

@Data
public class TestDemo {

    @Mapping
    private String name;

    @Mapping
    private Integer id;
}
