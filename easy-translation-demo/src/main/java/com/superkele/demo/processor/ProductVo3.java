package com.superkele.demo.processor;

import cn.hutool.core.date.DateUtil;
import com.superkele.translation.annotation.Mapping;
import com.superkele.translation.boot.annotation.Translator;
import lombok.Data;


@Data
public class ProductVo3 {

    private Integer productId;

    private String productName;

    @Mapping(translator = "getTypeId", async = true, mapper = "productId", sort = 0)
    private Integer typeId;

    @Mapping(translator = "getTypeById",  async = true,after = "typeId", mapper = "typeId",receive = "typeName")
    private String typeName;

    @Mapping(translator = "getCurrentTime", async = true,sort = 0)
    private String currentTime;


    @Translator("getCurrentTime")
    public static String currentTime() {
        return DateUtil.now();
    }
}
