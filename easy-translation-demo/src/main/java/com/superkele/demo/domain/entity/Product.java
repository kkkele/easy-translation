package com.superkele.demo.domain.entity;


import lombok.Data;

@Data
public class Product {

    private Integer productId;

    private String productName;

    private Integer catId;

    private Integer createBy;

}
