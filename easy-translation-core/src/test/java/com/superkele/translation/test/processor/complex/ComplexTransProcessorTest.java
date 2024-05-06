package com.superkele.translation.test.processor.complex;

import com.superkele.translation.annotation.Mapping;
import lombok.Data;

public class ComplexTransProcessorTest {


    /**
     * userId先查，在获取userId对应的shop，然后渲染出shopName
     * 同时，并发的执行其他操作
     */
    @Data
    public static class ComplexOperateVO {

        private Integer operateId;

        @Mapping(translator = "operate_id_to_operate", mapper = "operateId", sort = 0)
        private String operateName;

        @Mapping(translator = "operate_name_to_desc", mapper = "operateName", sort = 1)
        private String operateDesc;

        @Mapping(translator = "operate_id_to_operate", mapper = "operateId", sort = 0)
        private Integer userId;

        @Mapping(translator = "user_id_to_user", mapper = "userId", receive = "username", after = "userId", async = true)
        private String username;

        @Mapping(translator = "user_id_to_user", mapper = "userId", receive = "shopId", after = "userId", async = true)
        private Integer shopId;

        @Mapping(translator = "shop_id_to_shop", mapper = "shopId", receive = "shopName", after = "shopId", async = true)
        private String shopName;
    }
}
