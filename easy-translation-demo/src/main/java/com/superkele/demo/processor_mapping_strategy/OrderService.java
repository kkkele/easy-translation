package com.superkele.demo.processor_mapping_strategy;

import cn.hutool.core.date.DateUtil;
import com.superkele.translation.boot.annotation.Translator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {


    @Translator("getOrdersByIds")
    public List<Order> getOrders(List<Integer> ids) {
       return   ids.stream()
                .map(id -> {
                    Order order = new Order();
                    order.setId(id);
                    order.setOrderNo(UUID.randomUUID().toString().substring(0, 8));
                    order.setCreateTime(DateUtil.now());
                    return order;
                })
                .collect(Collectors.toList());

    }
}
