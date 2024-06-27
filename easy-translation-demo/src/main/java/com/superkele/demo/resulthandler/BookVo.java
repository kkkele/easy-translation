package com.superkele.demo.resulthandler;

import com.superkele.translation.annotation.Mapper;
import com.superkele.translation.annotation.Mapping;
import lombok.Data;

@Data
public class BookVo {

    private Integer id;

    @Mapping(translator = "getBookList", mappers = @Mapper(value = "id"), groupKey = "id",receive = "bookName")
    private String bookName0;

    @Mapping(translator = "getBookArr", mappers = @Mapper(value = "id"), groupKey = "id",receive = "bookName")
    private String bookName1;

    @Mapping(translator = "getBookSet", mappers = @Mapper(value = "id"), groupKey = "id",receive = "bookName")
    private String bookName2;

    @Mapping(translator = "getBookMap", mappers = @Mapper(value = "id"), groupKey = "id",receive = "bookName")
    private String bookName3;
}
