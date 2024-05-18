package com.superkele.demo.domain.vo;


import com.superkele.translation.annotation.Mapping;
import com.superkele.translation.annotation.constant.IgnoreNullPointerExceptionHandler;
import com.superkele.translation.boot.annotation.Translator;
import lombok.Data;

@Data
public class SkuVo {

    Integer skuId;

    String skuName;

    Integer spuId;

    @Mapping(translator = "getSpuName",mapper = "spuId",async = true)
    Integer spuName;

    Integer createBy;

    @Mapping(translator = "getUser",mapper = "createBy",async = true,receive = "nickName")
    String createName;

    @Mapping(translator = "getDeptId",mapper = "createBy",async = true)
    Integer deptId;

    @Mapping(translator = "getDeptName",mapper = "deptId",async = true,after = "deptId",nullPointerHandler = IgnoreNullPointerExceptionHandler.class)
    String deptName;

    @Mapping(translator = "getDesc",mapper = {"createName","deptName"},after = {"createName","deptName"})
    String desc;


}
