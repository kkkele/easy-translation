package com.superkele.demo.domain.vo;


import com.superkele.translation.annotation.Mapping;
import lombok.Data;

@Data
public class UserVO {

    private Integer userId;

    private String userName;

    @Mapping(translator = "introduce", mapper = {"userId","userName"})
    private String sentence;
}
