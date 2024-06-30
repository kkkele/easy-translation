package com.superkele.demo.context;

import com.superkele.translation.annotation.TransMapper;
import com.superkele.translation.annotation.TransValue;
import com.superkele.translation.annotation.Translation;
import lombok.Getter;

@Translation(name = "getStatus")
@Getter
public enum Status {
    NORMAL(0, "正常"),

    DISABLED(1, "禁用");


    @TransMapper
    private final Integer code;

    @TransValue
    private final String desc;

    Status(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
