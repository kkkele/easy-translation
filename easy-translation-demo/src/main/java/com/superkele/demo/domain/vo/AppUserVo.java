package com.superkele.demo.domain.vo;

import com.superkele.translation.annotation.Mapping;
import com.superkele.translation.annotation.constant.TranslateTiming;
import lombok.Data;

@Data
public class AppUserVo {

    private Integer userId;

    private Integer sexCode;

    @Mapping(translator = "dict", mapper = "sexCode",other = "sex",timing = TranslateTiming.JSON_SERIALIZE)
    private String sexValue;

    private Integer statusCode;

    @Mapping(translator = "dict", mapper = "statusCode",other = "status")
    private String statusValue;

    @Mapping(translator = "str", mapper = "userId",other = "true")
    private String str1;

    @Mapping(translator = "str", mapper = "userId",other = "false")
    private String str2;
}
