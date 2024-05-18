package com.superkele.demo.domain.vo;

import com.superkele.demo.domain.entity.Product;
import com.superkele.translation.annotation.Mapping;
import lombok.Data;

@Data
public class ProductVo extends Product {


    @Mapping(translator = "getUser", mapper = "createBy", receive = "nickName",async = true)
    private String nickName;

    @Mapping(translator = "getUser", mapper = "createBy", receive = "username",async = true)
    private String username;

    @Mapping(translator = "getCatName", mapper = "catId",async = true)
    private String catName;

}
