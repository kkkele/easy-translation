package com.superkele.demo.processor;

import com.superkele.translation.annotation.Mapping;
import com.superkele.translation.annotation.RefTranslation;
import lombok.Data;

@Data
public class ProductVo5 {

    private Integer productId;

    private String productName;

    private Integer typeId;

    @Mapping(translator = "getTypeById", mapper = "typeId", receive = "typeName")
    private String typeName;

    @RefTranslation
    @Mapping
    private ProductVo5 child;
}
