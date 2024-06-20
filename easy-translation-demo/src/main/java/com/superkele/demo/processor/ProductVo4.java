package com.superkele.demo.processor;

import com.superkele.translation.annotation.Mapping;
import lombok.Data;


@Data
public class ProductVo4 {

    private Integer productId;

    private String productName;

    private Integer typeId;

    @Mapping(translator = "getTypeById", mapper = "typeId",receive = "typeName",notNullMapping = true)
    private String typeName;
}
