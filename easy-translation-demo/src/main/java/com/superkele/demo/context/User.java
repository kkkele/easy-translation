package com.superkele.demo.context;

import com.superkele.translation.annotation.Mapping;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class User {
    private Integer id;
    private String name;
    private Integer age;
}
