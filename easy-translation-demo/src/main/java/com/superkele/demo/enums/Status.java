package com.superkele.demo.enums;


import com.superkele.translation.annotation.TransMapper;
import com.superkele.translation.annotation.TransValue;
import com.superkele.translation.boot.annotation.Translator;

@Translator("getStatus")
public enum Status {

    UNKNOWN(0, "未知"),

    NORMAL(1, "正常"),

    DELETED(2, "删除");

    @TransMapper
    private int code;

    @TransValue
    private String desc;

    Status(int code, String desc)
    {
        this.code = code;
        this.desc = desc;
    }

    public int getCode()
    {
        return code;
    }

    public String getDesc()
    {
        return desc;
   }
}
