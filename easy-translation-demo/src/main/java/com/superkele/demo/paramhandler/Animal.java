package com.superkele.demo.paramhandler;

import com.superkele.translation.annotation.Mapper;
import com.superkele.translation.annotation.Mapping;
import com.superkele.translation.boot.annotation.Translator;
import lombok.Data;

@Data
public class Animal {

    private Integer code1;

    @Mapping(translator = "getAnimalNameByCode",mappers = @Mapper("code1"))
    private String name1;

    private Integer code2;

    @Mapping(translator = "getAnimalNameByCode",mappers = @Mapper(value = "code2",paramHandler = "@intToStringParamHandler"))
    private String name2;

    @Translator("getAnimalNameByCode")
    public static String getByCode(String code) {
        switch (code) {
            case "1":
                return "cat";
            case "2":
                return "dog";
            default:
                return "unknown";
        }
    }
}
