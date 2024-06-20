package com.superkele.demo.processor_mapping_handler;


import com.superkele.translation.annotation.Mapping;
import com.superkele.translation.core.constant.TranslationConstant;
import lombok.Data;

@Data
public class Sku {

    private Integer skuId;

    private String skuName;

    @Mapping(translator = "getSales", async = true, mapper = "skuId")
    private Integer sales;

    private Integer spuId;

    @Mapping(translator = "getSpuByIds",async = true ,after = "sales", mappingHandler = TranslationConstant.MANY_TO_MANY_MAPPING_HANDLER, mapper = "spuId:id", receive = "spuName")
    private String spuName;
}
