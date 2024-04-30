package com.superkele.translation.test.asynctest;


import com.superkele.translation.annotation.Mapping;
import lombok.Data;
import org.junit.Test;

public class AsyncTest {

    @Test
    public void test() {

    }

    @Data
    public static class User {
        private Integer userId;
        private String userName;
    }

    @Data
    public static class Product {
        private Integer productId;
        private String productName;
        private Integer catId;
        @Mapping(translator = "getCatById", mapper = "catId", receive = "catName", sort = 0, async = true)
        private String catName;
        @Mapping(translator = "getShopByCatId", mapper = "catId", receive = "shopId", sort = 0, async = true)
        private Integer shopId;
        @Mapping(translator = "getShopById", mapper = "shopId", receive = "shopName", sort = 1,async = true)
        private String shopName;
        private Integer createBy;
        @Mapping(translator = "getUserById",mapper = "createBy",receive = "userName")
        private String createName;
    }

    @Data
    public static class Shop {
        private Integer shopId;

        private String shopName;
    }

    @Data
    public static class Category {
        private Integer catId;

        private String catName;
    }
}
