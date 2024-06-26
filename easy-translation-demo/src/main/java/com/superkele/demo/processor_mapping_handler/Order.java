package com.superkele.demo.processor_mapping_handler;


import com.superkele.translation.annotation.Mapper;
import com.superkele.translation.annotation.Mapping;
import com.superkele.translation.annotation.MappingHandler;
import com.superkele.translation.annotation.constant.MappingStrategy;
import com.superkele.translation.core.constant.TranslationConstant;
import lombok.Data;

@Data
public class Order {

    private Integer id;

    @Mapping(translator = "getOrdersByIds", strategy = MappingStrategy.BATCH_MAPPING, mappers = @Mapper("id"), receive = "orderNo")
    private String orderNo;

    @Mapping(translator = "getOrdersByIds", strategy = MappingStrategy.BATCH_MAPPING, mappers = @Mapper("id"), receive = "createTime")
    private String createTime;

}
