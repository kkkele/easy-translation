package com.superkele.demo.resulthandler;

import com.superkele.translation.annotation.Mapper;
import com.superkele.translation.annotation.Mapping;
import com.superkele.translation.annotation.constant.MappingStrategy;
import lombok.Data;

@Data
public class BookVo2 {

    static int count = 0;

    private Integer id = ++count;

    @Mapping(translator = "getBookList", strategy = MappingStrategy.BATCH, mappers = @Mapper(value = "id"), receive = "bookName")
    private String bookName0;

    @Mapping(translator = "getBookArr", strategy = MappingStrategy.BATCH, mappers = @Mapper(value = "id"), receive = "bookName")
    private String bookName1;

    @Mapping(translator = "getBookSet", strategy = MappingStrategy.BATCH, mappers = @Mapper(value = "id"), groupKey = "id", receive = "bookName")
    private String bookName2;

    @Mapping(translator = "getBookMap", strategy = MappingStrategy.BATCH, mappers = @Mapper(value = "id"), groupKey = "id", receive = "bookName")
    private String bookName3;
}
