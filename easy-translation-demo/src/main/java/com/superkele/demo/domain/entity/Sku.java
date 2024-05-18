package com.superkele.demo.domain.entity;


import lombok.Data;

@Data
public class Sku {

    Integer skuId;

    String skuName;

    Integer spuId;

    Integer createBy;
}
