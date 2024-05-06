package com.superkele.translation.test.processor.entity;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {

    private Integer id;

    private String username;

    private Integer shopId;
}
