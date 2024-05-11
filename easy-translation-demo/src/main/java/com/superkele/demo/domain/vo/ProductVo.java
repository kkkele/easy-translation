package com.superkele.demo.domain.vo;

import com.superkele.demo.domain.entity.Product;
import com.superkele.translation.annotation.Mapping;
import lombok.Data;

@Data
public class ProductVo extends Product {


    @Mapping(translator = "getUser", mapper = "createBy",receive = "nickName")
    private String nickName;

    @Mapping(translator = "getUser", mapper = "createBy", receive = "username")
    private String username;

    @Mapping(translator = "getCatName", mapper = "catId")
    private String catName;

}
