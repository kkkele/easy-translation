package com.superkele.demo.test.processor_mapping_handler;


import com.superkele.demo.EasyTranslationDemoApplication;
import com.superkele.demo.processor_mapping_handler.DictVo;
import com.superkele.demo.processor_mapping_handler.DictVo2;
import com.superkele.demo.processor_mapping_handler.Order;
import com.superkele.demo.processor_mapping_handler.Sku;
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
import java.util.Random;
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
    private Service service;

    /**
     * 测试 同步场景下 多对多映射器的调用
     */
    @Test
    public void testSyncManyToManyMappingHandler() {
        List<Order> mainOrder = service.getMainOrder();
        mainOrder.forEach(order -> {
            System.out.println(order);
            Assert.assertNotNull(order.getOrderNo());
            Assert.assertNotNull(order.getCreateTime());
        });
    }

    /**
     * 测试 异步场景下 多对多映射器的调用
     */
    @Test
    public void testAsyncManyToManyMappingHandler() {
        service.getMainSkuInfo().forEach(sku -> {
            System.out.println(sku);
            Assert.assertNotNull(sku.getSales());
            Assert.assertNotNull(sku.getSpuName());
        });
    }

    /**
     * 测试 多mapper参数下 多对多映射器的调用
     */
    @Test
    public void testMultiMapper_ManyToManyMappingHandler() {
        DictVo dict = service.getDict();
        System.out.println(dict);
        Assert.assertNotNull(dict.getDictValue());
        List<DictVo2> dictList = service.getDictList();
        dictList.forEach(dictVo2 -> {
            System.out.println(dictVo2);
            Assert.assertNotNull(dictVo2.getDictValue());
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

        @TranslationExecute(type = DictVo.class)
        public DictVo getDict() {
            DictVo dictVo = new DictVo();
            dictVo.setDictType("sex");
            dictVo.setDictCode(0);
            return dictVo;
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
