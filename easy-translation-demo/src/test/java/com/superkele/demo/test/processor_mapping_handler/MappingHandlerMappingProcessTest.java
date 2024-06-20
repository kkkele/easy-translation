package com.superkele.demo.test.processor_mapping_handler;


import com.superkele.demo.EasyTranslationDemoApplication;
import com.superkele.demo.processor_mapping_handler.Order;
import com.superkele.translation.annotation.TranslationExecute;
import com.superkele.translation.core.processor.support.DefaultTranslationProcessor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 重点测试对对象处理时，映射处理器的调用
 *
 * @see com.superkele.translation.core.mapping.MappingHandler
 */
@SpringBootTest(classes = {EasyTranslationDemoApplication.class, MappingHandlerMappingProcessTest.Service.class})
@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
public class MappingHandlerMappingProcessTest {

    @Autowired
    private DefaultTranslationProcessor defaultTranslationProcessor;

    @Autowired
    private Service service;

    @Test
    public void testOneToOneMappingHandler() {
        List<Order> mainOrder = service.getMainOrder();
        mainOrder.forEach(order -> {
            System.out.println(order);
            Assert.assertNotNull(order.getOrderNo());
            Assert.assertNotNull(order.getCreateTime());
        });
    }


    @Component
    public static class Service {

        @TranslationExecute(type = Order.class)
        public List<Order> getMainOrder() {
            return IntStream.range(1, 10)
                    .mapToObj(i -> {
                        Order order = new Order();
                        order.setId(i);
                        return order;
                    })
                    .collect(Collectors.toList());
        }
    }
}
