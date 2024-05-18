package com.superkele.demo.util;

import com.superkele.translation.annotation.TransMapper;
import com.superkele.translation.annotation.Translation;
import com.superkele.translation.boot.annotation.Translator;

public class SentenceUtil {


    @Translator("introduce")
    public static String getSentence(@TransMapper Integer id,@TransMapper String name) {
        return "你好，我的名字是" + name + ",我的编号为" + id;
    }
}
