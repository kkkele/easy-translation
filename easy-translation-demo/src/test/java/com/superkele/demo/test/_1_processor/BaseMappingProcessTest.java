package com.superkele.demo.test._1_processor;

import com.superkele.demo.processor.*;
import com.superkele.demo.test.utils.PropertyUtils;
import com.superkele.translation.core.processor.support.DefaultTranslationProcessor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Repeat;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Random;

/**
 * 包含了对 @Mapping注解 和 TranslationProcessor 的基本使用
 * 包括 mapper字段,other字段,async字段,after字段，notNullMapping字段的基本使用
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
public class BaseMappingProcessTest {

    @Autowired
    private DefaultTranslationProcessor defaultTranslationProcessor;

    @BeforeEach
    public void beforeEachMethod() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * 测试 单mapper参数翻译
     * @see com.superkele.demo.processor.ProductVo
     */
    @Test
    @Repeat(10)
    public void testBaseMapping() {
        ProductVo productVo = new ProductVo();
        productVo.setProductId(1);
        productVo.setProductName("可乐");
        productVo.setTypeId(new Random().nextInt(5));
        process(productVo);
        if (productVo.getTypeId() >= 1 && productVo.getTypeId() <= 3) {
            Assert.assertNotNull(productVo.getTypeName());
        } else {
            Assert.assertNull(productVo.getTypeName());
        }
    }


    /**
     * 测试 排序映射是否满足要求
     * @see com.superkele.demo.processor.ProductVo2
     */
    @Test
    @Repeat(10)
    public void testSortMapping() {
        ProductVo2 productVo = new ProductVo2();
        int productId = new Random().nextInt(100);
        productVo.setProductId(productId);
        productVo.setProductName("商品" + productId);
        process(productVo);
        Assert.assertNotNull(productVo.getTypeId());
        Assert.assertNotNull(productVo.getTypeName());
    }

    /**
     * 测试 异步翻译
     * @see com.superkele.demo.processor.ProductVo3
     */
    @Test
    @Repeat(100)
    public void testAsyncMapping() {
        ProductVo3 productVo = new ProductVo3();
        int productId = new Random().nextInt(100);
        productVo.setProductId(productId);
        productVo.setProductName("商品" + productId);
        process(productVo);
        Assert.assertNotNull(productVo.getTypeId());
        Assert.assertNotNull(productVo.getTypeName());
        Assert.assertNotNull(productVo.getCurrentTime());
    }

    /**
     * 测试 多参数翻译是否生效 （包括 mapper和other的任意组合）
     *
     * @see com.superkele.demo.processor.DemoEntity
     * 在@Translator标记的方法里，添加了@TransMapper注的参数将会成为mapper字段，其余不添加的将会成为other字段
     * 注意，当方法中没有任何参数添加@TransMapper，且没有任何参数添加@TransOther时，会默认指定第一个参数为mapper字段,
     * mapper字段即获取对象的mapper属性的值，other字段为补充条件，直接传递给方法。
     */
    @Test
    @Repeat(5)
    public void testMultiParamMapping() {
        DemoEntity demoEntity = new DemoEntity();
        process(demoEntity);
        PropertyUtils.getProperties(demoEntity).forEach((k, v) -> {
            Assert.assertNotNull(v);
        });
    }


    /**
     * 测试 notNullMapping 在关闭notNullMapping后，当对象原本就有值时，是否执行翻译
     * @see com.superkele.demo.processor.ProductVo4
     */
    @Test
    public void testNotNullMapping() {
        //-----------这是typeName字段关闭了notNullMapping,属性不为空时不映射的测试-----------
        ProductVo notNullMappingFalse = new ProductVo();
        notNullMappingFalse.setProductId(1);
        notNullMappingFalse.setProductName("可乐");
        notNullMappingFalse.setTypeId(2);
        notNullMappingFalse.setTypeName("random");
        process(notNullMappingFalse);
        Assert.assertEquals(notNullMappingFalse.getTypeName(),"random");
        //-----------这是typeName字段开启了notNullMapping,属性不为空也映射的测试-----------
        ProductVo4 notNullMappingTrue = new ProductVo4();
        notNullMappingTrue.setProductId(1);
        notNullMappingTrue.setProductName("可乐");
        notNullMappingTrue.setTypeId(2);
        notNullMappingTrue.setTypeName("random");
        process(notNullMappingTrue);
        Assert.assertNotEquals(notNullMappingTrue.getTypeName(),"random");
    }


    private void process(Object obj) {
        log.info("beforeProcess:{}", obj);
        defaultTranslationProcessor.process(obj);
        log.info("afterProcess:{}", obj);
    }

}
