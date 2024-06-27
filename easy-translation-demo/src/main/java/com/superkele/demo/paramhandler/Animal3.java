package com.superkele.demo.paramhandler;

import com.superkele.translation.annotation.Mapper;
import com.superkele.translation.annotation.Mapping;
import com.superkele.translation.annotation.constant.MappingStrategy;
import lombok.Data;

import java.util.Random;

@Data
public class Animal3 {

    static int counter = 0;

    private Integer id;

    private Integer typeId = ++counter;

/*    @Mapping(translator = "getByTypeIdList", mappers = @Mapper(value = "typeId"))
    private String typeName1;*/


    @Mapping(translator = "getByTypeIdList",strategy = MappingStrategy.BATCH, mappers = @Mapper(value = "typeId"))
    private String typeName2;

    @Mapping(translator = "getByTypeIdArr",strategy = MappingStrategy.BATCH, mappers = @Mapper(value = "typeId"))
    private String typeName3;

    @Mapping(translator = "getByTypeIdSet",strategy = MappingStrategy.BATCH, mappers = @Mapper(value = "typeId"))
    private String typeName4;
}
