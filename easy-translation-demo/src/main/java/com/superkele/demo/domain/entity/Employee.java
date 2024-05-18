package com.superkele.demo.domain.entity;


import com.superkele.translation.annotation.Mapping;
import lombok.Data;

@Data
public class Employee {

    private Integer employeeId;

    private String employeeName;

    @Mapping(translator = "getDeptId",mapper = "employeeId",sort = 1)
    private Integer deptId;

    @Mapping(translator = "getDeptName",mapper = "deptId",sort = 2)
    private String deptName;
}
