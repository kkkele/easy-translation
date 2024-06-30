package com.superkele.demo.paramhandler;

import com.superkele.translation.annotation.Mapper;
import com.superkele.translation.annotation.Mapping;
import com.superkele.translation.annotation.TransMapper;
import com.superkele.translation.boot.annotation.Translator;
import lombok.Data;

import java.util.List;

@Data
public class Animal2 {

    private String var1;

    private String var2;

    @Mapping(translator = "getNameByVar1AndVar2", mappers = @Mapper(value = {"var1", "var2"}))
    private String var3;

    @Mapping(translator = "getNameByVar1AndVar2", mappers = {
            @Mapper(value = "var1"),
            @Mapper(value = "var2", paramHandler = "@stringToListParamHandler")}
    )
    private String var4;


    @Translator("getNameByVar1AndVar2")
    public static String getNameByVar1AndVar2(@TransMapper Integer var1,@TransMapper List<String> var2) {
        return var1 + "=>" + var2;
    }
}
