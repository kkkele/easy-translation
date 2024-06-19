package com.superkele.demo.domain.vo;

import com.superkele.demo.domain.entity.Product;
import com.superkele.translation.annotation.Mapping;
import com.superkele.translation.core.constant.TranslationConstant;
import lombok.Data;
import org.apache.catalina.User;

@Data
public class ProductVo extends Product {


    @Mapping(translator = "getUser", mapper = "createBy", receive = "nickName")
    private String nickName;

    @Mapping(translator = "getUsers", mapper = "createBy:userId", mappingHandler = TranslationConstant.MANY_TO_MANY_MAPPING_HANDLER,receive = "username")
    private String username;

    @Mapping(translator = "getCatName", mapper = "catId",async = true)
    private String catName;


}
