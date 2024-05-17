package com.superkele.demo.domain.vo;


import com.superkele.demo.domain.entity.Product;
import com.superkele.translation.annotation.Mapping;
import com.superkele.translation.annotation.constant.IgnoreNullPointerExceptionHandler;
import lombok.Data;

@Data
public class ProductVoV2 {

    private Product product;

    @Mapping(translator = "getUser", mapper = "product.createBy", receive = "nickName", nullPointerHandler = IgnoreNullPointerExceptionHandler.class)
    private String nickName;

    @Mapping(translator = "getUser", mapper = "product.createBy", receive = "username", nullPointerHandler = IgnoreNullPointerExceptionHandler.class)
    private String username;

    @Mapping(translator = "getCatName", mapper = "product.catId", nullPointerHandler = IgnoreNullPointerExceptionHandler.class)
    private String catName;
}
