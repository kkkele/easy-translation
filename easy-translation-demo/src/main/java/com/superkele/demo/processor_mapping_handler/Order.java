package com.superkele.demo.processor_mapping_handler;


import com.superkele.translation.annotation.Mapping;
import com.superkele.translation.annotation.MappingHandler;
import com.superkele.translation.core.constant.TranslationConstant;
import lombok.Data;

@Data
public class Order {

    private Integer id;

    @Mapping(translator = "getOrdersByIds", mappingHandler =@MappingHandler(TranslationConstant.MANY_TO_MANY_MAPPING_HANDLER), mapper = "id",receive = "orderNo")
    private String orderNo;

    @Mapping(translator = "getOrdersByIds", mappingHandler =@MappingHandler(TranslationConstant.MANY_TO_MANY_MAPPING_HANDLER), mapper = "id",receive = "createTime")
    private String createTime;

}
