package com.superkele.demo.test._09json_serialize;


import cn.hutool.core.collection.ListUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.superkele.demo.EasyTranslationDemoApplication;
import com.superkele.demo.jackson_serializer.JsonVo;
import com.superkele.demo.jackson_serializer.JsonVo2;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Repeat;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootTest(classes = {EasyTranslationDemoApplication.class})
@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
public class ObjectMapperTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void test_simple_json_serialize() throws JsonProcessingException {
        JsonVo jsonVo = new JsonVo();
        jsonVo.setCardType(1);
        String jsonStr = objectMapper.writeValueAsString(jsonVo);
        System.out.println(jsonStr);
    }

    @Test
    @Repeat
    public void test_batch_json_serialize() throws JsonProcessingException {
        R<JsonVo> r = new R<>();
        r.setCode(200);
        JsonVo jsonVo1 = new JsonVo();
        jsonVo1.setCardType(1);
        r.setData(jsonVo1);
        JsonVo jsonVo2 = new JsonVo();
        jsonVo2.setCardType(2);
        JsonVo jsonVo3 = new JsonVo();
        jsonVo3.setCardType(3);
        List<JsonVo> jsonVos = ListUtil.of(jsonVo2, jsonVo3);
        r.setDataList(jsonVos);
        r.setDataArr(new Object[]{jsonVo2, jsonVo3});
        r.setDataSet(new HashSet<>(jsonVos));
        String jsonStr = objectMapper.writeValueAsString(r);
        System.out.println(jsonStr);
    }


    @Data
    public static class R<T> {
        int code;
        T data;
        List<T> dataList;
        Object[] dataArr;
        Set<T> dataSet;
    }
}
