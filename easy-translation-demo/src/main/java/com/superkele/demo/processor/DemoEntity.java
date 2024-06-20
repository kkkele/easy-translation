package com.superkele.demo.processor;


import com.superkele.translation.annotation.Mapping;
import com.superkele.translation.annotation.TransMapper;
import com.superkele.translation.boot.annotation.Translator;
import lombok.Data;

@Data
public class DemoEntity {

    @Mapping(translator = "demo_getName", sort = -1)
    private String var0;

    @Mapping(translator = "demo_getName1", mapper = "var0")
    private String var1;

    @Mapping(translator = "demo_getName2", mapper = "var0", other = "1")
    private String var2;

    @Mapping(translator = "demo_getName3", mapper = "var0", other = {"程序员", "1"})
    private String var3;

    @Mapping(translator = "demo_getName4", mapper = "var0", other = {"程序员", "1"})
    private String var4;

    private Integer sort;

    @Mapping(translator = "demo_getName5", mapper = {"var0", "sort"}, other = "程序员")
    private String var5;

    @Translator("demo_getName")
    public static String getName() {
        return "小明";
    }

    @Translator("demo_getName1")
    public static String getName1(String name) {
        return "超级" + name;
    }

    @Translator("demo_getName2")
    public static String getName2(String name, Integer sort) {
        return "天下第" + sort + "! => " + name;
    }

    @Translator("demo_getName3")
    public static String getName3(String name, String professional, Integer sort) {
        return "天下第" + sort + professional + "! => " + name;
    }

    @Translator("demo_getName4")
    public static String getName4(String professional, @TransMapper String name, Integer sort) {
        return "天下第" + sort + professional + "! => " + name;
    }

    @Translator("demo_getName5")
    public static String getName5(String professional, @TransMapper String name, @TransMapper Integer sort) {
        return "天下第" + sort + professional + "! => " + name;
    }
}
