package com.superkele.demo.processor;

import com.superkele.translation.annotation.Mapper;
import com.superkele.translation.annotation.Mapping;
import lombok.Data;

@Data
public class ProductVo2 {

    private Integer productId;

    private String productName;

    @Mapping(translator = "getTypeId", mappers = @Mapper("productId"), sort = 0)
    private Integer typeId;

    @Mapping(translator = "getTypeById", mappers = @Mapper("typeId"), sort = 1, receive = "typeName")
    private String typeName;
}
