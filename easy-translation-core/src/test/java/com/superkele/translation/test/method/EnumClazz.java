package com.superkele.translation.test.method;


import com.superkele.translation.annotation.TransMapper;
import com.superkele.translation.annotation.TransValue;
import com.superkele.translation.annotation.Translation;

@Translation
public enum EnumClazz {


    A(1, "a"),
    B(2, "b"),
    C(3, "c");

    @TransMapper
    private int id;

    @TransValue
    private String name;

    EnumClazz(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
