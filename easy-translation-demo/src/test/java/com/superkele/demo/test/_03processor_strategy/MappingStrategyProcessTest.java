package com.superkele.demo.test._03processor_strategy;


import com.superkele.demo.EasyTranslationDemoApplication;
import com.superkele.demo.processor_mapping_strategy.DictVo2;
import com.superkele.demo.processor_mapping_strategy.Order;
import com.superkele.demo.processor_mapping_strategy.Sku;
import com.superkele.translation.annotation.Mapping;
import com.superkele.translation.annotation.TranslationExecute;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.Repeat;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 重点测试对对象处理时，批量和单挑策略的执行结果
 * 顺便提供@TranslationExecute的使用参考
 * @see Mapping#strategy()
 * @see TranslationExecute
 */
@SpringBootTest(classes = {EasyTranslationDemoApplication.class, MappingStrategyProcessTest.Service.class})
@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
public class MappingStrategyProcessTest {


    @Autowired
    private Service service;

    /**
     * 测试 单mapper参数_同步场景下  批量策略是否正常执行
     *
     * @see com.superkele.demo.processor_mapping_strategy.Order#getOrderNo()
     * @see com.superkele.demo.processor_mapping_strategy.OrderService#getOrders(List)
     */
    @Test
    @Repeat(2)
    public void test_one_mapper_sync_batch_process() {
        List<Order> mainOrder = service.getMainOrder();
        mainOrder.forEach(order -> {
            Assert.assertNotNull(order.getOrderNo());
            Assert.assertNotNull(order.getCreateTime());
        });
    }

    /**
     * 测试 单mapper参数_异步场景下 批量策略是否正常执行
     *
     * @see com.superkele.demo.processor_mapping_strategy.Sku
     * @see com.superkele.demo.processor_mapping_strategy.SpuService#getSpuById(List)
     */
    @Test
    public void test_one_mapper_async_batch_process() {
        service.getMainSkuInfo().forEach(sku -> {
            Assert.assertNotNull(sku.getSales());
            Assert.assertNotNull(sku.getSpuName());
        });
    }


    /**
     * 测试 多mapper参数下 批量策略是否正常执行
     * @see com.superkele.demo.processor_mapping_strategy.DictVo2
     * @see com.superkele.demo.processor_mapping_strategy.DictVo2#convertToValue(List,List)
     */
    @Test
    public void test_multi_mapper_async_batch_process() {
        List<DictVo2> dictList = service.getDictList();
        dictList.forEach(dictVo2 -> Assert.assertNotNull(dictVo2.getDictValue()));
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

        @TranslationExecute(type = Sku.class)
        public List<Sku> getMainSkuInfo() {
            return IntStream.range(1, 10)
                    .mapToObj(i -> {
                        Sku order = new Sku();
                        order.setSkuId(i);
                        order.setSkuName("sku" + i);
                        order.setSpuId(new Random().nextBoolean() ? null : 1);
                        return order;
                    })
                    .collect(Collectors.toList());
        }

        @TranslationExecute(type = DictVo2.class)
        public List<DictVo2> getDictList() {
            return IntStream.range(1, 10)
                    .mapToObj(i -> {
                        DictVo2 dictVo = new DictVo2();
                        dictVo.setDictType(new Random().nextBoolean() ? "sex" : "status");
                        dictVo.setDictCode(new Random().nextInt(2));
                        return dictVo;
                    })
                    .collect(Collectors.toList());
        }
    }
}
