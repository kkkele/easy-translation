package com.superkele.demo.jackson_serializer;


import cn.hutool.core.collection.ListUtil;
import com.superkele.translation.boot.annotation.Translator;
import lombok.Data;

import java.util.List;
import java.util.Random;

@Data
public class Dept {

    private Integer deptId = new Random().nextInt(10);

    private String deptName = "部门" + deptId;

    private List<Dept> childDept;

    @Translator("getDeptById")
    public static Dept getDeptById(Integer deptId) {
        Dept dept = new Dept();
        dept.setDeptId(deptId);
        dept.setDeptName("deptName" + deptId);
        dept.setChildDept(ListUtil.of(new Dept(), new Dept(), new Dept()));
        return dept;
    }
}
