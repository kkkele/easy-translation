package com.superkele.translation.test.processor.service;

import com.superkele.translation.annotation.Translation;
import com.superkele.translation.test.processor.entity.Shop;

import java.util.concurrent.TimeUnit;

public class ShopService {

    @Translation(name = "shop_id_to_shop")
    public Shop getShop(Integer id){
        try {
            TimeUnit.MILLISECONDS.sleep(5);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return new Shop(id, "天下第" + id +" shop");
    }
}
