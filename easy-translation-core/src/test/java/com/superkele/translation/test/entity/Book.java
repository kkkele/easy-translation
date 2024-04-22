package com.superkele.translation.test.entity;

import com.superkele.translation.annotation.Mapping;
import lombok.Data;


@Data
public class Book {

    private Long id;

    private String name;

    private Long studentId;

    @Mapping(translator = "studentTranslator",mapper = "studentId")
    private String studentName;

}
