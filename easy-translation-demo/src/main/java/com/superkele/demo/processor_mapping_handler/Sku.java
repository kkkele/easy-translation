package com.superkele.demo.processor_mapping_handler;


import com.superkele.translation.annotation.Mapper;
import com.superkele.translation.annotation.Mapping;
import com.superkele.translation.annotation.MappingHandler;
import com.superkele.translation.annotation.constant.MappingStrategy;
import com.superkele.translation.core.constant.TranslationConstant;
import lombok.Data;

@Data
public class Sku {

    private Integer skuId;

    private String skuName;

    @Mapping(translator = "getSales", async = true, mappers = @Mapper("skuId"))
    private Integer sales;

    private Integer spuId;

    @Mapping(translator = "getSpuByIds",
            async = true,
            after = "sales",
            strategy = MappingStrategy.BATCH_MAPPING,
            groupKey = "id",
            mappers = @Mapper("spuId"),
            receive = "spuName")
    private String spuName;
}
