package com.superkele.demo.processor;


import cn.hutool.core.date.DateUtil;
import com.superkele.translation.annotation.Mapper;
import com.superkele.translation.annotation.Mapping;
import com.superkele.translation.boot.annotation.Translator;
import lombok.Data;

@Data
public class ProductVo {

    private Integer productId;

    private String productName;

    private Integer typeId;

    @Mapping(translator = "getTypeById", mappers = @Mapper("typeId"), receive = "typeName")
    private String typeName;


}
