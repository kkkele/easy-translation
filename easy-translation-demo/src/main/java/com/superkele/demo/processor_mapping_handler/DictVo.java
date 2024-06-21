package com.superkele.demo.processor_mapping_handler;

import com.superkele.translation.annotation.Mapping;
import com.superkele.translation.annotation.TransMapper;
import com.superkele.translation.boot.annotation.Translator;
import lombok.Data;

@Data
public class DictVo {

    private String dictType;

    private Integer dictCode;

    @Mapping(translator = "getDictValue", mapper = {"dictType", "dictCode"})
    private String dictValue;

    @Translator("getDictValue")
    public static String convertToValue(@TransMapper String dictType, @TransMapper Integer dictCode) {
        switch (dictType) {
            case "sex":
                switch (dictCode) {
                    case 0:
                        return "男";
                    case 1:
                        return "女";
                    default:
                        return "未知";
                }
            case "status":
                switch (dictCode) {
                    case 0:
                        return "正常";
                    case 1:
                        return "封禁";
                    default:
                        return "未知";
                }
        }
        return "未知";
    }
}
