package com.superkele.translation.test.util.entity;


import com.superkele.translation.annotation.Mapping;
import lombok.Data;

@Data
public class Book {

    private Long id;

    @Mapping(translator = "bookNameTranslator",mapper = "id")
    private String name;

    private Student student;
}


 class Student {
    Long id;
}