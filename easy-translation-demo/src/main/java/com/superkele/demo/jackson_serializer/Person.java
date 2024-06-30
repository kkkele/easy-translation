package com.superkele.demo.jackson_serializer;


import com.superkele.translation.annotation.Mapper;
import com.superkele.translation.annotation.Mapping;
import com.superkele.translation.annotation.constant.MappingStrategy;
import com.superkele.translation.annotation.constant.TranslateTiming;
import com.superkele.translation.boot.annotation.Translator;
import com.superkele.translation.extension.serialize.jackson.JsonMapping;
import lombok.Data;

import java.util.Random;

@Data
public class Person {

    private Integer personId;

    private String personName;

    private Integer sex = new Random().nextInt(1) + 1;

    @JsonMapping(translator = "getSexCode", mappers = @Mapper("sex"))
    private String sexCode;

    private Integer deptId = new Random().nextInt(10);

    @JsonMapping(translator = "getDeptById", mappers = @Mapper("deptId"))
    private Dept dept;

    @Translator("getSexCode")
    public static String getSexCode(Integer sex) {
        return sex == 1 ? "男" : "女";
    }

    @Translator("getPersonById")
    public static Person getPersonById(Integer personId) {
        Person person = new Person();
        person.setPersonId(personId);
        person.setPersonName("personName" + personId);
        person.setDeptId(personId);
        person.setDept(Dept.getDeptById(personId));
        return person;
    }
}
