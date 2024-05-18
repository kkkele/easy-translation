package com.superkele.demo.util;

import com.superkele.translation.annotation.TransMapper;
import com.superkele.translation.boot.annotation.Translator;

public class DictUtil {


    @Translator("dict")
    public static String getDictValue(Integer code, String dictType) {
        switch (dictType) {
            case "sex":
                switch (code) {
                    case 1:
                        return "男";
                    case 2:
                        return "女";
                    default:
                        return "未知";
                }
            case "status":
                switch (code) {
                    case 0:
                        return "正常";
                    case 1:
                        return "停用";
                    default:
                        return "未知";
                }
            default:
                return "未知";
        }
    }

    @Translator("str")
    public static String getStr(Boolean filter, @TransMapper Integer id) {
        if (!filter) {
            return "不给你看";
        }
        return "给你看==>" + id;
    }
}
