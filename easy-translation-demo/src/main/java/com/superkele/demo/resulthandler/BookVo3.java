package com.superkele.demo.resulthandler;

import com.superkele.translation.annotation.Mapper;
import com.superkele.translation.annotation.Mapping;
import com.superkele.translation.annotation.constant.MappingStrategy;
import lombok.Data;

@Data
public class BookVo3 {

    private Integer id;

    @Mapping(translator = "getBookList",
            strategy = MappingStrategy.BATCH,
            mappers = @Mapper(value = "id"),
            resultHandler = "@bookResultHandler",
            receive = "bookName")
    private String bookName;
}
