package com.superkele.demo.domain.vo;


import com.superkele.translation.annotation.Mapping;
import lombok.Data;

@Data
public class TestVo {

    private Integer id;

    @Mapping(translator = "getStrV2", mapper = "id",other = "1")
    private String user0;

    @Mapping(translator = "getStrV3", mapper = "id",other = {"1","2"})
    private String user1;

    @Mapping(translator = "getStrV4", mapper = "id",other = {"1","false"})
    private String user2;



}
