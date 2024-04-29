package com.superkele.translation.test.method;

import com.superkele.translation.annotation.Translation;

public class StaticMethodClazz {


    @Translation(name = "getById")
    public static String getById(Long id){
        return "StaticMethodClazz"+id;
    }
}
