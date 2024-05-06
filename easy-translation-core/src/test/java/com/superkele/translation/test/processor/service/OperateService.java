package com.superkele.translation.test.processor.service;

import com.superkele.translation.annotation.Translation;
import com.superkele.translation.test.processor.entity.Operate;

import java.util.Random;

public class OperateService {


    @Translation(name = "operate_id_to_operate")
    public Operate getOperate(Integer id) {
        Operate operate = new Operate();
        operate.setId(id);
        operate.setName("operate" + id);
        operate.setUserId(new Random().nextInt(1, 3));
        return operate;
    }

    @Translation(name = "operate_name_to_desc")
    public String convertOperateName(String name) {
        return "解析后的" + name;
    }
}
